"""
local_api.py — Local HTTP API (localhost:8080).

Serves the kiosk UI static files at GET / and exposes hardware state
endpoints so the UI can poll live data.  Bound to 127.0.0.1 only.

Endpoints:
  GET  /                        — kiosk UI (index.html)
  GET  /health                  — liveness check
  GET  /status                  — full hardware state snapshot
  GET  /sensors                 — latest sensor readings
  POST /simulate/start          — STUB only: start simulated flow
  POST /simulate/stop           — STUB only: stop simulated flow
  POST /simulate/trip_switch    — STUB only: trip limit switch
  POST /simulate/reset_switch   — STUB only: reset limit switch
"""

import json
import logging
import os
from typing import TYPE_CHECKING

from flask import Flask, Response, jsonify, request, abort, send_from_directory

if TYPE_CHECKING:
    from hardware_manager import HardwareManager

logger = logging.getLogger(__name__)

# Kiosk UI static files live next to this package
_UI_DIR = os.path.join(os.path.dirname(__file__), "..", "..", "kiosk_ui")
_UI_DIR = os.path.abspath(_UI_DIR)


def create_local_api(manager: "HardwareManager") -> Flask:
    """
    Build and return a Flask app with the diagnostics API wired to `manager`.
    Start it with: app.run(host='127.0.0.1', port=cfg.diag_api_port)
    """
    app = Flask(__name__, static_folder=None)
    app.config["manager"] = manager

    # ── Kiosk UI static files ──────────────────────────────────────────────

    @app.get("/")
    def kiosk_root():
        return send_from_directory(_UI_DIR, "index.html")

    @app.get("/static/<path:filename>")
    def kiosk_static(filename):
        return send_from_directory(os.path.join(_UI_DIR, "static"), filename)

    # ── Liveness ──────────────────────────────────────────────────────────────

    @app.get("/health")
    def health():
        return jsonify({"ok": True, "stationId": manager.cfg.station_id})

    # ── Full state snapshot ───────────────────────────────────────────────────

    @app.get("/status")
    def status():
        snap     = manager.flow_processor.snapshot()
        plc_info = manager.plc.cmd_ping()
        return jsonify({
            "stationId":     manager.cfg.station_id,
            "state":         manager.state,
            "transactionId": snap.transaction_id,
            "flow": {
                "volumeLitres":   snap.volume_litres,
                "flowRateLPM":    snap.flow_rate_lpm,
                "extrusionPct":   snap.extrusion_pct,
                "elapsedSeconds": snap.elapsed_seconds,
            },
            "plc":         plc_info,
            "pumpOn":      manager.relay.pump_is_on,
        })

    # ── PLC diagnostics ───────────────────────────────────────────────────────

    @app.get("/plc")
    def plc_status():
        return jsonify(manager.plc.cmd_ping())

    # ── Sensor readings ───────────────────────────────────────────────────────

    @app.get("/sensors")
    def sensors():
        return jsonify({
            "tankLevelPct": manager.last_tank_level,
            "limitTripped": manager.last_limit_tripped,
            "pulseCount":   manager.pulser.pulse_count,
        })

    # ── Simulation endpoints (STUB only) ─────────────────────────────────────

    @app.post("/simulate/start")
    def sim_start():
        _require_stub(manager)
        manager.pulser.open_stub_valve()
        manager.level_sensor.set_stub_valve_open(True)
        return jsonify({"ok": True, "action": "simulate_start"})

    @app.post("/simulate/stop")
    def sim_stop():
        _require_stub(manager)
        manager.pulser.close_stub_valve()
        manager.level_sensor.set_stub_valve_open(False)
        return jsonify({"ok": True, "action": "simulate_stop"})

    @app.post("/simulate/trip_switch")
    def sim_trip():
        _require_stub(manager)
        manager.limit_switch.set_stub_tripped(True)
        return jsonify({"ok": True, "action": "trip_switch"})

    @app.post("/simulate/reset_switch")
    def sim_reset():
        _require_stub(manager)
        manager.limit_switch.set_stub_tripped(False)
        return jsonify({"ok": True, "action": "reset_switch"})

    # ── Server-Sent Events (real-time flow stream for kiosk UI) ─────────────
    # The kiosk browser connects here with EventSource('/events').
    # Events pushed: 'flow' (each pulse), 'fault', 'complete'.
    # Falls back gracefully to polling if this endpoint is unreachable.

    @app.get("/events")
    def sse_events():
        q = manager.subscribe_sse()

        def generate():
            try:
                while True:
                    try:
                        payload = q.get(timeout=25)
                        event   = payload.pop("event", "message")
                        yield f"event: {event}\ndata: {json.dumps(payload)}\n\n"
                    except Exception:  # queue.Empty or generator closed
                        yield ": keepalive\n\n"   # prevent proxy timeout
            finally:
                manager.unsubscribe_sse(q)

        return Response(
            generate(),
            mimetype="text/event-stream",
            headers={
                "Cache-Control":       "no-cache",
                "X-Accel-Buffering":   "no",   # disable nginx buffering
                "Access-Control-Allow-Origin": "*",
            },
        )

    return app


# ── Helpers ───────────────────────────────────────────────────────────────────

def _require_stub(manager: "HardwareManager") -> None:
    if manager.cfg.hardware_mode == "REAL":
        abort(403, description="Simulation endpoints are disabled in REAL hardware mode")

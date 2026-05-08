"""
local_api.py — Local HTTP diagnostics API (localhost:8080).

Mirrors the same endpoint structure as the existing hardware_service/local_api.py
so the station kiosk UI and any local operator tool can query hardware state
without going through the cloud.

Endpoints:
  GET  /health                  — liveness check
  GET  /status                  — full hardware state snapshot
  GET  /sensors                 — latest sensor readings (level, limit, pulser)
  POST /simulate/start          — STUB only: simulate valve open + start flow
  POST /simulate/stop           — STUB only: simulate valve close + stop flow
  POST /simulate/trip_switch    — STUB only: simulate limit switch triggered
  POST /simulate/reset_switch   — STUB only: reset limit switch

Binding: 127.0.0.1 only (never exposed externally).
"""

import logging
from typing import TYPE_CHECKING

from flask import Flask, jsonify, request, abort

if TYPE_CHECKING:
    from hardware_manager import HardwareManager

logger = logging.getLogger(__name__)


def create_local_api(manager: "HardwareManager") -> Flask:
    """
    Build and return a Flask app with the diagnostics API wired to `manager`.
    Start it with: app.run(host='127.0.0.1', port=cfg.diag_api_port)
    """
    app = Flask(__name__)
    app.config["manager"] = manager

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
            "valveOpen":   manager.relay.valve_is_open,
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

    return app


# ── Helpers ───────────────────────────────────────────────────────────────────

def _require_stub(manager: "HardwareManager") -> None:
    if manager.cfg.hardware_mode == "REAL":
        abort(403, description="Simulation endpoints are disabled in REAL hardware mode")

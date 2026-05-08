"""
local_api.py — Flask diagnostics/status API running on localhost.

Provides a lightweight HTTP endpoint so technicians can check station
health without needing cloud access.

Endpoints:
  GET  /health             — liveness check
  GET  /status             — current pump state + station info
  POST /simulate/authorize — (non-production only) trigger a test dispense
  POST /simulate/stop      — (non-production only) force stop
"""

import logging
import os
from flask import Flask, jsonify, request

logger = logging.getLogger(__name__)
app = Flask(__name__)

# Populated by main.py after the controller is initialised
_controller = None


def init_diag_api(controller) -> None:
    global _controller
    _controller = controller


@app.get("/health")
def health():
    return jsonify({"status": "ok"}), 200


@app.get("/status")
def status():
    if not _controller:
        return jsonify({"error": "controller not initialised"}), 503
    return jsonify({
        "stationId": os.getenv("STATION_ID"),
        "state":     _controller._state.name,
        "volume":    round(_controller._volume_dispensed, 3),
    })


@app.post("/simulate/authorize")
def sim_authorize():
    if os.getenv("NODE_ENV") == "production":
        return jsonify({"error": "simulation disabled in production"}), 403
    if not _controller:
        return jsonify({"error": "controller not initialised"}), 503
    data = request.get_json(force=True) or {}
    _controller._handle_authorize({
        "transactionId": data.get("transactionId", "sim-001"),
        "maxVolume":     float(data.get("maxVolume", 10.0)),
        "maxAmount":     float(data.get("maxAmount", 100.0)),
    })
    return jsonify({"ok": True, "authCode": _controller._auth_code})


@app.post("/simulate/stop")
def sim_stop():
    if os.getenv("NODE_ENV") == "production":
        return jsonify({"error": "simulation disabled in production"}), 403
    if not _controller:
        return jsonify({"error": "controller not initialised"}), 503
    _controller._handle_stop({"reason": "SIMULATION_STOP"})
    return jsonify({"ok": True})

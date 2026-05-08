"""
main.py — Entry point for the AceRev hardware gateway service.

Wires together:
  - Hardware interface (stub / GPIO / PLC)
  - MQTT client (AWS IoT Core)
  - Pump controller (state machine)
  - Local diagnostics API (Flask)
  - Heartbeat loop

Run:
  python main.py
"""

import logging
import threading
import sys

import structlog
from config import cfg
from display import PumpDisplay
from local_api import app as flask_app, init_diag_api
from mqtt_client import MQTTClient
from plc_interface import get_hardware
from pump_controller import PumpController


# ── Logging setup ─────────────────────────────────────────────────────────────

def setup_logging() -> None:
    level = getattr(logging, cfg.log_level.upper(), logging.INFO)
    structlog.configure(
        wrapper_class=structlog.make_filtering_bound_logger(level),
        logger_factory=structlog.PrintLoggerFactory(),
    )
    logging.basicConfig(
        level=level,
        format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
    )


# ── Main ──────────────────────────────────────────────────────────────────────

def main() -> None:
    setup_logging()
    log = logging.getLogger("main")

    log.info("=== AceRev Hardware Gateway starting ===")
    log.info("Station : %s (%s)", cfg.station_name, cfg.station_id)
    log.info("HW mode : %s", cfg.hardware_mode)

    # 1. Initialise hardware interface
    try:
        hardware = get_hardware()
    except NotImplementedError as exc:
        log.error("Hardware init failed: %s", exc)
        log.error("Set HARDWARE_MODE=STUB in .env to run without physical hardware.")
        sys.exit(1)

    # 2. Initialise display
    display = PumpDisplay()

    # 3. Initialise MQTT and connect to AWS IoT Core
    mqtt = MQTTClient()
    try:
        mqtt.connect()
    except Exception as exc:
        log.error("MQTT connect failed: %s", exc)
        log.error("Check IOT_CERT_PATH / IOT_KEY_PATH / AWS_IOT_ENDPOINT in .env")
        sys.exit(1)

    # 4. Create the pump controller (registers its own MQTT handlers)
    controller = PumpController(mqtt=mqtt, hardware=hardware, display=display)

    # 5. Start heartbeat loop (background thread)
    controller.run_heartbeat(interval_sec=30)

    # 6. Publish initial IDLE status
    mqtt.publish_status("IDLE")

    # 7. Start local diagnostics API in a background thread
    init_diag_api(controller)
    api_thread = threading.Thread(
        target=lambda: flask_app.run(
            host="0.0.0.0",
            port=cfg.diag_api_port,
            debug=False,
            use_reloader=False,
        ),
        daemon=True,
    )
    api_thread.start()
    log.info("Diagnostics API running on http://0.0.0.0:%d", cfg.diag_api_port)

    # 8. Keep the main thread alive
    log.info("Hardware gateway running. Press Ctrl+C to stop.")
    try:
        while True:
            import time
            time.sleep(1)
    except KeyboardInterrupt:
        log.info("Shutting down...")
        mqtt.disconnect()


if __name__ == "__main__":
    main()

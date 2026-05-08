"""
main.py — Entry point for the ODROID-M2 hardware gateway service.

Starts the HardwareManager (sensors, relays, MQTT) and the local diagnostics
HTTP API in a background thread, then blocks until SIGINT/SIGTERM.

Run:
  python main.py

Run in STUB mode (no hardware):
  HARDWARE_MODE=STUB python main.py
"""

import logging
import signal
import sys
import threading

from config import cfg
from hardware_manager import HardwareManager
from comms import create_local_api


# ── Logging ───────────────────────────────────────────────────────────────────

def _setup_logging() -> None:
    level = getattr(logging, cfg.log_level.upper(), logging.INFO)
    logging.basicConfig(
        level=level,
        format="%(asctime)s [%(levelname)-8s] %(name)s: %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S",
    )


# ── Main ──────────────────────────────────────────────────────────────────────

def main() -> None:
    _setup_logging()
    log = logging.getLogger("main")

    log.info("=" * 60)
    log.info("  ODROID-M2 Hardware Gateway")
    log.info("  Station : %s (%s)", cfg.station_name, cfg.station_id)
    log.info("  Mode    : %s", cfg.hardware_mode)
    log.info("=" * 60)

    manager = HardwareManager(cfg)

    # ── Local HTTP diagnostics API (background thread) ────────────────────────
    flask_app = create_local_api(manager)
    api_thread = threading.Thread(
        target=lambda: flask_app.run(
            host="127.0.0.1",
            port=cfg.diag_api_port,
            debug=False,
            use_reloader=False,
        ),
        daemon=True,
        name="local-api",
    )
    api_thread.start()
    log.info("Local API listening on http://127.0.0.1:%d", cfg.diag_api_port)

    # ── Start hardware ────────────────────────────────────────────────────────
    try:
        manager.start()
    except Exception as exc:
        log.error("Hardware startup failed: %s", exc)
        log.error("Set HARDWARE_MODE=STUB in .env to run without physical hardware.")
        sys.exit(1)

    # ── Wait for shutdown signal ──────────────────────────────────────────────
    shutdown = threading.Event()

    def _handle_signal(sig, frame):
        log.info("Signal %s received — shutting down", sig)
        shutdown.set()

    signal.signal(signal.SIGINT,  _handle_signal)
    signal.signal(signal.SIGTERM, _handle_signal)

    log.info("Running. Press Ctrl+C to stop.")
    shutdown.wait()

    log.info("Stopping hardware manager...")
    manager.stop()
    log.info("Shutdown complete.")


if __name__ == "__main__":
    main()

"""
relay_controller.py — GPIO relay control for the pump motor.

Hardware: 4-Channel Opto-isolated Relay Module (BOM C4, 5V coil).
The M2's 3.3V GPIO triggers the opto-isolator input directly.

The Piusi Suzzarablue AC pump has a built-in bypass valve, so the pump relay
is the SOLE flow-control mechanism.  No solenoid valve relay is needed.

Active-high vs active-low:
  Most Chinese opto-isolated relay boards are ACTIVE-LOW (energise relay by
  pulling the IN pin LOW).  Set PUMP_RELAY_ACTIVE_HIGH=false in .env for those
  boards.  If your relay board energises on HIGH, set to true.

GPIO wiring (ODROID-M2 40-pin → relay board):
  Pump relay IN ──── GPIO line (pump_relay_line)
  Relay board VCC ── 3.3V (or 5V if relay board requires — check board label)
  Relay board GND ── GND

Safety design:
  - Relay defaults to OFF (de-energised) on startup and on any error.

STUB mode: logs relay state changes without touching GPIO.
"""

import logging
import threading
from typing import Optional

logger = logging.getLogger(__name__)


class RelayController:
    """
    Controls the pump motor relay via a single GPIO output line.

    Usage:
        relay = RelayController(cfg)
        relay.open()        # claim GPIO line, default OFF
        relay.pump_on()
        # ... dispense ...
        relay.pump_off()
        relay.close()       # release GPIO, relay forced OFF
    """

    def __init__(self, cfg) -> None:
        self._cfg   = cfg
        self._stub  = cfg.hardware_mode != "REAL"
        self._lock  = threading.Lock()

        # GPIO line handles (REAL mode)
        self._pump_line: Optional[object] = None
        self._chip:      Optional[object] = None

        # State tracking
        self._pump_on_state = False

    # ── Lifecycle ─────────────────────────────────────────────────────────────

    def open(self) -> None:
        """Claim GPIO output line and default pump relay to OFF."""
        if self._stub:
            logger.info("RelayController: STUB mode — GPIO not opened")
            return

        try:
            import gpiod  # type: ignore

            self._chip = gpiod.Chip(self._cfg.gpio_chip)

            self._pump_line = self._chip.get_line(self._cfg.pump_relay_line)
            self._pump_line.request(
                consumer="pump-relay",
                type=gpiod.LINE_REQ_DIR_OUT,
                default_vals=[self._off_value(self._cfg.pump_relay_active_high)],
            )

            logger.info(
                "RelayController: GPIO opened — pump line=%d on %s",
                self._cfg.pump_relay_line,
                self._cfg.gpio_chip,
            )
        except Exception as exc:
            logger.error("RelayController GPIO open failed: %s", exc)
            raise

    def close(self) -> None:
        """Force pump relay OFF and release GPIO line."""
        self._set_pump(False)

        if self._pump_line:
            try:
                self._pump_line.release()
            except Exception:
                pass
        if self._chip:
            try:
                self._chip.close()
            except Exception:
                pass
        self._pump_line = self._chip = None
        logger.info("RelayController: closed — pump relay forced OFF")

    # ── Pump relay ────────────────────────────────────────────────────────────

    def pump_on(self) -> None:
        """Energise the pump motor relay (start motor)."""
        self._set_pump(True)

    def pump_off(self) -> None:
        """De-energise the pump motor relay (stop motor)."""
        self._set_pump(False)

    @property
    def pump_is_on(self) -> bool:
        return self._pump_on_state

    # ── Internal ─────────────────────────────────────────────────────────────

    def _set_pump(self, on: bool) -> None:
        with self._lock:
            self._pump_on_state = on
            if self._stub:
                logger.info("Pump relay: %s [STUB]", "ON" if on else "OFF")
                return
            if self._pump_line:
                try:
                    self._pump_line.set_value(
                        self._on_value(self._cfg.pump_relay_active_high) if on
                        else self._off_value(self._cfg.pump_relay_active_high)
                    )
                    logger.debug("Pump relay GPIO: %s", "ON" if on else "OFF")
                except Exception as exc:
                    logger.error("Pump relay set error: %s", exc)

    @staticmethod
    def _on_value(active_high: bool) -> int:
        return 1 if active_high else 0

    @staticmethod
    def _off_value(active_high: bool) -> int:
        return 0 if active_high else 1

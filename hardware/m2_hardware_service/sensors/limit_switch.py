"""
limit_switch.py — Normally-Closed (NC) limit switch GPIO input reader.

Use cases on the refilling station:
  - Tank-full overflow protection (NC switch mounted at max-fill level)
  - Nozzle-in-holder detection (confirms nozzle is seated before allowing flow)
  - Door/panel interlock (panel must be closed to dispense)

Hardware wiring to ODROID-M2:
  Switch terminal A ──── GPIO line (limit_switch_line) with internal pull-up
  Switch terminal B ──── GND

  NC behaviour:
    Circuit intact (switch NOT tripped) → GPIO reads HIGH (3.3V via pull-up)
    Circuit open   (switch tripped)     → GPIO reads LOW  (connected to GND)

  This is fail-safe: a broken wire is treated the same as a triggered switch.

STUB mode: always returns OK (not tripped) so dispensing can proceed.
"""

import logging
import time
import threading
from typing import Optional

logger = logging.getLogger(__name__)


class LimitSwitch:
    """
    Reads the state of a Normally-Closed limit switch on a GPIO input.

    Usage:
        switch = LimitSwitch(cfg)
        switch.open()
        if switch.is_tripped():
            # abort dispensing
    """

    def __init__(self, cfg) -> None:
        self._cfg    = cfg
        self._stub   = cfg.hardware_mode != "REAL"
        self._line   = None
        self._chip   = None
        self._lock   = threading.Lock()

        # Stub: can be forced tripped for testing
        self._stub_tripped = False

    # ── Public API ────────────────────────────────────────────────────────────

    def open(self) -> None:
        """Claim the GPIO line. Call once at startup."""
        if self._stub:
            logger.info("LimitSwitch: STUB mode — GPIO not opened")
            return

        try:
            import gpiod  # type: ignore

            self._chip = gpiod.Chip(self._cfg.gpio_chip)
            self._line = self._chip.get_line(self._cfg.limit_switch_line)
            self._line.request(
                consumer="limit-switch",
                type=gpiod.LINE_REQ_DIR_IN,
                flags=gpiod.LINE_REQ_FLAG_BIAS_PULL_UP,
            )
            logger.info("LimitSwitch: GPIO opened — line=%d on %s",
                        self._cfg.limit_switch_line, self._cfg.gpio_chip)
        except Exception as exc:
            logger.error("LimitSwitch GPIO open failed: %s", exc)
            raise

    def is_tripped(self) -> bool:
        """
        Return True if the limit switch has been triggered (circuit open).
        A tripped switch means stop dispensing immediately.
        """
        if self._stub:
            return self._stub_tripped

        with self._lock:
            if self._line is None:
                logger.warning("LimitSwitch: line not opened — assuming tripped (safe)")
                return True
            try:
                value = self._line.get_value()
                # NC wiring: HIGH = intact = NOT tripped, LOW = open = TRIPPED
                return value == 0
            except Exception as exc:
                logger.error("LimitSwitch read error: %s — assuming tripped", exc)
                return True     # fail-safe: treat read error as tripped

    def is_ok(self) -> bool:
        """Convenience inverse of is_tripped()."""
        return not self.is_tripped()

    def close(self) -> None:
        """Release GPIO resources."""
        if self._line:
            try:
                self._line.release()
            except Exception:
                pass
        if self._chip:
            try:
                self._chip.close()
            except Exception:
                pass
        self._line = self._chip = None
        logger.info("LimitSwitch: closed")

    # ── Stub helpers ─────────────────────────────────────────────────────────

    def set_stub_tripped(self, tripped: bool) -> None:
        """STUB only: simulate a tripped or reset switch."""
        self._stub_tripped = tripped
        logger.info("LimitSwitch [STUB]: tripped=%s", tripped)


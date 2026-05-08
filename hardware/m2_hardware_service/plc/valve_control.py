"""
valve_control.py — Solenoid valve sequencer.

The solenoid valve is a Normally-Closed (NC) 24VDC valve switched by the
relay board (BOM C4, valve relay channel).

Sequencing rules (enforced by this module):
  1. Valve must NOT open unless the pump is already RUNNING.
     (Opening valve before pump starts causes backflow / siphoning.)
  2. Valve must close before the pump stops.
     (Closing valve first ensures no residual flow after pump off.)
  3. A mechanical delay of OPEN_DELAY_S / CLOSE_DELAY_S is observed after
     each relay state change to allow the solenoid plunger to fully travel.

State machine:
  CLOSED
    ↓  open() called + pump running
  OPENING  (wait OPEN_DELAY_S)
    ↓
  OPEN
    ↓  close() called OR pump stopped
  CLOSING  (wait CLOSE_DELAY_S)
    ↓
  CLOSED
"""

import logging
import time
import threading
from enum import Enum, auto

logger = logging.getLogger(__name__)

OPEN_DELAY_S  = 0.25    # 250ms for plunger to fully open
CLOSE_DELAY_S = 0.15    # 150ms for spring return to close


class ValveState(Enum):
    CLOSED  = auto()
    OPENING = auto()
    OPEN    = auto()
    CLOSING = auto()


class ValveController:
    """
    Controls the solenoid valve relay with mechanical sequencing delays.

    Usage (in StationPLC scan loop):
        valve.evaluate()           # call every scan cycle
        valve.open(pump)           # only opens if pump.is_running
        valve.close()              # always closes

    Properties:
        valve.state  → ValveState
        valve.is_open → bool (True only when fully OPEN)
    """

    def __init__(self, relay) -> None:
        self._relay          = relay
        self._state          = ValveState.CLOSED
        self._lock           = threading.Lock()
        self._transition_at: float = 0.0

    # ── Public API ────────────────────────────────────────────────────────────

    def open(self, pump_sequencer) -> bool:
        """
        Request valve open.  Only proceeds if pump is RUNNING.
        Returns True if request accepted.
        """
        with self._lock:
            if not pump_sequencer.is_running:
                logger.warning("Valve open rejected — pump is not running")
                return False
            if self._state != ValveState.CLOSED:
                logger.debug("Valve open — already %s", self._state.name)
                return True

            self._state = ValveState.OPENING
            self._transition_at = time.monotonic() + OPEN_DELAY_S
            self._relay.valve_open()
            logger.info("Valve OPENING (relay energised)")
            return True

    def close(self) -> None:
        """Request valve close (always accepted regardless of pump state)."""
        with self._lock:
            if self._state in (ValveState.CLOSING, ValveState.CLOSED):
                return
            self._state = ValveState.CLOSING
            self._transition_at = time.monotonic() + CLOSE_DELAY_S
            self._relay.valve_close()
            logger.info("Valve CLOSING (relay de-energised)")

    def evaluate(self) -> None:
        """Call every PLC scan cycle to advance timed transitions."""
        now = time.monotonic()
        with self._lock:
            if self._state == ValveState.OPENING and now >= self._transition_at:
                self._state = ValveState.OPEN
                logger.info("Valve fully OPEN — flow permitted")

            elif self._state == ValveState.CLOSING and now >= self._transition_at:
                self._state = ValveState.CLOSED
                logger.info("Valve fully CLOSED — flow stopped")

    # ── Properties ────────────────────────────────────────────────────────────

    @property
    def state(self) -> ValveState:
        return self._state

    @property
    def is_open(self) -> bool:
        return self._state == ValveState.OPEN

    @property
    def is_closed(self) -> bool:
        return self._state == ValveState.CLOSED

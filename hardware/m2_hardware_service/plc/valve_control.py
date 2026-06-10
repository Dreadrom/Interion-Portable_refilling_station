"""
valve_control.py — No solenoid valve: flow is controlled by pump relay only.

The Piusi Suzzarablue AC pump has a BUILT-IN BYPASS VALVE, which means:
  • Pump ON  → fluid flows through the meter to the nozzle
  • Pump OFF → bypass recirculates internally, flow stops immediately

There is NO separate solenoid valve in this system.
Flow ON/OFF is achieved entirely by starting/stopping the pump relay.
This class is retained as a transparent pass-through so the rest of the
PLC layer (station_plc.py) requires no structural changes.

ValveState tracks the pump's flow state for display/telemetry purposes only:
  CLOSED  — pump is off, no flow
  OPEN    — pump is running, flow active
"""

import logging
from enum import Enum, auto

logger = logging.getLogger(__name__)


class ValveState(Enum):
    CLOSED = auto()   # pump off — no flow
    OPEN   = auto()   # pump on  — flow active


class ValveController:
    """
    No-op valve controller — flow is controlled through PumpSequencer only.
    State mirrors the pump run state for telemetry/status reporting.
    """

    def __init__(self, relay=None) -> None:
        # relay argument retained for API compatibility but unused
        self._state = ValveState.CLOSED

    def open(self, pump_sequencer) -> bool:
        """Mirror pump running state — no relay action needed."""
        if pump_sequencer.is_running:
            self._state = ValveState.OPEN
            logger.debug("ValveController: OPEN (pump is running — bypass pump controls flow)")
            return True
        return False

    def close(self) -> None:
        """Mirror pump stopped state — no relay action needed."""
        self._state = ValveState.CLOSED
        logger.debug("ValveController: CLOSED (pump stopped)")

    def evaluate(self) -> None:
        """No timed transitions required — no-op."""
        pass

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

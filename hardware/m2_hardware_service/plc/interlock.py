"""
interlock.py — Safety interlock chain for the refilling station.

Evaluates all safety conditions every PLC scan cycle and sets a single
`permits_dispense` output bit.  If ANY interlock is faulted, dispensing
is immediately blocked and the active fault codes are published.

Interlocks checked (in priority order):
  1. LIMIT_SWITCH_TRIPPED   — NC limit switch open (overflow / nozzle-out / door)
  2. TANK_LEVEL_EMPTY       — tank level ≤ LOW_LEVEL_PCT threshold
  3. PUMP_DUTY_EXCEEDED     — pump in COOLING_DOWN state (not safe to run)
  4. VALVE_SEQUENCE_FAULT   — valve open while pump not running
  5. BACKFLOW_DETECTED      — MK325 CH2 leads CH1 (reverse flow)

Adding a new interlock:
  1. Add a FaultCode entry.
  2. Add the check inside InterLockChain.evaluate().
  3. That's it — permits_dispense is automatically False when any fault is active.
"""

import logging
from dataclasses import dataclass, field
from enum import Enum, auto
from typing import Optional

logger = logging.getLogger(__name__)

# ── Thresholds ────────────────────────────────────────────────────────────────
LOW_LEVEL_PCT    = 5.0    # % tank level below which dispensing is blocked
LEVEL_FAULT_PCT  = 2.0    # hysteresis — level must rise above this to clear the fault


class FaultCode(Enum):
    LIMIT_SWITCH_TRIPPED  = "LIMIT_SWITCH_TRIPPED"
    TANK_LEVEL_EMPTY      = "TANK_LEVEL_EMPTY"
    PUMP_DUTY_EXCEEDED    = "PUMP_DUTY_EXCEEDED"
    VALVE_SEQUENCE_FAULT  = "VALVE_SEQUENCE_FAULT"
    BACKFLOW_DETECTED     = "BACKFLOW_DETECTED"


@dataclass
class InterLockResult:
    permits_dispense: bool
    active_faults:    list[FaultCode] = field(default_factory=list)

    @property
    def fault_names(self) -> list[str]:
        return [f.value for f in self.active_faults]


class InterLockChain:
    """
    Evaluates all safety interlocks and exposes a single permits_dispense flag.

    Usage (in StationPLC scan loop):
        result = interlocks.evaluate(
            limit_tripped=...,
            tank_level_pct=...,
            pump=pump_sequencer,
            valve=valve_controller,
            pulser=mk325_pulser,
        )
        if not result.permits_dispense:
            # emergency stop
    """

    def __init__(self) -> None:
        self._last_result = InterLockResult(permits_dispense=True)
        self._level_fault_latched = False   # latched until level recovers

    # ── Public API ────────────────────────────────────────────────────────────

    def evaluate(
        self,
        limit_tripped:  bool,
        tank_level_pct: Optional[float],
        pump,           # PumpSequencer
        valve,          # ValveController
        pulser,         # MK325Pulser
    ) -> InterLockResult:
        faults: list[FaultCode] = []

        # ── 1. Limit switch ───────────────────────────────────────────────────
        if limit_tripped:
            faults.append(FaultCode.LIMIT_SWITCH_TRIPPED)

        # ── 2. Tank level (with hysteresis to prevent chatter) ────────────────
        if tank_level_pct is not None:
            if tank_level_pct <= LOW_LEVEL_PCT:
                self._level_fault_latched = True
            elif tank_level_pct > LOW_LEVEL_PCT + LEVEL_FAULT_PCT:
                self._level_fault_latched = False
        else:
            # Cannot read level → assume empty (fail-safe)
            self._level_fault_latched = True

        if self._level_fault_latched:
            faults.append(FaultCode.TANK_LEVEL_EMPTY)

        # ── 3. Pump duty cycle ────────────────────────────────────────────────
        from .pump_sequence import PumpState
        if pump.state == PumpState.COOLING_DOWN:
            faults.append(FaultCode.PUMP_DUTY_EXCEEDED)

        # ── 4. Valve sequence fault ───────────────────────────────────────────
        # Valve should never be open while pump is not running
        if valve.is_open and not pump.is_running:
            faults.append(FaultCode.VALVE_SEQUENCE_FAULT)

        # ── 5. Backflow detection ────────────────────────────────────────────
        if pulser.is_reverse_flow():
            faults.append(FaultCode.BACKFLOW_DETECTED)

        # ── Log new faults ────────────────────────────────────────────────────
        prev_faults = set(self._last_result.active_faults)
        curr_faults = set(faults)

        for new_fault in curr_faults - prev_faults:
            logger.warning("INTERLOCK FAULT: %s", new_fault.value)
        for cleared_fault in prev_faults - curr_faults:
            logger.info("Interlock cleared: %s", cleared_fault.value)

        result = InterLockResult(
            permits_dispense=len(faults) == 0,
            active_faults=faults,
        )
        self._last_result = result
        return result

    @property
    def last_result(self) -> InterLockResult:
        return self._last_result

    def reset_level_latch(self) -> None:
        """Manually clear a latched low-level fault (e.g. after tank refill)."""
        self._level_fault_latched = False
        logger.info("Level fault latch manually cleared")

"""
pump_sequence.py — Piusi Suzzarablue AC Pump sequencer with duty-cycle protection.

Pump datasheet facts (F00203090 / F00203200):
  Voltage    : 230V AC / 50Hz
  Power      : 400W
  Current    : 1.95A max
  RPM        : 1450
  Flow       : up to 32 L/min
  DUTY CYCLE : 20 minutes continuous run maximum

Relay wiring note:
  The relay board (BOM C4) LOAD side switches 230V AC to the pump.
  Ensure the relay is rated ≥ 250V AC / 3A.  The standard 10A opto-
  isolated relay module far exceeds this requirement.

Duty-cycle enforcement:
  MAX_RUN_S  = 1200s (20 min)  — hard limit from datasheet
  COOL_MIN_S = 1200s (20 min)  — conservative minimum cooling period
    (Piusi does not publish a mandatory off-time; 1:1 ratio is the safe default.
     Reduce COOL_MIN_S in .env if your application allows shorter breaks.)

State machine:
  STOPPED
    ↓  start() called + interlocks OK
  STARTING  (200ms ramp — pump reaches operating pressure)
    ↓
  RUNNING   (accumulates run time every scan)
    ↓  stop() called  OR  run_time ≥ MAX_RUN_S
  STOPPING  (50ms coast-down)
    ↓  stop() called for normal stop  →  STOPPED
       duty-cycle exceeded           →  COOLING_DOWN
  COOLING_DOWN
    ↓  elapsed cool time ≥ COOL_MIN_S
  STOPPED   (pump available again)
"""

import logging
import time
import threading
from enum import Enum, auto

logger = logging.getLogger(__name__)

# ── Timing constants ──────────────────────────────────────────────────────────
MAX_RUN_S   = 1200.0    # 20 minutes continuous run limit (datasheet)
COOL_MIN_S  = 1200.0    # 20 minutes minimum cooling before restart
START_RAMP_S = 0.2      # delay after relay energised before declaring RUNNING
STOP_COAST_S = 0.05     # delay after relay de-energised


class PumpState(Enum):
    STOPPED      = auto()
    STARTING     = auto()
    RUNNING      = auto()
    STOPPING     = auto()
    COOLING_DOWN = auto()


class PumpSequencer:
    """
    Controls the Suzzarablue AC pump relay with duty-cycle protection.

    Cooperates with RelayController: call pump.start() / pump.stop() —
    this class drives relay.pump_on() / relay.pump_off() internally.

    Usage (in StationPLC scan loop):
        pump.evaluate()          # call every scan cycle
        pump.start()             # request pump start
        pump.stop("OPERATOR")    # request pump stop

    Properties:
        pump.state               → PumpState
        pump.can_start           → bool (False during COOLING_DOWN)
        pump.run_seconds         → float (current run duration)
        pump.cool_remaining_s    → float (seconds until restart allowed)
    """

    def __init__(self, relay) -> None:
        self._relay = relay
        self._state = PumpState.STOPPED
        self._lock  = threading.Lock()

        self._run_start_s:  float = 0.0
        self._cool_start_s: float = 0.0
        self._transition_at: float = 0.0    # monotonic time for timed transitions

        # Callbacks
        self._on_duty_exceeded = None       # injected by StationPLC

    # ── Public API ────────────────────────────────────────────────────────────

    def start(self) -> bool:
        """
        Request pump start.  Returns True if the request was accepted.
        Rejected if state is not STOPPED or COOLING_DOWN blocks restart.
        """
        with self._lock:
            if self._state == PumpState.COOLING_DOWN:
                remaining = self.cool_remaining_s
                logger.warning(
                    "Pump start rejected — cooling down (%.0fs remaining)", remaining
                )
                return False
            if self._state != PumpState.STOPPED:
                logger.warning("Pump start rejected — state=%s", self._state.name)
                return False

            self._state = PumpState.STARTING
            self._transition_at = time.monotonic() + START_RAMP_S
            self._run_start_s = time.monotonic()
            self._relay.pump_on()
            logger.info("Pump STARTING (relay energised — 230V AC)")
            return True

    def stop(self, reason: str = "REQUESTED") -> None:
        """Request pump stop."""
        with self._lock:
            if self._state not in (PumpState.STARTING,
                                   PumpState.RUNNING,
                                   PumpState.STOPPING):
                return
            self._state = PumpState.STOPPING
            self._transition_at = time.monotonic() + STOP_COAST_S
            self._relay.pump_off()
            logger.info("Pump STOPPING (reason=%s)", reason)

    def evaluate(self) -> None:
        """
        Call on every PLC scan cycle (100ms).
        Drives timed state transitions and enforces duty cycle.
        """
        now = time.monotonic()

        with self._lock:
            state = self._state

            if state == PumpState.STARTING:
                if now >= self._transition_at:
                    self._state = PumpState.RUNNING
                    logger.info("Pump RUNNING")

            elif state == PumpState.RUNNING:
                elapsed = now - self._run_start_s
                if elapsed >= MAX_RUN_S:
                    logger.warning(
                        "DUTY CYCLE EXCEEDED (%.0f s) — forcing pump stop and cooldown",
                        elapsed,
                    )
                    self._relay.pump_off()
                    self._state = PumpState.COOLING_DOWN
                    self._cool_start_s = now
                    self._transition_at = now + STOP_COAST_S
                    if self._on_duty_exceeded:
                        self._on_duty_exceeded()

            elif state == PumpState.STOPPING:
                if now >= self._transition_at:
                    elapsed = now - self._run_start_s
                    logger.info("Pump STOPPED (ran for %.1f s)", elapsed)
                    # Determine next state: cooling required only if near limit
                    if elapsed >= MAX_RUN_S * 0.9:
                        self._state = PumpState.COOLING_DOWN
                        self._cool_start_s = now
                        logger.info("Entering COOLING_DOWN (ran %.0f%% of duty cycle)", 
                                    elapsed / MAX_RUN_S * 100)
                    else:
                        self._state = PumpState.STOPPED

            elif state == PumpState.COOLING_DOWN:
                if now - self._cool_start_s >= COOL_MIN_S:
                    logger.info("Cooling complete — pump available")
                    self._state = PumpState.STOPPED

    def on_duty_exceeded(self, callback) -> None:
        """Register a callback invoked when duty cycle is exceeded (emergency stop)."""
        self._on_duty_exceeded = callback

    # ── Properties ────────────────────────────────────────────────────────────

    @property
    def state(self) -> PumpState:
        return self._state

    @property
    def is_running(self) -> bool:
        return self._state in (PumpState.STARTING, PumpState.RUNNING)

    @property
    def can_start(self) -> bool:
        return self._state == PumpState.STOPPED

    @property
    def run_seconds(self) -> float:
        if self._state in (PumpState.STARTING, PumpState.RUNNING):
            return time.monotonic() - self._run_start_s
        return 0.0

    @property
    def run_remaining_s(self) -> float:
        """Seconds of run time remaining before forced cooldown."""
        if self._state not in (PumpState.STARTING, PumpState.RUNNING):
            return MAX_RUN_S
        return max(0.0, MAX_RUN_S - self.run_seconds)

    @property
    def cool_remaining_s(self) -> float:
        """Seconds remaining in the cooling period (0 if not cooling)."""
        if self._state != PumpState.COOLING_DOWN:
            return 0.0
        return max(0.0, COOL_MIN_S - (time.monotonic() - self._cool_start_s))

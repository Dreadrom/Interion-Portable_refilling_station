"""
station_plc.py — Main PLC scan loop for the ODROID-M2 refilling station.

Implements a cyclic scan architecture equivalent to a PLC ladder program:

  SCAN CYCLE (every 100ms):
    1. READ inputs   — limit switch, level sensor, pulser state
    2. EVALUATE interlocks — safety chain check
    3. EVALUATE pump sequencer — timed state transitions + duty cycle
    4. EVALUATE valve controller — timed state transitions
    5. EXECUTE dispense logic — act on state machine command
    6. PUBLISH outputs — telemetry if in DISPENSING state

External control (from HardwareManager / MQTT):
    plc.cmd_start(txn_id, max_volume) — begin a dispense
    plc.cmd_stop(reason)              — halt current dispense
    plc.cmd_ping()                    → returns current plc state dict

The StationPLC is intentionally decoupled from MQTT/HTTP — it receives plain
Python calls and returns plain data.  HardwareManager owns all IO with the
cloud and the local API.
"""

import logging
import threading
import time
from enum import Enum, auto
from typing import Callable, Optional

from .pump_sequence import PumpSequencer, PumpState
from .valve_control import ValveController
from .interlock import InterLockChain, InterLockResult

logger = logging.getLogger(__name__)

SCAN_INTERVAL_S = 0.1    # 100ms PLC scan cycle


class PlcState(Enum):
    IDLE        = auto()
    AUTHORISED  = auto()    # command received, waiting to start
    DISPENSING  = auto()    # pump running, valve open, counting
    STOPPING    = auto()    # valve closing, pump ramping down
    FAULT       = auto()    # interlock tripped during dispense


# Callback signatures used by StationPLC
OnFlowTick   = Callable[[float], None]    # volume_increment_litres
OnFault      = Callable[[list], None]     # fault_codes list
OnComplete   = Callable[[str], None]      # stop_reason string


class StationPLC:
    """
    Main PLC scan loop.  Owns PumpSequencer, ValveController, InterLockChain.

    Usage:
        plc = StationPLC(relay, pulser, level_sensor, limit_switch)
        plc.on_flow_tick(handle_pulse)
        plc.on_fault(handle_fault)
        plc.on_complete(handle_complete)
        plc.start()               # starts background scan thread

        plc.cmd_start("txn-001", max_volume=20.0)
        # ...
        plc.cmd_stop("OPERATOR")
        plc.stop()                # stops scan thread
    """

    def __init__(self, relay, pulser, level_sensor, limit_switch) -> None:
        self._relay         = relay
        self._pulser        = pulser
        self._level_sensor  = level_sensor
        self._limit_switch  = limit_switch

        # ── PLC blocks ────────────────────────────────────────────────────────
        self.pump       = PumpSequencer(relay)
        self.valve      = ValveController(relay)
        self.interlocks = InterLockChain()

        # ── Internal state ────────────────────────────────────────────────────
        self._plc_state       = PlcState.IDLE
        self._state_lock      = threading.Lock()
        self._txn_id:         Optional[str]   = None
        self._max_volume:     float           = 0.0
        self._stop_reason:    Optional[str]   = None

        # Cached sensor values (updated each scan)
        self._last_tank_level:    Optional[float] = None
        self._last_limit_tripped: bool            = False
        self._last_interlock:     Optional[InterLockResult] = None

        # ── Background scan ───────────────────────────────────────────────────
        self._scan_thread:   Optional[threading.Thread] = None
        self._stop_event     = threading.Event()

        # ── Callbacks (wired by HardwareManager) ─────────────────────────────
        self._flow_tick_cb:   Optional[OnFlowTick]  = None
        self._fault_cb:       Optional[OnFault]     = None
        self._complete_cb:    Optional[OnComplete]  = None

        # Wire duty-cycle exceeded → emergency stop
        self.pump.on_duty_exceeded(lambda: self._emergency_stop("DUTY_CYCLE_EXCEEDED"))

        # Wire pulser pulse callback → flow tick
        self._pulser.start(self._on_pulse)

    # ── Lifecycle ─────────────────────────────────────────────────────────────

    def start(self) -> None:
        """Start the PLC scan loop thread."""
        self._stop_event.clear()
        self._scan_thread = threading.Thread(
            target=self._scan_loop, daemon=True, name="plc-scan"
        )
        self._scan_thread.start()
        logger.info("StationPLC scan loop started (%.0fms cycle)", SCAN_INTERVAL_S * 1000)

    def stop(self) -> None:
        """Stop the scan loop and ensure hardware is safe."""
        logger.info("StationPLC stopping")
        self._stop_event.set()
        self.valve.close()
        time.sleep(CLOSE_DELAY := 0.2)
        self.pump.stop("SHUTDOWN")
        if self._scan_thread:
            self._scan_thread.join(timeout=2)
        logger.info("StationPLC stopped")

    # ── External commands (from HardwareManager / MQTT) ──────────────────────

    def cmd_start(self, txn_id: str, max_volume: float = 0.0) -> bool:
        """
        Command: begin dispensing for transaction `txn_id` up to `max_volume` litres.
        Returns False if interlocks prevent start or pump is not available.
        """
        with self._state_lock:
            if self._plc_state != PlcState.IDLE:
                logger.warning("cmd_start rejected — state=%s", self._plc_state.name)
                return False

            # Quick interlock pre-check before committing
            il = self._evaluate_interlocks()
            if not il.permits_dispense:
                logger.warning("cmd_start rejected — interlocks: %s", il.fault_names)
                return False

            if not self.pump.can_start:
                logger.warning("cmd_start rejected — pump not available (%s)",
                               self.pump.state.name)
                return False

            self._txn_id    = txn_id
            self._max_volume = max_volume
            self._plc_state = PlcState.AUTHORISED
            logger.info("cmd_start accepted — txn=%s max=%.2fL", txn_id, max_volume)
            return True

    def cmd_stop(self, reason: str = "OPERATOR") -> None:
        """Command: stop dispensing."""
        with self._state_lock:
            if self._plc_state not in (PlcState.DISPENSING, PlcState.AUTHORISED):
                return
            self._stop_reason = reason
            self._plc_state   = PlcState.STOPPING
        logger.info("cmd_stop: %s", reason)

    def cmd_ping(self) -> dict:
        """Return a summary of current PLC state (for diagnostics)."""
        il = self._last_interlock
        return {
            "plcState":         self._plc_state.name,
            "pumpState":        self.pump.state.name,
            "valveState":       self.valve.state.name,
            "txnId":            self._txn_id,
            "runRemainingS":    round(self.pump.run_remaining_s, 0),
            "coolRemainingS":   round(self.pump.cool_remaining_s, 0),
            "tankLevelPct":     self._last_tank_level,
            "limitTripped":     self._last_limit_tripped,
            "permitsDispense":  il.permits_dispense if il else None,
            "activeFaults":     il.fault_names if il else [],
        }

    # ── Callback registration ─────────────────────────────────────────────────

    def on_flow_tick(self, cb: OnFlowTick) -> None:
        self._flow_tick_cb = cb

    def on_fault(self, cb: OnFault) -> None:
        self._fault_cb = cb

    def on_complete(self, cb: OnComplete) -> None:
        self._complete_cb = cb

    # ── PLC scan loop ─────────────────────────────────────────────────────────

    def _scan_loop(self) -> None:
        while not self._stop_event.is_set():
            scan_start = time.monotonic()
            try:
                self._scan_once()
            except Exception as exc:
                logger.error("PLC scan error: %s", exc, exc_info=True)
            elapsed = time.monotonic() - scan_start
            sleep = max(0.0, SCAN_INTERVAL_S - elapsed)
            self._stop_event.wait(timeout=sleep)

    def _scan_once(self) -> None:
        # ── 1. READ inputs ────────────────────────────────────────────────────
        self._last_limit_tripped = self._limit_switch.is_tripped()
        self._last_tank_level    = self._level_sensor.read_level()

        # ── 2. EVALUATE interlocks ────────────────────────────────────────────
        il = self._evaluate_interlocks()
        self._last_interlock = il

        # ── 3. EVALUATE PLC blocks ────────────────────────────────────────────
        self.pump.evaluate()
        self.valve.evaluate()

        # ── 4. EXECUTE dispense logic ─────────────────────────────────────────
        with self._state_lock:
            state = self._plc_state

        if state == PlcState.AUTHORISED:
            self._exec_authorised(il)

        elif state == PlcState.DISPENSING:
            self._exec_dispensing(il)

        elif state == PlcState.STOPPING:
            self._exec_stopping()

    def _exec_authorised(self, il: InterLockResult) -> None:
        """AUTHORISED → start pump → open valve → DISPENSING."""
        if not il.permits_dispense:
            logger.warning("Interlocks prevent start: %s", il.fault_names)
            with self._state_lock:
                self._plc_state = PlcState.FAULT
            self._fire_fault(il.active_faults)
            self._reset()
            return

        if self.pump.can_start and self.pump.start():
            time.sleep(0.3)   # brief pause after pump on before opening valve
            self.valve.open(self.pump)
            with self._state_lock:
                self._plc_state = PlcState.DISPENSING
            logger.info("PLC → DISPENSING")

    def _exec_dispensing(self, il: InterLockResult) -> None:
        """DISPENSING — monitor interlocks and volume limit."""
        if not il.permits_dispense:
            logger.warning("Interlock tripped during dispense: %s", il.fault_names)
            self._fire_fault(il.active_faults)
            self._emergency_stop("INTERLOCK_TRIP")
            return

        # Volume cap check is handled via the pulse callback → cmd_stop

    def _exec_stopping(self) -> None:
        """STOPPING — close valve, stop pump, wait for both to settle."""
        # Close valve first
        if not self.valve.is_closed:
            self.valve.close()
            return   # wait next scan for valve to close

        # Valve is closed — now stop pump
        if self.pump.is_running:
            self.pump.stop(self._stop_reason or "STOPPING")
            return   # wait for pump to settle

        # Both off — complete
        reason = self._stop_reason or "STOP"
        logger.info("Dispense sequence complete (reason=%s)", reason)
        self._fire_complete(reason)
        self._reset()

    # ── Helpers ───────────────────────────────────────────────────────────────

    def _evaluate_interlocks(self) -> InterLockResult:
        return self.interlocks.evaluate(
            limit_tripped=self._last_limit_tripped,
            tank_level_pct=self._last_tank_level,
            pump=self.pump,
            valve=self.valve,
            pulser=self._pulser,
        )

    def _emergency_stop(self, reason: str) -> None:
        """Immediate safe shutdown — valve close then pump off."""
        logger.error("EMERGENCY STOP: %s", reason)
        self.valve.close()
        self.pump.stop(reason)
        with self._state_lock:
            self._stop_reason = reason
            self._plc_state   = PlcState.STOPPING

    def _on_pulse(self, volume_increment: float) -> None:
        """Called by MK325Pulser on every pulse tick."""
        if self._plc_state != PlcState.DISPENSING:
            return

        if self._flow_tick_cb:
            try:
                self._flow_tick_cb(volume_increment)
            except Exception as exc:
                logger.error("Flow tick callback error: %s", exc)

        # Volume limit check
        # (FlowProcessor accumulates total; HardwareManager calls cmd_stop when hit)

    def _reset(self) -> None:
        with self._state_lock:
            self._txn_id      = None
            self._max_volume  = 0.0
            self._stop_reason = None
            self._plc_state   = PlcState.IDLE

    def _fire_fault(self, faults) -> None:
        if self._fault_cb:
            try:
                self._fault_cb([f.value for f in faults])
            except Exception as exc:
                logger.error("Fault callback error: %s", exc)

    def _fire_complete(self, reason: str) -> None:
        if self._complete_cb:
            try:
                self._complete_cb(reason)
            except Exception as exc:
                logger.error("Complete callback error: %s", exc)

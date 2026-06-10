"""
hardware_manager.py — Central orchestrator for the ODROID-M2 station.

Wires together:
  - plc.StationPLC   (PLC scan loop: pump sequencer, valve control, interlocks)
  - MK325Pulser      (flow measurement — owned by StationPLC)
  - LevelSensor      (tank fill level via Modbus RTU)
  - LimitSwitch      (overflow / interlock protection)
  - RelayController  (pump motor + solenoid valve — 230V AC relay switching)
  - FlowProcessor    (on-chip volume / rate / extrusion calculation)
  - MQTTUploader     (AWS IoT Core telemetry + command receive)

State machine (MQTT-driven):
  IDLE
    ↓  AUTHORIZE command
  AUTHORIZING  — display code, publish auth/challenge
    ↓  AUTH_CODE accepted
  DISPENSING   — PLC running, flow published each pulse tick
    ↓  STOP / target reached / PLC fault / duty cycle exceeded
  IDLE

All hardware sequencing (pump on/off timing, valve delays, duty cycle,
interlocks) is handled by plc.StationPLC — this class only coordinates
MQTT ↔ PLC and publishes telemetry.

Background threads:
  - heartbeat_loop    — publishes MQTT heartbeat every heartbeat_interval_s
  - sensor_upload_loop — periodically uploads sensor snapshot to MQTT
    (StationPLC itself reads sensors every 100ms scan cycle)
"""

import logging
import threading
import time
import queue
import random
import string
from enum import Enum, auto
from typing import Optional, List

from config import M2Config
from sensors import MK325Pulser, LevelSensor, LimitSwitch
from actuators import RelayController
from processing import FlowProcessor
from comms import MQTTUploader
from plc import StationPLC

logger = logging.getLogger(__name__)

UNIT_PRICE_PER_LITRE = 10.00   # MYR — update or pull from backend payload


class State(Enum):
    IDLE        = auto()
    AUTHORIZING = auto()
    DISPENSING  = auto()


def _generate_auth_code(length: int = 4) -> str:
    return "".join(random.choices(string.digits, k=length))


class HardwareManager:
    """
    Top-level hardware orchestrator.  Instantiate once in main.py, call start(),
    then let the MQTT command loop drive the state machine.
    """

    def __init__(self, cfg: M2Config) -> None:
        self.cfg = cfg

        # ── Hardware components ───────────────────────────────────────────────
        self.pulser         = MK325Pulser(cfg)
        self.level_sensor   = LevelSensor(cfg)
        self.limit_switch   = LimitSwitch(cfg)
        self.relay          = RelayController(cfg)
        self.flow_processor = FlowProcessor()
        self.mqtt           = MQTTUploader(cfg)

        # ── PLC (owns pump sequencer, valve controller, interlock chain) ──────
        # Note: pulser is passed in so the PLC scan loop owns pulse counting.
        self.plc = StationPLC(
            relay=self.relay,
            pulser=self.pulser,
            level_sensor=self.level_sensor,
            limit_switch=self.limit_switch,
        )

        # ── State ─────────────────────────────────────────────────────────────
        self._state      = State.IDLE
        self._state_lock = threading.Lock()

        self._txn_id:     Optional[str] = None
        self._max_volume: float         = 0.0
        self._max_amount: float         = 0.0
        self._auth_code:  Optional[str] = None

        # Cached for local API
        self.last_tank_level:    Optional[float] = None
        self.last_limit_tripped: bool            = False

        # SSE subscriber queues (one per connected kiosk browser tab)
        self._sse_queues: List[queue.Queue] = []
        self._sse_lock   = threading.Lock()

        # Background thread handles
        self._heartbeat_thread:    Optional[threading.Thread] = None
        self._sensor_upload_thread: Optional[threading.Thread] = None
        self._stop_event = threading.Event()

    @property
    def state(self) -> str:
        return self._state.name

    # ── SSE pub/sub ───────────────────────────────────────────────────────────

    def subscribe_sse(self) -> queue.Queue:
        """Register a new SSE listener; returns the queue to read events from."""
        q: queue.Queue = queue.Queue(maxsize=64)
        with self._sse_lock:
            self._sse_queues.append(q)
        return q

    def unsubscribe_sse(self, q: queue.Queue) -> None:
        """Remove an SSE listener queue."""
        with self._sse_lock:
            self._sse_queues = [x for x in self._sse_queues if x is not q]

    def _emit_sse(self, event: str, data: dict) -> None:
        """Push an SSE event to all connected listeners (non-blocking)."""
        payload = {"event": event, **data}
        with self._sse_lock:
            dead = []
            for q in self._sse_queues:
                try:
                    q.put_nowait(payload)
                except queue.Full:
                    dead.append(q)   # slow subscriber — drop it
            for q in dead:
                self._sse_queues.remove(q)

    # ── Lifecycle ─────────────────────────────────────────────────────────────

    def start(self) -> None:
        """Initialise all hardware, connect MQTT, start PLC and background loops."""
        logger.info("HardwareManager: starting up — mode=%s", self.cfg.hardware_mode)

        # Hardware init
        self.relay.open()
        self.limit_switch.open()
        self.level_sensor.connect()

        # Wire PLC callbacks → this manager
        self.plc.on_flow_tick(self._on_flow_tick)
        self.plc.on_fault(self._on_plc_fault)
        self.plc.on_complete(self._on_plc_complete)

        # Start PLC scan loop (100ms cycle)
        self.plc.start()

        # Register MQTT callbacks
        self.mqtt.on_command(self._on_command)
        self.mqtt.on_auth_response(self._on_auth_response)
        self.mqtt.connect()

        # Background loops
        self._stop_event.clear()
        self._heartbeat_thread = threading.Thread(
            target=self._heartbeat_loop, daemon=True, name="heartbeat"
        )
        self._sensor_upload_thread = threading.Thread(
            target=self._sensor_upload_loop, daemon=True, name="sensor-upload"
        )
        self._heartbeat_thread.start()
        self._sensor_upload_thread.start()

        self.mqtt.publish_status("IDLE")
        logger.info("HardwareManager: ready")

    def stop(self) -> None:
        """Graceful shutdown."""
        logger.info("HardwareManager: shutting down")
        self._stop_event.set()

        self.plc.stop()          # PLC handles safe valve→pump shutdown sequence
        self.limit_switch.close()
        self.level_sensor.close()
        self.relay.close()
        self.mqtt.disconnect()

    # ── MQTT command handlers ─────────────────────────────────────────────────

    def _on_command(self, data: dict) -> None:
        action = data.get("action", "").upper()
        logger.info("Command received: %s (state=%s)", action, self._state.name)

        if action == "AUTHORIZE":
            self._handle_authorize(data)
        elif action == "STOP":
            self._handle_stop(data)
        elif action == "PING":
            self.mqtt.publish_status(self._state.name, self._txn_id)
        else:
            logger.warning("Unknown command action: %s", action)

    def _on_auth_response(self, data: dict) -> None:
        if self._state != State.AUTHORIZING:
            return
        accepted = data.get("accepted", False)
        txn_id   = data.get("transactionId")
        if txn_id != self._txn_id:
            logger.warning("Auth response for unknown txn: %s", txn_id)
            return

        if accepted:
            logger.info("Auth accepted — handing off to PLC, txn=%s", self._txn_id)
            started = self.plc.cmd_start(self._txn_id, self._max_volume)
            if started:
                with self._state_lock:
                    self._state = State.DISPENSING
                # Stub: notify sensors dispensing is active
                if self.cfg.hardware_mode != "REAL":
                    self.level_sensor.set_stub_valve_open(True)
                self.flow_processor.start_transaction(
                    txn_id=self._txn_id,
                    target_litres=self._max_volume,
                )
                self.mqtt.publish_status("DISPENSING", self._txn_id)
            else:
                logger.error("PLC rejected start — interlocks or pump unavailable")
                self._reset()
        else:
            logger.warning("Auth rejected for txn=%s", self._txn_id)
            self._reset()

    # ── State transitions ─────────────────────────────────────────────────────

    def _handle_authorize(self, data: dict) -> None:
        with self._state_lock:
            if self._state != State.IDLE:
                logger.warning("AUTHORIZE ignored — not IDLE (state=%s)", self._state.name)
                return
            self._txn_id     = data.get("transactionId")
            self._max_volume = float(data.get("maxVolume", 0))
            self._max_amount = float(data.get("maxAmount", 0))
            self._state      = State.AUTHORIZING

        self._auth_code = _generate_auth_code()
        logger.info("Auth code for txn %s: %s", self._txn_id, self._auth_code)
        self.mqtt.publish_auth_challenge(self._txn_id, self._auth_code)
        self.mqtt.publish_status("AUTHORIZING", self._txn_id)

    def _handle_stop(self, data: dict) -> None:
        txn_id = data.get("transactionId")
        if txn_id and txn_id != self._txn_id:
            logger.warning("STOP for unknown txn: %s", txn_id)
            return
        if self._state not in (State.DISPENSING, State.AUTHORIZING):
            return
        self.plc.cmd_stop("OPERATOR_STOP")

    # ── PLC callbacks ─────────────────────────────────────────────────────────

    def _on_flow_tick(self, volume_increment: float) -> None:
        """Called by StationPLC on each MK325 pulse — forwards to FlowProcessor."""
        self.flow_processor.on_pulse(volume_increment)
        snap = self.flow_processor.snapshot()
        self.mqtt.publish_flow(snap)

        # Stream to kiosk SSE clients
        self._emit_sse('flow', {
            'volumeLitres':   round(snap.volume_litres, 4),
            'flowRateLPM':    round(snap.flow_rate_lpm, 2),
            'elapsedSeconds': round(snap.elapsed_seconds, 1),
        })

        # Enforce volume cap
        if self._max_volume > 0 and snap.volume_litres >= self._max_volume:
            logger.info("Volume target reached (%.3fL) — stopping", snap.volume_litres)
            self.plc.cmd_stop("VOLUME_TARGET_REACHED")

    def _on_plc_fault(self, fault_codes: list) -> None:
        """Called by StationPLC when an interlock trips during dispense."""
        logger.error("PLC faults: %s", fault_codes)
        self._emit_sse('fault', {'faults': fault_codes})
        self.mqtt.publish_status("FAULT", self._txn_id)

    def _on_plc_complete(self, reason: str) -> None:
        """Called by StationPLC when the dispense sequence fully completes."""
        final  = self.flow_processor.end_transaction()
        amount = round(final.volume_litres * UNIT_PRICE_PER_LITRE, 2)

        self._emit_sse('complete', {
            'flow': {
                'volumeLitres':   round(final.volume_litres, 4),
                'elapsedSeconds': round(final.elapsed_seconds, 1),
                'flowRateLPM':    round(final.flow_rate_lpm, 2),
            },
            'plc': {'stopReason': reason},
        })

        self.mqtt.publish_complete(
            txn_id=self._txn_id or "",
            volume=final.volume_litres,
            amount=amount,
            stop_reason=reason,
        )
        logger.info("Dispense complete — %.3fL / MYR%.2f (reason=%s)",
                    final.volume_litres, amount, reason)

        # Stub cleanup
        if self.cfg.hardware_mode != "REAL":
            self.level_sensor.set_stub_valve_open(False)

        self._reset()

    def _reset(self) -> None:
        with self._state_lock:
            self._state      = State.IDLE
            self._txn_id     = None
            self._max_volume = 0.0
            self._max_amount = 0.0
            self._auth_code  = None
        self.mqtt.publish_status("IDLE")

    # ── Background sensor upload (MQTT only — PLC reads sensors every scan) ───

    def _sensor_upload_loop(self) -> None:
        logger.info("Sensor upload loop started (interval=%.1fs)",
                    self.cfg.sensor_poll_interval_s)
        while not self._stop_event.is_set():
            try:
                # Read latest values from PLC's cached scan data
                plc_status = self.plc.cmd_ping()
                level   = plc_status.get("tankLevelPct")
                tripped = plc_status.get("limitTripped", False)
                self.last_tank_level    = level
                self.last_limit_tripped = tripped
                self.mqtt.publish_sensors(level, tripped)
            except Exception as exc:
                logger.error("Sensor upload error: %s", exc)
            self._stop_event.wait(timeout=self.cfg.sensor_poll_interval_s)

    # ── Heartbeat ─────────────────────────────────────────────────────────────

    def _heartbeat_loop(self) -> None:
        logger.info("Heartbeat loop started (interval=%.1fs)",
                    self.cfg.heartbeat_interval_s)
        while not self._stop_event.is_set():
            try:
                self.mqtt.publish_heartbeat()
            except Exception as exc:
                logger.error("Heartbeat error: %s", exc)
            self._stop_event.wait(timeout=self.cfg.heartbeat_interval_s)

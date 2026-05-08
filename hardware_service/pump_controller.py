"""
pump_controller.py — Core dispensing orchestrator.

State machine:
  IDLE  →  AUTHORIZING  →  WAITING_AUTH  →  DISPENSING  →  IDLE
                                ↑                ↓
                            (code wrong)   (stop / limit / e-stop)

Command messages received via MQTT (topic: .../commands):
  { "action": "AUTHORIZE",  "transactionId": "...", "maxVolume": 50.0, "maxAmount": 500.0 }
  { "action": "AUTH_CODE",  "transactionId": "...", "code": "1234" }
  { "action": "STOP",       "transactionId": "..." }
  { "action": "PING" }

Telemetry messages published (topics: .../telemetry/flow, .../telemetry/status):
  flow:   { "volumeLitres": 12.5, "rateLPM": 5.0, "elapsedSeconds": 150 }
  status: { "status": "DISPENSING", "transactionId": "..." }

Auth challenge published (topic: .../auth/challenge):
  { "transactionId": "...", "ready": true }   ← tells backend code is on display

Auth response received (topic: .../auth/response):
  { "transactionId": "...", "accepted": true }  ← backend validated user's code entry
"""

import logging
import threading
import time
from enum import Enum, auto

from config import cfg
from display import PumpDisplay, generate_auth_code
from mqtt_client import MQTTClient
from plc_interface import PumpHardware

logger = logging.getLogger(__name__)

UNIT_PRICE = 10.00  # MYR per litre — TODO: pull from backend or MQTT payload


class State(Enum):
    IDLE          = auto()
    AUTHORIZING   = auto()  # command received, code being displayed
    WAITING_AUTH  = auto()  # code on display, waiting for user to enter in app
    DISPENSING    = auto()
    STOPPING      = auto()


class PumpController:
    def __init__(self, mqtt: MQTTClient, hardware: PumpHardware, display: PumpDisplay) -> None:
        self._mqtt     = mqtt
        self._hw       = hardware
        self._display  = display
        self._state    = State.IDLE
        self._lock     = threading.Lock()

        # Active transaction data
        self._txn_id: str | None          = None
        self._max_volume: float           = 0.0
        self._max_amount: float           = 0.0
        self._auth_code: str | None       = None
        self._volume_dispensed: float     = 0.0
        self._start_time: float | None    = None

        # Register MQTT handlers
        mqtt.on("commands",      self._on_command)
        mqtt.on("auth/response", self._on_auth_response)

    # ── MQTT handlers ────────────────────────────────────────────────────────

    def _on_command(self, _topic: str, data: dict) -> None:
        action = data.get("action", "").upper()
        logger.info("Command received: %s | state=%s", action, self._state.name)

        if action == "AUTHORIZE":
            self._handle_authorize(data)
        elif action == "STOP":
            self._handle_stop(data)
        elif action == "PING":
            self._mqtt.publish_status(self._state.name)
        else:
            logger.warning("Unknown command action: %s", action)

    def _on_auth_response(self, _topic: str, data: dict) -> None:
        """Backend tells us whether the code the user entered was accepted."""
        if self._state != State.WAITING_AUTH:
            return
        accepted = data.get("accepted", False)
        txn_id   = data.get("transactionId")

        if txn_id != self._txn_id:
            logger.warning("Auth response for unknown transaction %s", txn_id)
            return

        if accepted:
            logger.info("Auth accepted — starting dispense for txn %s", self._txn_id)
            self._start_dispensing()
        else:
            logger.warning("Auth rejected for txn %s", self._txn_id)
            self._reset(publish_status="AUTH_FAILED")

    # ── State transitions ────────────────────────────────────────────────────

    def _handle_authorize(self, data: dict) -> None:
        with self._lock:
            if self._state != State.IDLE:
                logger.warning("AUTHORIZE received but station not IDLE (state=%s)", self._state.name)
                return

            self._txn_id     = data.get("transactionId")
            self._max_volume = float(data.get("maxVolume", 0))
            self._max_amount = float(data.get("maxAmount", 0))
            self._volume_dispensed = 0.0
            self._state = State.AUTHORIZING

        # Generate and display auth code
        self._auth_code = generate_auth_code()
        self._display.show_auth_code(self._auth_code)

        # Publish challenge — backend will relay to the app session
        self._mqtt.publish("auth/challenge", {
            "transactionId": self._txn_id,
            "code": self._auth_code,   # backend stores this; validates user's entry
            "ready": True,
        })
        self._mqtt.publish_status("WAITING_AUTH", {"transactionId": self._txn_id})

        with self._lock:
            self._state = State.WAITING_AUTH

        logger.info("Auth code %s displayed for txn %s", self._auth_code, self._txn_id)

    def _start_dispensing(self) -> None:
        with self._lock:
            self._state = State.DISPENSING
            self._start_time = time.monotonic()

        self._display.show_message("DISPENSING", f"Max {self._max_volume:.0f} L")
        self._mqtt.publish_status("DISPENSING", {"transactionId": self._txn_id})

        self._hw.open_valve()
        self._hw.start_flow_counting(self._on_flow_tick)

    def _on_flow_tick(self, litres_this_tick: float) -> None:
        """Called by hardware on each flow pulse; accumulates volume and checks limits."""
        with self._lock:
            if self._state != State.DISPENSING:
                return
            self._volume_dispensed += litres_this_tick
            volume = self._volume_dispensed
            elapsed = int(time.monotonic() - (self._start_time or time.monotonic()))

        # Publish live flow telemetry
        rate_lpm = (volume / max(elapsed, 1)) * 60
        self._mqtt.publish_flow(volume, rate_lpm, elapsed)

        # Check limits
        amount_so_far = volume * UNIT_PRICE
        if volume >= self._max_volume or amount_so_far >= self._max_amount:
            logger.info(
                "Limit reached: %.3f L / MYR %.2f — stopping dispense", volume, amount_so_far
            )
            self._stop_dispensing(reason="TARGET_REACHED")

        # Check emergency stop
        if self._hw.is_estop_active():
            logger.warning("Emergency stop triggered!")
            self._stop_dispensing(reason="EMERGENCY_STOP")

    def _handle_stop(self, data: dict) -> None:
        if self._state not in (State.DISPENSING, State.WAITING_AUTH, State.AUTHORIZING):
            return
        self._stop_dispensing(reason=data.get("reason", "DRIVER_STOPPED"))

    def _stop_dispensing(self, reason: str = "STOPPED") -> None:
        with self._lock:
            if self._state == State.STOPPING:
                return
            self._state = State.STOPPING
            volume = self._volume_dispensed

        self._hw.close_valve()
        self._hw.stop_flow_counting()
        self._display.show_message("DONE", f"{volume:.2f} L")

        self._mqtt.publish("telemetry/complete", {
            "transactionId": self._txn_id,
            "volumeLitres":  round(volume, 3),
            "amountCharged": round(volume * UNIT_PRICE, 2),
            "stopReason":    reason,
        })
        self._reset(publish_status="IDLE")

    def _reset(self, publish_status: str = "IDLE") -> None:
        with self._lock:
            self._txn_id           = None
            self._auth_code        = None
            self._volume_dispensed = 0.0
            self._start_time       = None
            self._state            = State.IDLE

        self._display.clear()
        self._mqtt.publish_status(publish_status)
        logger.info("Station reset to IDLE")

    # ── Heartbeat ────────────────────────────────────────────────────────────

    def run_heartbeat(self, interval_sec: int = 30) -> None:
        """Publish a heartbeat on a background thread. Call once at startup."""
        def _loop():
            while True:
                self._mqtt.publish_heartbeat()
                time.sleep(interval_sec)
        t = threading.Thread(target=_loop, daemon=True)
        t.start()

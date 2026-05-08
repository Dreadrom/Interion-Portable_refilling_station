"""
plc_interface.py — Abstract hardware interface + mode-selected implementations.

The PumpHardware base class defines the contract used by PumpController.
At startup, get_hardware() returns the correct implementation based on
HARDWARE_MODE in .env:

  STUB    — Simulated hardware (default, no physical device needed)
  GPIO    — Raspberry Pi GPIO direct relay + flow sensor
  SNAP7   — Siemens S7-1200/S7-1500 via python-snap7
  MODBUS  — Any PLC via Modbus TCP (e.g. Siemens with Modbus module)

Stub mode simulates dispensing so the full MQTT + pump_controller flow
can be tested end-to-end without any hardware connected.
"""

import logging
import random
import threading
import time
from abc import ABC, abstractmethod
from typing import Callable

from config import cfg

logger = logging.getLogger(__name__)

# Callback type: called each time the flow sensor ticks (one pulse = 1/N litres)
FlowCallback = Callable[[float], None]  # arg: volume dispensed this tick (litres)


# ── Abstract base ────────────────────────────────────────────────────────────

class PumpHardware(ABC):
    """
    Contract for all hardware back-ends.
    All methods are synchronous; the caller (PumpController) runs them in a
    background thread.
    """

    @abstractmethod
    def open_valve(self) -> None:
        """Command the solenoid valve to open (start flow)."""

    @abstractmethod
    def close_valve(self) -> None:
        """Command the solenoid valve to close (stop flow)."""

    @abstractmethod
    def start_flow_counting(self, callback: FlowCallback) -> None:
        """
        Begin counting flow pulses and calling `callback` with incremental
        litres dispensed. Runs until stop_flow_counting() is called.
        """

    @abstractmethod
    def stop_flow_counting(self) -> None:
        """Stop the flow counting thread/interrupt."""

    @abstractmethod
    def is_estop_active(self) -> bool:
        """Return True if the physical emergency-stop is engaged."""


# ── STUB implementation ───────────────────────────────────────────────────────

class StubHardware(PumpHardware):
    """
    Simulated hardware for development and testing.
    Dispenses at a configurable simulated rate (default 5 L/min).
    """

    SIMULATED_RATE_LPM = 5.0   # litres per minute
    TICK_INTERVAL_SEC  = 0.5   # how often to fire the callback

    def __init__(self) -> None:
        self._valve_open = False
        self._flow_thread: threading.Thread | None = None
        self._stop_event = threading.Event()

    def open_valve(self) -> None:
        self._valve_open = True
        logger.info("[STUB] Valve OPENED")

    def close_valve(self) -> None:
        self._valve_open = False
        logger.info("[STUB] Valve CLOSED")

    def start_flow_counting(self, callback: FlowCallback) -> None:
        self._stop_event.clear()
        self._flow_thread = threading.Thread(
            target=self._simulate_flow,
            args=(callback,),
            daemon=True,
        )
        self._flow_thread.start()

    def _simulate_flow(self, callback: FlowCallback) -> None:
        litres_per_tick = (self.SIMULATED_RATE_LPM / 60) * self.TICK_INTERVAL_SEC
        while not self._stop_event.is_set():
            time.sleep(self.TICK_INTERVAL_SEC)
            if self._valve_open:
                # Small random noise ±2% to simulate sensor jitter
                jitter = litres_per_tick * random.uniform(-0.02, 0.02)
                callback(litres_per_tick + jitter)

    def stop_flow_counting(self) -> None:
        self._stop_event.set()
        if self._flow_thread:
            self._flow_thread.join(timeout=2)

    def is_estop_active(self) -> bool:
        return False  # Never triggered in stub mode


# ── GPIO implementation (Raspberry Pi) ───────────────────────────────────────
# ⚠️  HARDWARE STUB — implement when RPi is confirmed as the controller.
#     Requires: pip install gpiozero RPi.GPIO

class GPIOHardware(PumpHardware):
    """
    Direct RPi GPIO control:
      - Relay module (active-low or active-high) on GPIO_VALVE_PIN
      - Pulse-output flow sensor (YF-S201 class) on GPIO_FLOW_PIN
      - Normally-closed emergency stop button on GPIO_ESTOP_PIN (pull-up)
    """

    def __init__(self) -> None:
        # TODO: import gpiozero here once RPi is the target platform
        raise NotImplementedError(
            "GPIO hardware not yet implemented. Set HARDWARE_MODE=STUB for now."
        )

    def open_valve(self) -> None: ...
    def close_valve(self) -> None: ...
    def start_flow_counting(self, callback: FlowCallback) -> None: ...
    def stop_flow_counting(self) -> None: ...
    def is_estop_active(self) -> bool: ...


# ── Siemens S7 implementation (python-snap7) ─────────────────────────────────
# ⚠️  HARDWARE STUB — implement when S7-1200/S7-1500 model is confirmed.
#     Requires: pip install python-snap7

class Snap7Hardware(PumpHardware):
    """
    Siemens S7-1200 / S7-1500 control via python-snap7.
    PLC data block layout (example, adjust to your program):
      DB1.DBX0.0  — valve command bit  (write True = open)
      DB1.DBD2    — flow volume REAL   (read, litres, accumulated by PLC)
      DB1.DBX6.0  — emergency stop bit (read)
    """

    def __init__(self) -> None:
        # TODO: import snap7 and connect once PLC model/DB layout is confirmed
        raise NotImplementedError(
            "Snap7 hardware not yet implemented. Set HARDWARE_MODE=STUB for now."
        )

    def open_valve(self) -> None: ...
    def close_valve(self) -> None: ...
    def start_flow_counting(self, callback: FlowCallback) -> None: ...
    def stop_flow_counting(self) -> None: ...
    def is_estop_active(self) -> bool: ...


# ── Modbus TCP implementation ─────────────────────────────────────────────────
# ⚠️  HARDWARE STUB — implement when Modbus-capable PLC is confirmed.
#     Requires: pip install pymodbus

class ModbusHardware(PumpHardware):
    """
    Generic Modbus TCP PLC interface.
    Register map (example — adjust to your PLC program):
      Coil  0     — valve command  (write 1 = open)
      Coil  1     — emergency stop (read)
      Input 0–1   — flow volume as 32-bit float (litres, big-endian)
    """

    def __init__(self) -> None:
        # TODO: import pymodbus and configure once PLC register map is known
        raise NotImplementedError(
            "Modbus hardware not yet implemented. Set HARDWARE_MODE=STUB for now."
        )

    def open_valve(self) -> None: ...
    def close_valve(self) -> None: ...
    def start_flow_counting(self, callback: FlowCallback) -> None: ...
    def stop_flow_counting(self) -> None: ...
    def is_estop_active(self) -> bool: ...


# ── Factory ───────────────────────────────────────────────────────────────────

def get_hardware() -> PumpHardware:
    """Return the hardware implementation selected by HARDWARE_MODE."""
    mode = cfg.hardware_mode
    logger.info("Hardware mode: %s", mode)
    if mode == "GPIO":
        return GPIOHardware()
    if mode == "SNAP7":
        return Snap7Hardware()
    if mode == "MODBUS":
        return ModbusHardware()
    # Default / "STUB"
    return StubHardware()

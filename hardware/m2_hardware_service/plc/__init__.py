"""
plc/ — PLC-equivalent control logic for the ODROID-M2 station.

Mimics a PLC ladder-logic structure in Python, organised into:

  pump_sequence.py   — Piusi Suzzarablue AC pump state machine + 20-min duty cycle
  valve_control.py   — Solenoid valve sequencer with mechanical delays
  interlock.py       — Safety interlock chain (limit switch, level, duty cycle)
  station_plc.py     — Main scan loop (wires all PLC blocks together @ 100ms cycle)

Import StationPLC from this package and call plc.start() / plc.stop() from
hardware_manager.py.  All PLC blocks share the same RelayController instance
so there is a single authoritative source of truth for relay state.
"""

from .pump_sequence import PumpSequencer, PumpState
from .valve_control import ValveController, ValveState
from .interlock import InterLockChain, FaultCode
from .station_plc import StationPLC

__all__ = [
    "PumpSequencer", "PumpState",
    "ValveController", "ValveState",
    "InterLockChain", "FaultCode",
    "StationPLC",
]

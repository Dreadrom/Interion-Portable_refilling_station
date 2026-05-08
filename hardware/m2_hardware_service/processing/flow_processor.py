"""
flow_processor.py — On-chip flow and extrusion calculation.

Runs entirely on the ODROID-M2's RK3588S2 CPU.
Receives raw pulse increments from MK325Pulser and produces:

  volume_litres   — total volume dispensed this transaction
  flow_rate_lpm   — instantaneous flow rate (L/min), rolling 3-second window
  extrusion_pct   — percentage of target volume dispensed (0–100+%)
  elapsed_seconds — wall-clock seconds since dispense started

The results are made available as read-only properties so the HardwareManager
can publish them to MQTT and the local API on any schedule it chooses.

Design notes:
  - All methods are thread-safe (called from the pulser's callback thread
    AND read from the MQTT publisher thread simultaneously).
  - Flow rate uses a sliding window of (timestamp, volume) pairs to smooth
    out pulse jitter from the reed switch.
  - Extrusion % can exceed 100 — this signals the pump should be stopped if
    a max-volume limit was set.
"""

import threading
import time
import collections
from dataclasses import dataclass
from typing import Deque, Optional, Tuple


# Rolling window duration for flow-rate calculation (seconds)
_FLOW_RATE_WINDOW_S = 3.0


@dataclass
class FlowSnapshot:
    """Immutable snapshot of all computed flow metrics at a given instant."""
    volume_litres:    float
    flow_rate_lpm:    float
    extrusion_pct:    float
    elapsed_seconds:  float
    transaction_id:   Optional[str]


class FlowProcessor:
    """
    Accumulates MK325 pulse increments and computes flow metrics on-chip.

    Usage:
        processor = FlowProcessor()
        processor.start_transaction(txn_id="abc123", target_litres=20.0)
        # ...pulser calls processor.on_pulse(increment) on each tick...
        snapshot = processor.snapshot()
        processor.end_transaction()
    """

    def __init__(self) -> None:
        self._lock = threading.Lock()

        self._txn_id:         Optional[str]   = None
        self._target_litres:  float           = 0.0
        self._volume_litres:  float           = 0.0
        self._start_time:     Optional[float] = None

        # Rolling window: deque of (monotonic_time, volume_at_that_time)
        self._window: Deque[Tuple[float, float]] = collections.deque()

    # ── Transaction lifecycle ─────────────────────────────────────────────────

    def start_transaction(self, txn_id: str, target_litres: float = 0.0) -> None:
        """Reset all accumulators and start a new dispensing transaction."""
        with self._lock:
            self._txn_id        = txn_id
            self._target_litres = target_litres
            self._volume_litres = 0.0
            self._start_time    = time.monotonic()
            self._window.clear()

    def end_transaction(self) -> FlowSnapshot:
        """Freeze the final snapshot and clear transaction state."""
        snap = self.snapshot()
        with self._lock:
            self._txn_id     = None
            self._start_time = None
            self._window.clear()
        return snap

    # ── Pulse callback (called from pulser thread) ────────────────────────────

    def on_pulse(self, volume_increment_litres: float) -> None:
        """
        Called by MK325Pulser on every pulse.
        Adds the incremental volume and records a data point for rate calculation.
        """
        now = time.monotonic()
        with self._lock:
            self._volume_litres += volume_increment_litres
            self._window.append((now, self._volume_litres))
            # Purge data points older than the rolling window
            cutoff = now - _FLOW_RATE_WINDOW_S
            while self._window and self._window[0][0] < cutoff:
                self._window.popleft()

    # ── Read metrics (called from MQTT/API threads) ───────────────────────────

    def snapshot(self) -> FlowSnapshot:
        """Return a consistent, thread-safe snapshot of all flow metrics."""
        with self._lock:
            volume   = self._volume_litres
            elapsed  = (time.monotonic() - self._start_time) if self._start_time else 0.0
            rate     = self._compute_rate()
            extr_pct = (volume / self._target_litres * 100.0) if self._target_litres > 0 else 0.0
            txn_id   = self._txn_id

        return FlowSnapshot(
            volume_litres=round(volume,   3),
            flow_rate_lpm=round(rate,     2),
            extrusion_pct=round(extr_pct, 1),
            elapsed_seconds=round(elapsed, 1),
            transaction_id=txn_id,
        )

    @property
    def volume_litres(self) -> float:
        with self._lock:
            return self._volume_litres

    @property
    def is_over_target(self) -> bool:
        """True when dispensed volume has reached or exceeded the target."""
        with self._lock:
            return (
                self._target_litres > 0
                and self._volume_litres >= self._target_litres
            )

    # ── Internal ─────────────────────────────────────────────────────────────

    def _compute_rate(self) -> float:
        """
        Calculate instantaneous flow rate (L/min) from the rolling window.
        Must be called while holding self._lock.
        """
        if len(self._window) < 2:
            return 0.0

        oldest_time,   oldest_vol  = self._window[0]
        newest_time,   newest_vol  = self._window[-1]

        dt = newest_time - oldest_time
        dv = newest_vol  - oldest_vol

        if dt <= 0:
            return 0.0

        return (dv / dt) * 60.0   # convert L/s → L/min

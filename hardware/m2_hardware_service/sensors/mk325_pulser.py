"""
mk325_pulser.py — Piusi MK325 dual reed-switch pulser interface.

Hardware facts (from datasheet):
  - Output: two independent passive reed switches (S1=CH1, S2=CH2)
  - K-factor: ~160 pulses per litre
  - Contact rating: 28V AC/DC, 100mA max
  - Connection: supply your own V+ (3.3V from M2) + GND through each switch

GPIO wiring to ODROID-M2 (3.3V logic):
  MK325 V+   ──── 3.3V header pin
  MK325 GND  ──── GND header pin
  MK325 CH1  ──── GPIO line (pulser_ch1_line) — internal pull-up enabled
  MK325 CH2  ──── GPIO line (pulser_ch2_line) — internal pull-up enabled

  Reed switch closes to GND on each pulse → GPIO goes HIGH → LOW (falling edge).

Direction detection:
  CH2 falls AFTER CH1  → forward flow  (normal dispensing)
  CH2 falls BEFORE CH1 → reverse flow  (backflow — trigger alarm)

STUB mode: simulates pulses at a configurable rate without real hardware.
"""

import logging
import threading
import time
import random
from datetime import timedelta
from typing import Callable, Optional

logger = logging.getLogger(__name__)

# Type: called on each pulse with the volume increment in litres
PulseCallback = Callable[[float], None]

# Simulated flow rate in stub mode (litres/min)
_STUB_FLOW_LPM = 10.0
_STUB_TICK_S   = 0.1    # fire a simulated pulse every 100ms when "valve open"


class MK325Pulser:
    """
    Piusi MK325 pulse meter reader.

    Usage:
        pulser = MK325Pulser(cfg)
        pulser.start(on_pulse)          # registers interrupt, returns immediately
        # ... dispense ...
        pulser.stop()
        total = pulser.total_volume_litres
    """

    def __init__(self, cfg) -> None:
        self._cfg         = cfg
        self._k_factor    = cfg.pulser_k_factor        # pulses/litre
        self._debounce_s  = cfg.pulser_debounce_ms / 1000.0
        self._stub        = cfg.hardware_mode != "REAL"

        self._callback: Optional[PulseCallback] = None
        self._pulse_count  = 0
        self._lock         = threading.Lock()
        self._running      = False
        self._thread: Optional[threading.Thread] = None

        # gpiod objects (REAL mode only)
        self._chip        = None
        self._line_ch1    = None
        self._line_ch2    = None

        # Timing for direction detection
        self._last_ch1_time = 0.0
        self._last_ch2_time = 0.0

        # Stub: simulate valve open state
        self._stub_valve_open = False

    # ── Public API ────────────────────────────────────────────────────────────

    def start(self, callback: PulseCallback) -> None:
        """Begin pulse counting. `callback` is called on each pulse."""
        self._callback = callback
        self._pulse_count = 0
        self._running = True

        if self._stub:
            self._thread = threading.Thread(target=self._stub_loop, daemon=True)
        else:
            self._setup_gpio()
            self._thread = threading.Thread(target=self._gpio_poll_loop, daemon=True)

        self._thread.start()
        logger.info("MK325Pulser started (mode=%s, K=%.1f P/L)",
                    "STUB" if self._stub else "REAL", self._k_factor)

    def stop(self) -> None:
        """Stop pulse counting and release GPIO resources."""
        self._running = False
        if self._thread:
            self._thread.join(timeout=2)
        self._release_gpio()
        logger.info("MK325Pulser stopped — total pulses=%d, volume=%.3fL",
                    self._pulse_count, self.total_volume_litres)

    def open_stub_valve(self) -> None:
        """STUB only: simulate valve open (pulses start flowing)."""
        self._stub_valve_open = True

    def close_stub_valve(self) -> None:
        """STUB only: simulate valve closed (pulses stop)."""
        self._stub_valve_open = False

    def reset(self) -> None:
        """Reset pulse count (call at start of each transaction)."""
        with self._lock:
            self._pulse_count = 0

    @property
    def total_volume_litres(self) -> float:
        with self._lock:
            return self._pulse_count / self._k_factor

    @property
    def pulse_count(self) -> int:
        with self._lock:
            return self._pulse_count

    def is_reverse_flow(self) -> bool:
        """
        True if CH2 edge arrived before CH1 — indicates backflow.
        Only meaningful in REAL mode.
        """
        if self._stub:
            return False
        # If CH2 last fired more recently than CH1, flow is reversed
        return self._last_ch2_time > self._last_ch1_time + 0.002  # 2ms window

    # ── REAL mode — gpiod ────────────────────────────────────────────────────

    def _setup_gpio(self) -> None:
        try:
            import gpiod  # type: ignore
            self._chip = gpiod.Chip(self._cfg.gpio_chip)

            self._line_ch1 = self._chip.get_line(self._cfg.pulser_ch1_line)
            self._line_ch1.request(
                consumer="mk325-ch1",
                type=gpiod.LINE_REQ_EV_FALLING_EDGE,
                flags=gpiod.LINE_REQ_FLAG_BIAS_PULL_UP,
            )

            self._line_ch2 = self._chip.get_line(self._cfg.pulser_ch2_line)
            self._line_ch2.request(
                consumer="mk325-ch2",
                type=gpiod.LINE_REQ_EV_FALLING_EDGE,
                flags=gpiod.LINE_REQ_FLAG_BIAS_PULL_UP,
            )
            logger.info("MK325 GPIO lines opened — CH1=%d CH2=%d on %s",
                        self._cfg.pulser_ch1_line, self._cfg.pulser_ch2_line,
                        self._cfg.gpio_chip)
        except Exception as exc:
            logger.error("MK325 GPIO setup failed: %s", exc)
            raise

    def _gpio_poll_loop(self) -> None:
        """Block on CH1 falling-edge events (timeout 100ms so we can check _running)."""
        timeout = timedelta(milliseconds=100)
        while self._running:
            try:
                if self._line_ch1.event_wait(timeout):
                    event = self._line_ch1.event_read()
                    now = time.monotonic()

                    # Debounce: ignore pulses that arrive too fast
                    if now - self._last_ch1_time < self._debounce_s:
                        continue

                    self._last_ch1_time = now
                    self._fire_pulse()

                # Poll CH2 for direction (non-blocking)
                if self._line_ch2 and self._line_ch2.event_wait(timedelta(milliseconds=1)):
                    self._line_ch2.event_read()
                    self._last_ch2_time = time.monotonic()

            except Exception as exc:
                logger.error("MK325 GPIO poll error: %s", exc)
                time.sleep(0.1)

    def _release_gpio(self) -> None:
        for line in (self._line_ch1, self._line_ch2):
            if line:
                try:
                    line.release()
                except Exception:
                    pass
        if self._chip:
            try:
                self._chip.close()
            except Exception:
                pass
        self._line_ch1 = self._line_ch2 = self._chip = None

    # ── STUB mode — simulated pulses ─────────────────────────────────────────

    def _stub_loop(self) -> None:
        """Generate synthetic pulses at STUB_FLOW_LPM when stub valve is open."""
        # Pulses per tick = (LPM / 60) * tick_s * K_factor
        while self._running:
            time.sleep(_STUB_TICK_S)
            if self._stub_valve_open:
                pulses_per_tick = (_STUB_FLOW_LPM / 60.0) * _STUB_TICK_S * self._k_factor
                # Randomise slightly to simulate real jitter
                n = int(pulses_per_tick + random.uniform(-0.5, 0.5))
                for _ in range(max(1, n)):
                    self._fire_pulse()
                    time.sleep(0.001)   # brief spacing between pulses

    # ── Internal ─────────────────────────────────────────────────────────────

    def _fire_pulse(self) -> None:
        with self._lock:
            self._pulse_count += 1

        if self._callback:
            try:
                self._callback(1.0 / self._k_factor)
            except Exception as exc:
                logger.error("Pulse callback raised: %s", exc)

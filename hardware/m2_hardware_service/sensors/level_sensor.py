"""
level_sensor.py — Gauge / tank level sensor via RS485 Modbus RTU.

Hardware wiring:
  Level sensor RS485 A+ ──── USB-RS485 adapter A+  (BOM C3, /dev/ttyUSB0)
  Level sensor RS485 B- ──── USB-RS485 adapter B-
  Level sensor power    ──── 12V or 24V (per your sensor datasheet)

Register format (configure via .env to match your sensor):
  Default: holding register 0, slave address 1
  Raw value example: 0–1000 → 0.0–100.0% with LEVEL_SENSOR_SCALE=0.1
  Adjust LEVEL_SENSOR_REG, LEVEL_SENSOR_ADDR, LEVEL_SENSOR_SCALE to match
  your sensor's register map.

STUB mode: returns a simulated tank level that drains slowly during dispensing.
"""

import logging
import threading
import time
from typing import Optional

logger = logging.getLogger(__name__)

_STUB_INITIAL_LEVEL = 78.0   # %
_STUB_DRAIN_RATE    = 0.05   # % per second when valve is open


class LevelSensor:
    """
    Reads tank fill level from a Modbus RTU gauge sensor over USB-RS485.

    Usage:
        sensor = LevelSensor(cfg)
        sensor.connect()
        pct = sensor.read_level()   # 0.0–100.0 (%)
        sensor.close()
    """

    def __init__(self, cfg) -> None:
        self._cfg    = cfg
        self._stub   = cfg.hardware_mode != "REAL"
        self._client = None
        self._lock   = threading.Lock()

        # Stub state
        self._stub_level      = _STUB_INITIAL_LEVEL
        self._stub_valve_open = False
        self._stub_last_time  = time.monotonic()

    # ── Public API ────────────────────────────────────────────────────────────

    def connect(self) -> None:
        """Open the serial port and verify Modbus communication."""
        if self._stub:
            logger.info("LevelSensor: STUB mode — no serial port opened")
            return

        try:
            from pymodbus.client import ModbusSerialClient  # type: ignore

            self._client = ModbusSerialClient(
                port=self._cfg.rs485_port,
                baudrate=self._cfg.rs485_baudrate,
                parity=self._cfg.rs485_parity,
                stopbits=self._cfg.rs485_stopbits,
                bytesize=8,
                timeout=self._cfg.level_sensor_timeout_s,
            )
            if not self._client.connect():
                raise ConnectionError(
                    f"Could not open serial port {self._cfg.rs485_port}"
                )
            logger.info(
                "LevelSensor: connected on %s (slave=%d, reg=%d, scale=%.3f)",
                self._cfg.rs485_port,
                self._cfg.level_sensor_addr,
                self._cfg.level_sensor_reg,
                self._cfg.level_sensor_scale,
            )
        except Exception as exc:
            logger.error("LevelSensor connect failed: %s", exc)
            raise

    def read_level(self) -> Optional[float]:
        """
        Return tank fill level as a float percentage (0.0–100.0).
        Returns None if the Modbus read fails.
        """
        if self._stub:
            return self._stub_read()

        with self._lock:
            try:
                result = self._client.read_holding_registers(
                    address=self._cfg.level_sensor_reg,
                    count=1,
                    slave=self._cfg.level_sensor_addr,
                )
                if result.isError():
                    logger.warning("LevelSensor: Modbus read error — %s", result)
                    return None

                raw   = result.registers[0]
                level = raw * self._cfg.level_sensor_scale
                level = max(0.0, min(100.0, level))   # clamp to safe range
                logger.debug("LevelSensor: raw=%d → %.2f%%", raw, level)
                return level

            except Exception as exc:
                logger.error("LevelSensor read error: %s", exc)
                return None

    def close(self) -> None:
        if self._client:
            try:
                self._client.close()
            except Exception:
                pass
        logger.info("LevelSensor: closed")

    # ── Stub helpers ─────────────────────────────────────────────────────────

    def set_stub_valve_open(self, open: bool) -> None:
        """STUB only: notify sensor whether dispensing is active (drives simulated drain)."""
        self._stub_valve_open = open

    # ── Internal ─────────────────────────────────────────────────────────────

    def _stub_read(self) -> float:
        now     = time.monotonic()
        elapsed = now - self._stub_last_time
        self._stub_last_time = now

        if self._stub_valve_open and self._stub_level > 0:
            self._stub_level = max(0.0, self._stub_level - _STUB_DRAIN_RATE * elapsed)

        return round(self._stub_level, 2)

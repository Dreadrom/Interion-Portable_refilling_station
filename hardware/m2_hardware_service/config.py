"""
config.py — Load and validate all environment config for the ODROID-M2
hardware service.

All values are read from .env (or real environment) once at import time.
Import `cfg` everywhere config is needed.

Hardware mode:
  REAL  — Full GPIO + RS485 Modbus RTU (production on M2)
  STUB  — Fully simulated (development, no hardware needed)
"""

import os
from dataclasses import dataclass
from dotenv import load_dotenv

load_dotenv()


@dataclass(frozen=True)
class M2Config:
    # ── Station identity ────────────────────────────────────────────────────
    station_id:   str
    station_name: str

    # ── AWS IoT Core ─────────────────────────────────────────────────────────
    iot_endpoint:       str
    iot_port:           int
    cert_path:          str
    key_path:           str
    ca_path:            str
    mqtt_topic_prefix:  str

    # ── Hardware mode ────────────────────────────────────────────────────────
    hardware_mode: str   # REAL | STUB

    # ── GPIO chip (ODROID-M2, Ubuntu) ────────────────────────────────────────
    # RK3588S2 exposes banks via /dev/gpiochip0 .. /dev/gpiochip4
    # Check `gpiodetect` to confirm the correct chip on your board.
    gpio_chip: str

    # ── MK325 Pulser GPIO lines ───────────────────────────────────────────────
    # CH1 = forward pulse count (falling edge = 1 pulse = 1/K_FACTOR litres)
    # CH2 = direction detection (leads/lags CH1 to indicate reverse flow)
    pulser_ch1_line:    int
    pulser_ch2_line:    int
    pulser_k_factor:    float   # pulses per litre — MK325 ≈ 160 P/L
    pulser_debounce_ms: int     # ignore pulses within this window (anti-bounce)

    # ── Pump motor relay GPIO line ────────────────────────────────────────────
    # Driving a 4-ch opto-isolated relay board (BOM C4).
    # Set active_high=True if HIGH level energises the relay coil.
    # Many opto-isolated boards are active-LOW — set False in that case.
    pump_relay_line:        int
    pump_relay_active_high: bool

    # ── Solenoid valve relay GPIO line ───────────────────────────────────────
    valve_relay_line:        int
    valve_relay_active_high: bool

    # ── Limit switch GPIO line ────────────────────────────────────────────────
    # Wire as Normally-Closed (NC).  Pull-up enabled internally.
    # GPIO reads HIGH = circuit intact (OK), LOW = switch tripped (fault/full).
    limit_switch_line: int

    # ── RS485 Modbus RTU — gauge level sensor (USB-RS485 adapter, BOM C3) ────
    rs485_port:          str    # /dev/ttyUSB0
    rs485_baudrate:      int
    rs485_parity:        str    # N / E / O
    rs485_stopbits:      int
    level_sensor_addr:   int    # Modbus slave address (1–247)
    level_sensor_reg:    int    # holding register address for tank level
    level_sensor_scale:  float  # raw_value × scale = level % (e.g. 750 × 0.1 = 75.0)
    level_sensor_timeout_s: float

    # ── Polling & telemetry intervals ────────────────────────────────────────
    sensor_poll_interval_s: float   # level + limit switch poll cadence
    heartbeat_interval_s:   float   # MQTT heartbeat cadence

    # ── Diagnostics API ──────────────────────────────────────────────────────
    diag_api_port: int

    # ── Logging ──────────────────────────────────────────────────────────────
    log_level: str


def _load() -> M2Config:
    return M2Config(
        station_id=os.environ["STATION_ID"],
        station_name=os.getenv("STATION_NAME", os.environ["STATION_ID"]),

        iot_endpoint=os.environ["AWS_IOT_ENDPOINT"],
        iot_port=int(os.getenv("AWS_IOT_PORT", "8883")),
        cert_path=os.environ["IOT_CERT_PATH"],
        key_path=os.environ["IOT_KEY_PATH"],
        ca_path=os.environ["IOT_CA_PATH"],
        mqtt_topic_prefix=os.getenv("MQTT_TOPIC_PREFIX", "acerev/stations"),

        hardware_mode=os.getenv("HARDWARE_MODE", "STUB").upper(),

        # ODROID-M2: run `gpiodetect` to find the correct chip.
        # gpiochip0 (Bank 0: GPIO0_A0..D7), gpiochip1 (Bank 1: GPIO1_A0..D7), etc.
        gpio_chip=os.getenv("GPIO_CHIP", "/dev/gpiochip1"),

        # MK325 pulser — connect to 40-pin header GPIO lines (gpiod numbering)
        # Default: GPIO1_A4 (line 4) = CH1, GPIO1_A5 (line 5) = CH2
        pulser_ch1_line=int(os.getenv("PULSER_CH1_LINE", "4")),
        pulser_ch2_line=int(os.getenv("PULSER_CH2_LINE", "5")),
        pulser_k_factor=float(os.getenv("PULSER_K_FACTOR", "160.0")),
        pulser_debounce_ms=int(os.getenv("PULSER_DEBOUNCE_MS", "5")),

        # Pump relay — GPIO1_A6 (line 6) default
        pump_relay_line=int(os.getenv("PUMP_RELAY_LINE", "6")),
        pump_relay_active_high=os.getenv("PUMP_RELAY_ACTIVE_HIGH", "true").lower() == "true",

        # Valve relay — GPIO1_A7 (line 7) default
        valve_relay_line=int(os.getenv("VALVE_RELAY_LINE", "7")),
        valve_relay_active_high=os.getenv("VALVE_RELAY_ACTIVE_HIGH", "true").lower() == "true",

        # Limit switch — GPIO1_B0 (line 8) default
        limit_switch_line=int(os.getenv("LIMIT_SWITCH_LINE", "8")),

        # USB-RS485 serial port
        rs485_port=os.getenv("RS485_PORT", "/dev/ttyUSB0"),
        rs485_baudrate=int(os.getenv("RS485_BAUDRATE", "9600")),
        rs485_parity=os.getenv("RS485_PARITY", "N"),
        rs485_stopbits=int(os.getenv("RS485_STOPBITS", "1")),
        level_sensor_addr=int(os.getenv("LEVEL_SENSOR_ADDR", "1")),
        level_sensor_reg=int(os.getenv("LEVEL_SENSOR_REG", "0")),
        level_sensor_scale=float(os.getenv("LEVEL_SENSOR_SCALE", "0.1")),
        level_sensor_timeout_s=float(os.getenv("LEVEL_SENSOR_TIMEOUT_S", "1.0")),

        sensor_poll_interval_s=float(os.getenv("SENSOR_POLL_INTERVAL_S", "5.0")),
        heartbeat_interval_s=float(os.getenv("HEARTBEAT_INTERVAL_S", "30.0")),

        diag_api_port=int(os.getenv("DIAG_API_PORT", "8080")),
        log_level=os.getenv("LOG_LEVEL", "INFO"),
    )


cfg = _load()

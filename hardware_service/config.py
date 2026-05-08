"""
config.py — Load and validate all environment variables.

Values are read from the .env file (or real environment) once at import time.
Import `cfg` from this module everywhere you need configuration.
"""

import os
from dataclasses import dataclass
from dotenv import load_dotenv

load_dotenv()


@dataclass(frozen=True)
class Config:
    # Station
    station_id: str
    station_name: str

    # AWS IoT Core
    iot_endpoint: str
    iot_port: int
    cert_path: str
    key_path: str
    ca_path: str
    mqtt_topic_prefix: str

    # Hardware mode
    hardware_mode: str  # STUB | GPIO | SNAP7 | MODBUS

    # Siemens S7
    plc_host: str
    plc_rack: int
    plc_slot: int

    # Modbus
    modbus_host: str
    modbus_port: int

    # RPi GPIO
    gpio_valve_pin: int
    gpio_flow_pin: int
    gpio_estop_pin: int
    flow_pulses_per_litre: int

    # Diagnostics API
    diag_api_port: int

    # Logging
    log_level: str


def _load() -> Config:
    return Config(
        station_id=os.environ["STATION_ID"],
        station_name=os.getenv("STATION_NAME", os.environ["STATION_ID"]),

        iot_endpoint=os.environ["AWS_IOT_ENDPOINT"],
        iot_port=int(os.getenv("AWS_IOT_PORT", "8883")),
        cert_path=os.environ["IOT_CERT_PATH"],
        key_path=os.environ["IOT_KEY_PATH"],
        ca_path=os.environ["IOT_CA_PATH"],
        mqtt_topic_prefix=os.getenv("MQTT_TOPIC_PREFIX", "acerev/stations"),

        hardware_mode=os.getenv("HARDWARE_MODE", "STUB").upper(),

        plc_host=os.getenv("PLC_HOST", "192.168.1.50"),
        plc_rack=int(os.getenv("PLC_RACK", "0")),
        plc_slot=int(os.getenv("PLC_SLOT", "1")),

        modbus_host=os.getenv("MODBUS_HOST", "192.168.1.50"),
        modbus_port=int(os.getenv("MODBUS_PORT", "502")),

        gpio_valve_pin=int(os.getenv("GPIO_VALVE_PIN", "17")),
        gpio_flow_pin=int(os.getenv("GPIO_FLOW_PIN", "27")),
        gpio_estop_pin=int(os.getenv("GPIO_ESTOP_PIN", "22")),
        flow_pulses_per_litre=int(os.getenv("FLOW_PULSES_PER_LITRE", "450")),

        diag_api_port=int(os.getenv("DIAG_API_PORT", "8080")),
        log_level=os.getenv("LOG_LEVEL", "INFO"),
    )


cfg = _load()

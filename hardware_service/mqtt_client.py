"""
mqtt_client.py — AWS IoT Core MQTT connection using mTLS certificates.

Handles:
  - Connection lifecycle (connect, reconnect on drop)
  - Publishing telemetry and status messages
  - Subscribing to command topics
    - Structured topic naming:  bluediesel/stations/{station_id}/{subtopic}

Topic schema
────────────
    bluediesel/stations/{id}/commands        ← backend → RPi (dispense, stop, ping)
    bluediesel/stations/{id}/telemetry/flow  ← RPi → backend (volume, rate, elapsed)
    bluediesel/stations/{id}/telemetry/status← RPi → backend (IDLE, DISPENSING, ALARM…)
    bluediesel/stations/{id}/heartbeat       ← RPi → backend (keepalive every 30 s)
    bluediesel/stations/{id}/auth/challenge  ← RPi → backend (pump auth code ready)
    bluediesel/stations/{id}/auth/response   ← backend → RPi (code accepted/rejected)
"""

import json
import logging
import threading
from typing import Callable

from awscrt import mqtt
from awsiot import mqtt_connection_builder

from config import cfg

logger = logging.getLogger(__name__)

# Type alias for message handler callbacks
MessageHandler = Callable[[str, dict], None]


class MQTTClient:
    def __init__(self) -> None:
        self._connection: mqtt.Connection | None = None
        self._connected = threading.Event()
        self._handlers: dict[str, list[MessageHandler]] = {}

    # ── Topic helpers ────────────────────────────────────────────────────────

    def _topic(self, subtopic: str) -> str:
        return f"{cfg.mqtt_topic_prefix}/{cfg.station_id}/{subtopic}"

    # ── Connection ───────────────────────────────────────────────────────────

    def connect(self) -> None:
        """Build and open the MQTT connection. Blocks until connected."""
        self._connection = mqtt_connection_builder.mtls_from_path(
            endpoint=cfg.iot_endpoint,
            port=cfg.iot_port,
            cert_filepath=cfg.cert_path,
            pri_key_filepath=cfg.key_path,
            ca_filepath=cfg.ca_path,
            client_id=f"bluediesel-station-{cfg.station_id}",
            clean_session=False,
            keep_alive_secs=30,
            on_connection_interrupted=self._on_interrupted,
            on_connection_resumed=self._on_resumed,
        )

        connect_future = self._connection.connect()
        connect_future.result()  # raises on failure
        self._connected.set()
        logger.info("MQTT connected to %s", cfg.iot_endpoint)
        self._subscribe_all()

    def disconnect(self) -> None:
        if self._connection:
            self._connection.disconnect().result()
            self._connected.clear()
            logger.info("MQTT disconnected")

    # ── Subscribe ────────────────────────────────────────────────────────────

    def _subscribe_all(self) -> None:
        """Subscribe to all inbound command topics."""
        topics = [
            "commands",
            "auth/response",
        ]
        for subtopic in topics:
            topic = self._topic(subtopic)
            self._connection.subscribe(
                topic=topic,
                qos=mqtt.QoS.AT_LEAST_ONCE,
                callback=self._on_message,
            ).result()
            logger.debug("Subscribed to %s", topic)

    def on(self, subtopic: str, handler: MessageHandler) -> None:
        """Register a handler for a given subtopic (e.g. 'commands')."""
        self._handlers.setdefault(subtopic, []).append(handler)

    def _on_message(self, topic: str, payload: bytes, **_kwargs) -> None:
        try:
            data = json.loads(payload.decode("utf-8"))
        except json.JSONDecodeError:
            logger.warning("Received non-JSON payload on %s", topic)
            return

        # Extract subtopic from full topic path
        prefix = f"{cfg.mqtt_topic_prefix}/{cfg.station_id}/"
        subtopic = topic[len(prefix):] if topic.startswith(prefix) else topic

        logger.debug("MQTT ← %s: %s", subtopic, data)
        for handler in self._handlers.get(subtopic, []):
            try:
                handler(subtopic, data)
            except Exception as exc:
                logger.exception("Handler error for %s: %s", subtopic, exc)

    # ── Publish ──────────────────────────────────────────────────────────────

    def publish(self, subtopic: str, payload: dict, qos: int = 1) -> None:
        """Publish a JSON message to a station subtopic."""
        if not self._connection:
            logger.warning("MQTT publish skipped — not connected")
            return
        topic = self._topic(subtopic)
        self._connection.publish(
            topic=topic,
            payload=json.dumps(payload),
            qos=mqtt.QoS.AT_LEAST_ONCE if qos >= 1 else mqtt.QoS.AT_MOST_ONCE,
        )
        logger.debug("MQTT → %s: %s", subtopic, payload)

    def publish_status(self, status: str, extra: dict | None = None) -> None:
        self.publish("telemetry/status", {"status": status, **(extra or {})})

    def publish_flow(self, volume_litres: float, rate_lpm: float, elapsed_sec: int) -> None:
        self.publish("telemetry/flow", {
            "volumeLitres": round(volume_litres, 3),
            "rateLPM": round(rate_lpm, 2),
            "elapsedSeconds": elapsed_sec,
        })

    def publish_heartbeat(self) -> None:
        self.publish("heartbeat", {"stationId": cfg.station_id})

    # ── Reconnect callbacks ──────────────────────────────────────────────────

    def _on_interrupted(self, connection, error, **_kwargs) -> None:
        logger.warning("MQTT connection interrupted: %s", error)
        self._connected.clear()

    def _on_resumed(self, connection, return_code, session_present, **_kwargs) -> None:
        logger.info("MQTT connection resumed (rc=%s, session=%s)", return_code, session_present)
        self._connected.set()
        self._subscribe_all()

"""
mqtt_uploader.py — AWS IoT Core MQTT publisher for the ODROID-M2.

Uses the same mTLS certificate + topic schema as the existing hardware_service,
so the backend (IoTBridgeHandler.ts) requires no changes.

Topic schema (all prefixed acerev/stations/{station_id}/):
  PUBLISH  telemetry/flow     — volume, rate, extrusion% every tick
  PUBLISH  telemetry/sensors  — tank level, limit switch state every poll
  PUBLISH  telemetry/status   — state machine status changes
  PUBLISH  telemetry/complete — transaction completion summary
  PUBLISH  auth/challenge     — pump auth code ready for backend relay
  PUBLISH  heartbeat          — keepalive every 30s
  SUBSCRIBE commands          — AUTHORIZE / STOP / PING from backend
  SUBSCRIBE auth/response     — auth code accepted/rejected from backend

Callbacks registered via:
  uploader.on_command(fn)       → fn(data: dict) called on commands topic
  uploader.on_auth_response(fn) → fn(data: dict) called on auth/response topic
"""

import json
import logging
import threading
from typing import Callable, Optional

logger = logging.getLogger(__name__)

CommandHandler = Callable[[dict], None]


class MQTTUploader:
    """
    Manages the AWS IoT Core MQTT connection and all publish/subscribe operations.

    Usage:
        uploader = MQTTUploader(cfg)
        uploader.on_command(my_command_handler)
        uploader.connect()          # blocks until connected
        uploader.publish_heartbeat()
        # ... during dispense ...
        uploader.publish_flow(snapshot)
        uploader.disconnect()
    """

    def __init__(self, cfg) -> None:
        self._cfg        = cfg
        self._connection = None
        self._connected  = threading.Event()
        self._command_handlers:      list[CommandHandler] = []
        self._auth_response_handlers: list[CommandHandler] = []

    # ── Connection ────────────────────────────────────────────────────────────

    def connect(self) -> None:
        """Build mTLS connection to AWS IoT Core and subscribe to inbound topics."""
        try:
            from awscrt import mqtt                        # type: ignore
            from awsiot import mqtt_connection_builder     # type: ignore

            self._connection = mqtt_connection_builder.mtls_from_path(
                endpoint=self._cfg.iot_endpoint,
                port=self._cfg.iot_port,
                cert_filepath=self._cfg.cert_path,
                pri_key_filepath=self._cfg.key_path,
                ca_filepath=self._cfg.ca_path,
                client_id=f"acerev-m2-{self._cfg.station_id}",
                clean_session=False,
                keep_alive_secs=30,
                on_connection_interrupted=self._on_interrupted,
                on_connection_resumed=self._on_resumed,
            )
            self._connection.connect().result()
            self._connected.set()
            logger.info("MQTT: connected to %s", self._cfg.iot_endpoint)
            self._subscribe_all()

        except Exception as exc:
            logger.error("MQTT connect failed: %s", exc)
            raise

    def disconnect(self) -> None:
        if self._connection:
            self._connection.disconnect().result()
            self._connected.clear()
            logger.info("MQTT: disconnected")

    # ── Register handlers ─────────────────────────────────────────────────────

    def on_command(self, fn: CommandHandler) -> None:
        self._command_handlers.append(fn)

    def on_auth_response(self, fn: CommandHandler) -> None:
        self._auth_response_handlers.append(fn)

    # ── Publish helpers ───────────────────────────────────────────────────────

    def publish_flow(self, snapshot) -> None:
        """Publish flow telemetry from a FlowSnapshot."""
        self._publish("telemetry/flow", {
            "transactionId":  snapshot.transaction_id,
            "volumeLitres":   snapshot.volume_litres,
            "rateLPM":        snapshot.flow_rate_lpm,
            "extrusionPct":   snapshot.extrusion_pct,
            "elapsedSeconds": snapshot.elapsed_seconds,
        })

    def publish_sensors(self, tank_level_pct: Optional[float], limit_tripped: bool) -> None:
        """Publish background sensor telemetry (tank level + limit switch)."""
        self._publish("telemetry/sensors", {
            "stationId":      self._cfg.station_id,
            "tankLevelPct":   tank_level_pct,
            "limitTripped":   limit_tripped,
        })

    def publish_status(self, status: str, txn_id: Optional[str] = None) -> None:
        payload: dict = {"status": status, "stationId": self._cfg.station_id}
        if txn_id:
            payload["transactionId"] = txn_id
        self._publish("telemetry/status", payload)

    def publish_complete(self, txn_id: str, volume: float,
                         amount: float, stop_reason: str) -> None:
        self._publish("telemetry/complete", {
            "transactionId": txn_id,
            "volumeLitres":  volume,
            "amountCharged": amount,
            "stopReason":    stop_reason,
        })

    def publish_auth_challenge(self, txn_id: str, code: str) -> None:
        self._publish("auth/challenge", {
            "transactionId": txn_id,
            "code":          code,
            "ready":         True,
        })

    def publish_heartbeat(self) -> None:
        self._publish("heartbeat", {
            "stationId":   self._cfg.station_id,
            "stationName": self._cfg.station_name,
        })

    # ── Internal ─────────────────────────────────────────────────────────────

    def _topic(self, subtopic: str) -> str:
        return f"{self._cfg.mqtt_topic_prefix}/{self._cfg.station_id}/{subtopic}"

    def _publish(self, subtopic: str, payload: dict) -> None:
        if not self._connection or not self._connected.is_set():
            logger.debug("MQTT not connected — dropping %s", subtopic)
            return
        try:
            from awscrt import mqtt  # type: ignore
            self._connection.publish(
                topic=self._topic(subtopic),
                payload=json.dumps(payload),
                qos=mqtt.QoS.AT_LEAST_ONCE,
            )
            logger.debug("MQTT → %s: %s", subtopic, payload)
        except Exception as exc:
            logger.error("MQTT publish error (%s): %s", subtopic, exc)

    def _subscribe_all(self) -> None:
        for subtopic, dispatcher in [
            ("commands",      self._dispatch_command),
            ("auth/response", self._dispatch_auth_response),
        ]:
            try:
                from awscrt import mqtt  # type: ignore
                self._connection.subscribe(
                    topic=self._topic(subtopic),
                    qos=mqtt.QoS.AT_LEAST_ONCE,
                    callback=lambda topic, payload, **_: dispatcher(payload),
                ).result()
                logger.info("MQTT: subscribed to %s", self._topic(subtopic))
            except Exception as exc:
                logger.error("MQTT subscribe error (%s): %s", subtopic, exc)

    def _dispatch_command(self, payload: bytes) -> None:
        try:
            data = json.loads(payload)
        except Exception:
            logger.warning("MQTT: invalid JSON on commands topic")
            return
        for fn in self._command_handlers:
            try:
                fn(data)
            except Exception as exc:
                logger.error("Command handler raised: %s", exc)

    def _dispatch_auth_response(self, payload: bytes) -> None:
        try:
            data = json.loads(payload)
        except Exception:
            logger.warning("MQTT: invalid JSON on auth/response topic")
            return
        for fn in self._auth_response_handlers:
            try:
                fn(data)
            except Exception as exc:
                logger.error("Auth response handler raised: %s", exc)

    def _on_interrupted(self, connection, error, **kwargs) -> None:
        self._connected.clear()
        logger.warning("MQTT: connection interrupted — %s", error)

    def _on_resumed(self, connection, return_code, session_present, **kwargs) -> None:
        self._connected.set()
        logger.info("MQTT: connection resumed (session_present=%s)", session_present)
        self._subscribe_all()

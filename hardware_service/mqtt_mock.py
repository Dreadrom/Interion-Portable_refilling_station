"""Mock MQTT client for testing without AWS IoT Core"""
import logging
import json

logger = logging.getLogger(__name__)


class MQTTClient:
    """Simulated MQTT client for local testing without AWS IoT infrastructure"""
    
    def __init__(self) -> None:
        self._handlers = {}
        self._connected = False
        logger.info("Mock MQTT client initialized")
        
    def connect(self) -> None:
        """Simulate connection to AWS IoT Core"""
        self._connected = True
        logger.info("✅ MOCK MQTT: Connected (simulated - no AWS IoT Core needed)")
        
    def disconnect(self) -> None:
        """Simulate disconnection"""
        self._connected = False
        logger.info("MOCK MQTT: Disconnected (simulated)")
        
    def publish(self, topic: str, payload) -> None:
        """Simulate publishing a message (accepts dict or JSON string)"""
        if isinstance(payload, dict):
            logger.info(f"📤 MOCK MQTT: Published to [{topic}]")
            logger.info(f"   Payload: {json.dumps(payload, indent=2)}")
        elif isinstance(payload, str):
            try:
                parsed = json.loads(payload)
                logger.info(f"📤 MOCK MQTT: Published to [{topic}]")
                logger.info(f"   Payload: {json.dumps(parsed, indent=2)}")
            except json.JSONDecodeError:
                logger.info(f"📤 MOCK MQTT: Published to [{topic}]: {payload}")
        else:
            logger.info(f"📤 MOCK MQTT: Published to [{topic}]: {payload}")
        
    def publish_status(self, status: str, extra: dict = None) -> None:
        """Simulate publishing status update"""
        payload = {"status": status}
        if extra:
            payload.update(extra)
        logger.info(f"📊 MOCK MQTT: Status = {status}" + (f" | {extra}" if extra else ""))
        
    def publish_flow(self, volume: float, rate: float, elapsed: float) -> None:
        """Simulate publishing flow telemetry"""
        logger.info(f"🌊 MOCK MQTT: Flow telemetry → Volume: {volume:.2f}L | Rate: {rate:.1f} LPM | Time: {elapsed:.0f}s")
        
    def publish_completion(self, transaction_id: str, volume: float, stop_reason: str) -> None:
        """Simulate publishing completion"""
        logger.info(f"✅ MOCK MQTT: Dispense complete → TX: {transaction_id} | Volume: {volume:.2f}L | Reason: {stop_reason}")
        
    def publish_heartbeat(self) -> None:
        """Simulate publishing heartbeat"""
        logger.debug("💓 MOCK MQTT: Heartbeat sent")
        
    def subscribe(self, topic: str, handler) -> None:
        """Simulate subscribing to a topic"""
        self._handlers[topic] = handler
        logger.info(f"📥 MOCK MQTT: Subscribed to [{topic}]")
    
    def on(self, subtopic: str, handler) -> None:
        """Register a handler for a subtopic (simplified subscribe)"""
        self._handlers[subtopic] = handler
        logger.info(f"📥 MOCK MQTT: Handler registered for [{subtopic}]")
        
    def _on_message(self, topic: str, payload: dict) -> None:
        """Simulate receiving a message"""
        if topic in self._handlers:
            self._handlers[topic](topic, payload)

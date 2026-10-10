"""
IoT Gateway Consumer — assina MQTT labs/presence e republica no RabbitMQ.
Idempotência por (deviceId, seq) evita duplicidade após QoS 1 / reentrega.
"""
import json
import sys

import paho.mqtt.client as mqtt
import pika

MQTT_BROKER = "localhost"
MQTT_PORT = 1883
MQTT_TOPIC = "labs/presence"

AMQP_HOST = "localhost"
AMQP_EXCHANGE = "gamificacao.events"
AMQP_ROUTING_KEY = "iot.presence"

_seen: set[tuple[str, int]] = set()


def publish_amqp(event: dict) -> None:
    credentials = pika.PlainCredentials("gamificacao", "gamificacao")
    params = pika.ConnectionParameters(host=AMQP_HOST, credentials=credentials)
    connection = pika.BlockingConnection(params)
    channel = connection.channel()
    channel.exchange_declare(exchange=AMQP_EXCHANGE, exchange_type="topic", durable=True)
    channel.basic_publish(
        exchange=AMQP_EXCHANGE,
        routing_key=AMQP_ROUTING_KEY,
        body=json.dumps(event).encode("utf-8"),
    )
    connection.close()


def on_message(client, userdata, msg):
    raw = msg.payload.decode("utf-8")
    try:
        data = json.loads(raw)
    except json.JSONDecodeError:
        print(f"[gateway] payload inválido descartado: {raw}", file=sys.stderr)
        return

    device_id = data.get("deviceId", "unknown")
    seq = int(data.get("seq", -1))
    key = (device_id, seq)

    if key in _seen:
        print(f"[gateway] duplicata ignorada device={device_id} seq={seq}")
        return

    _seen.add(key)
    normalized = {
        "event": "LabPresenceEvent",
        "deviceId": device_id,
        "seq": seq,
        "lab": data.get("lab"),
        "ts": data.get("ts"),
        "sourceTopic": msg.topic,
    }
    publish_amqp(normalized)
    print(f"[gateway] MQTT→AMQP device={device_id} seq={seq}")


def main() -> None:
    client = mqtt.Client(
        mqtt.CallbackAPIVersion.VERSION2,
        client_id="iot-gateway-consumer",
        protocol=mqtt.MQTTv311,
    )
    client.on_message = on_message
    client.connect(MQTT_BROKER, MQTT_PORT, keepalive=60)
    client.subscribe(MQTT_TOPIC, qos=1)
    print(f"[gateway] inscrito em {MQTT_TOPIC} (QoS 1) → RabbitMQ {AMQP_ROUTING_KEY}")
    client.loop_forever()


if __name__ == "__main__":
    main()

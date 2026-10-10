"""
Simulador ESP32 — publica presença no tópico labs/presence com QoS 1.
Simula oscilação de Wi-Fi: pausa 30s e retoma; o broker entrega o backlog.
"""
import json
import time
import uuid
from datetime import datetime, timezone

import paho.mqtt.client as mqtt

BROKER = "localhost"
PORT = 1883
TOPIC = "labs/presence"
CLIENT_ID = f"esp32-lab-{uuid.uuid4().hex[:6]}"


def on_connect(client, userdata, flags, reason_code, properties=None):
    print(f"[sensor] conectado rc={reason_code}")


def publish_presence(client: mqtt.Client, seq: int) -> None:
    payload = {
        "deviceId": CLIENT_ID,
        "seq": seq,
        "event": "presence",
        "lab": "lab-iot-01",
        "ts": datetime.now(timezone.utc).isoformat(),
    }
    info = client.publish(TOPIC, json.dumps(payload), qos=1)
    info.wait_for_publish()
    print(f"[sensor] seq={seq} PUBACK ok")


def main() -> None:
    client = mqtt.Client(
        mqtt.CallbackAPIVersion.VERSION2,
        client_id=CLIENT_ID,
        protocol=mqtt.MQTTv311,
    )
    client.on_connect = on_connect
    client.connect(BROKER, PORT, keepalive=60)
    client.loop_start()

    seq = 0
    print("[sensor] publicando presença (QoS 1)...")
    for _ in range(5):
        seq += 1
        publish_presence(client, seq)
        time.sleep(1)

    print("[sensor] >>> OSCILAÇÃO Wi-Fi 30s (desconecta / sem publish) <<<")
    client.loop_stop()
    client.disconnect()
    time.sleep(30)

    print("[sensor] Wi-Fi restabelecido — reconecta e drena backlog local + novas leituras")
    client.connect(BROKER, PORT, keepalive=60)
    client.loop_start()
    for _ in range(5):
        seq += 1
        publish_presence(client, seq)
        time.sleep(1)

    print(f"[sensor] total publicado: {seq} (seqs únicos — sem duplicidade de seq)")
    client.loop_stop()
    client.disconnect()


if __name__ == "__main__":
    main()

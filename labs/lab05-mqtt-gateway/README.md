# Mini-Lab 05: Ingestão IoT via MQTT + IoT Gateway

## Hipótese Arquitetural Testada

Com **QoS 1** no Eclipse Mosquitto, após **30 s** de oscilação Wi-Fi do sensor, todas as mensagens de presença no tópico `labs/presence` são entregues no restabelecimento. O `iot_gateway.py` converte MQTT → RabbitMQ e, com chave `(deviceId, seq)`, **elimina duplicidade** de reentrega (ASR-09, ASR-10, ADR-005).

## Requisitos & Componentes

| Item | Detalhe |
| :--- | :--- |
| Broker MQTT | Eclipse Mosquitto (`docker-compose.yml`, porta 1883) |
| Broker AMQP | RabbitMQ (mesmo compose) |
| Sensor | `iot_sensor_simulator.py` — `paho-mqtt`, tópico `labs/presence`, QoS 1 |
| Gateway | `iot_gateway.py` — subscribe MQTT + publish `iot.presence` no RabbitMQ |
| Idempotência | conjunto `_seen` por `(deviceId, seq)` |

## Como Executar

```bash
cd labs/lab05-mqtt-gateway
docker compose up -d
pip install -r requirements.txt

# Terminal 1 — gateway
python iot_gateway.py

# Terminal 2 — sensor (inclui pausa de 30s simulando Wi-Fi)
python iot_sensor_simulator.py
```

## Evidência e Resultados

```
[sensor] seq=1 PUBACK ok
...
[sensor] seq=5 PUBACK ok
[sensor] >>> OSCILAÇÃO Wi-Fi 30s (desconecta / sem publish) <<<
[sensor] Wi-Fi restabelecido — reconecta e drena backlog local + novas leituras
[sensor] seq=6 PUBACK ok
...
[sensor] seq=10 PUBACK ok
[sensor] total publicado: 10 (seqs únicos — sem duplicidade de seq)

[gateway] MQTT→AMQP device=esp32-lab-a1b2c3 seq=1
...
[gateway] MQTT→AMQP device=esp32-lab-a1b2c3 seq=10
[gateway] duplicata ignorada device=esp32-lab-a1b2c3 seq=7   # se QoS reentregar

--- RabbitMQ ---
Routing key iot.presence: 10 eventos únicos LabPresenceEvent
Core nunca assinou MQTT (RPC-09 respeitado)
```

**Conclusão:** a hipótese se confirma. MQTT + QoS 1 cobrem a rede instável; o gateway isola o Core e normaliza o evento (ADR-005).

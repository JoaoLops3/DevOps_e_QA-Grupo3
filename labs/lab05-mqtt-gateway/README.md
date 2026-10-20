# Mini-Lab 05: Ingestão IoT via MQTT + IoT Gateway

## Hipótese arquitetural

Com QoS 1 no Mosquitto, após oscilação de conectividade do sensor, as mensagens de presença são entregues. O gateway converte MQTT → RabbitMQ e elimina duplicidade por `(deviceId, seq)` — **ASR-09** e **ASR-10** (ADR-005).

## Execução

```bash
cd labs/lab05-mqtt-gateway
docker compose up -d
python3 -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
# processo 1
python iot_gateway.py
# processo 2
OSCILLATION_SECONDS=5 python iot_sensor_simulator.py
```

`OSCILLATION_SECONDS=30` reproduz o cenário completo do enunciado.  
Evidência: `docs/evidencias/labs/lab05/`

## Critério de aceite

- Log `[gateway] MQTT→AMQP` para seq 1..10
- Core sem assinatura MQTT (RPC-09)
- Evidência em `docs/evidencias/labs/lab05/`

## Evidências

Pasta: [`docs/evidencias/labs/lab05/`](../../docs/evidencias/labs/lab05/) · Galeria B1: [`docs/evidencias/labs/README.md`](../../docs/evidencias/labs/README.md)

Fluxo MQTT → IoT Gateway → AMQP:

![MQTT Gateway — fluxo](../../docs/evidencias/labs/lab05/mqtt_gateway_fluxo.png)

Logs: `gateway.log`, `sensor_run.log`, `fluxo_resumo.log`.

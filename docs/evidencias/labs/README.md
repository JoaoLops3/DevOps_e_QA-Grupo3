# Evidências dos mini-labs — Architecture Lab B1

**Hub da galeria (explicação + todas as fotos por lab):** [`README.md`](../../../README.md) — seções 3 a 9.

Esta pasta guarda os artefatos versionados. Cada `lab0N/` espelha o mini-lab em `labs/lab0N-…/`.

| Lab | Pasta | Hipótese | Artefatos principais |
| :--- | :--- | :--- | :--- |
| 01 Modulith | [lab01/](./lab01/) | Fronteiras do monólito modular | `modulith_verify_ok.png`, logs Surefire |
| 02 Domínio | [lab02/](./lab02/) | Domínio hexagonal sem Spring/JPA | `domain_tests_ok.png`, `domain_tests.log` |
| 03 Circuit Breaker | [lab03/](./lab03/) | Degradação graciosa da IA | `circuit_close_fallback.png`, `circuit_open_fallback.png` |
| 04 RabbitMQ | [lab04/](./lab04/) | Eventos assíncronos com fila/DLQ | `rabbitmq_fila_ready.png`, `queue_depth.txt` (ready=150) |
| 05 MQTT | [lab05/](./lab05/) | Ingestão IoT via gateway | `mqtt_gateway_fluxo.png`, logs |
| 06 Observabilidade | [lab06/](./lab06/) | Jenkins + Prometheus/Grafana | prints Jenkins, `prometheus_*.json`, `grafana_acesso.txt` |
| C4 | [c4/](./c4/) | Export dos diagramas | `c4-*.png` |

Reprodução: `README.md` de cada pasta em [`labs/`](../../../labs/).

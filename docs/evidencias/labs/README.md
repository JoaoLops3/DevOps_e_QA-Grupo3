# Evidências dos mini-labs — Architecture Lab B1

Artefatos de execução dos desafios B1 (logs, relatórios Surefire e capturas dos ambientes de validação).

| Lab | Pasta | Hipótese | Artefatos |
| :--- | :--- | :--- | :--- |
| 01 Modulith | [lab01/](./lab01/) | Fronteiras do monólito modular | logs Surefire, `modulith_verify_ok.png` |
| 02 Domínio | [lab02/](./lab02/) | Domínio hexagonal sem Spring/JPA | logs Surefire, `domain_tests_ok.png` |
| 03 Circuit Breaker | [lab03/](./lab03/) | Degradação graciosa da IA | `circuit_breaker_run.log`, capturas CLOSED/OPEN |
| 04 RabbitMQ | [lab04/](./lab04/) | Eventos assíncronos com fila/DLQ | `publish_run.log`, capturas da Management UI |
| 05 MQTT | [lab05/](./lab05/) | Ingestão IoT via gateway | `gateway.log`, `sensor_run.log`, captura do fluxo |
| 06 Observabilidade | [lab06/](./lab06/) | Pipelines Jenkins + Prometheus/Grafana | jobs SUCCESS, Quality Gate, targets UP, gráficos |
| C4 | [c4/](./c4/) | Diagramas arquiteturais exportados | `c4-*.png` |

O `docker compose` da raiz sobe apenas o MVP (Core + PostgreSQL + pgAdmin). Os componentes do C4 (IA, brokers, observabilidade) são validados nos mini-labs em `labs/`.

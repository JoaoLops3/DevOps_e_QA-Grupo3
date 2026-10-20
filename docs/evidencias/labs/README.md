# Evidências dos mini-labs — Architecture Lab B1

**Índice canônico das capturas B1.** Os READMEs da raiz e de cada lab apontam para cá; evite duplicar a galeria completa em outros arquivos.

Artefatos de execução dos desafios (logs, Surefire e capturas dos ambientes de validação).

| Lab | Pasta | Hipótese | Artefatos |
| :--- | :--- | :--- | :--- |
| 01 Modulith | [lab01/](./lab01/) | Fronteiras do monólito modular | logs Surefire, `modulith_verify_ok.png` |
| 02 Domínio | [lab02/](./lab02/) | Domínio hexagonal sem Spring/JPA | logs Surefire, `domain_tests_ok.png` |
| 03 Circuit Breaker | [lab03/](./lab03/) | Degradação graciosa da IA | `circuit_breaker_run.log`, capturas CLOSED/OPEN |
| 04 RabbitMQ | [lab04/](./lab04/) | Eventos assíncronos com fila/DLQ | `publish_run.log`, `queue_depth.txt` (ready=150), Management UI |
| 05 MQTT | [lab05/](./lab05/) | Ingestão IoT via gateway | `gateway.log`, `sensor_run.log`, captura do fluxo |
| 06 Observabilidade | [lab06/](./lab06/) | Pipelines Jenkins + Prometheus/Grafana | jobs SUCCESS, Quality Gate, `prometheus_*.json`, carga |
| C4 | [c4/](./c4/) | Diagramas arquiteturais exportados | `c4-*.png` |

O `docker compose` da raiz sobe apenas o MVP (Core + PostgreSQL + pgAdmin). Os componentes do C4 (IA, brokers, observabilidade) são validados nos mini-labs em `labs/`.

---

## Lab 01 — Modulith

![Modulith — fronteiras OK](./lab01/modulith_verify_ok.png)

## Lab 02 — Domínio hexagonal

![Domínio — testes OK](./lab02/domain_tests_ok.png)

## Lab 03 — Circuit Breaker

![Circuit Breaker CLOSED](./lab03/circuit_close_fallback.png)

![Circuit Breaker OPEN](./lab03/circuit_open_fallback.png)

## Lab 04 — RabbitMQ

Publisher padrão: 50 eventos/execução. Evidência de profundidade: `queue_depth.txt` com **ready=150** (reexecuções com consumer OFF). Captura Management UI:

![RabbitMQ — profundidade da fila](./lab04/rabbitmq_fila_200.png)

## Lab 05 — MQTT Gateway

![MQTT Gateway — fluxo](./lab05/mqtt_gateway_fluxo.png)

## Lab 06 — Jenkins + Prometheus / Grafana

### Jenkins (ASR-11)

![Jenkins — dashboard dos três jobs](./lab06/jenkins_jobs_dashboard.png)

![Jenkins — pipeline-core SUCCESS](./lab06/jenkins_pipeline_core_ok.png)

![Jenkins — estágios pipeline-core](./lab06/jenkins_pipeline_core_stages.png)

![Jenkins — console pipeline-core](./lab06/jenkins_pipeline_core_console.png)

![Terminal Lab 06 — Jenkins](./lab06/Terminal_lab6_Jenkins.png)

### Prometheus / Grafana (ASR-12)

Sem capturas PNG de painel nesta pasta — comprovação via artefatos versionados:

| Arquivo | Conteúdo |
| :--- | :--- |
| `prometheus_up.json` | Target UP (`"1"`) |
| `prometheus_p95.json` | Latência p95 |
| `load_test.log` | Simulação de carga |
| `grafana_acesso.txt` | URL e datasource |
| `demo_health.json` | Health do `demo-metrics` |

## C4

![C4 Container](./c4/c4-container-overview.png)

![C4 Component](./c4/c4-component-core.png)

![C4 Sequence D4](./c4/c4-sequence-desafio4.png)

![C4 Deployment D6](./c4/c4-deployment.png)

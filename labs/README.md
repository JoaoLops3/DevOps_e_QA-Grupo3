# Mini-labs — Architecture Lab B1

Mini-labs isolados por desafio. Cada pasta tem hipótese, comando de execução, critério de aceite e link para evidências.

**Galeria completa (explicação + fotos no README raiz):** [`../README.md`](../README.md) — seções 3 a 9.

| Lab | Desafio | ASR | Reprodução |
| :--- | :--- | :--- | :--- |
| [lab01-spring-modulith](./lab01-spring-modulith/) | D1 — Monólito modular | ASR-01/02 | `../../mvnw -f pom.xml test -Dtest=ModularityTests` |
| [lab02-domain-isolation](./lab02-domain-isolation/) | D2 — Domínio hexagonal | ASR-03/04 | `../../mvnw -f pom.xml test` |
| [lab03-python-circuitbreaker](./lab03-python-circuitbreaker/) | D3 — IA + Circuit Breaker | ASR-05/06 | `docker compose` + demo Python |
| [lab04-rabbitmq-resilience](./lab04-rabbitmq-resilience/) | D4 — Sync REST × async RabbitMQ | ASR-07/08 | `docker compose` + `sync/comparar_sync_async.py` |
| [lab05-mqtt-gateway](./lab05-mqtt-gateway/) | D5 — IoT / MQTT | ASR-09/10 | `docker compose` + gateway/sensor |
| [lab06-jenkins-prometheus](./lab06-jenkins-prometheus/) | D6 — CI/CD e observabilidade | ASR-11/12 | `docker compose` + carga |

**Evidências (arquivos):** [`docs/evidencias/labs/`](../docs/evidencias/labs/)  
**Registro arquitetural:** [`docs/arquitetura/README.md`](../docs/arquitetura/README.md)

MVP na raiz (Core + PostgreSQL + pgAdmin): `docker compose up --build` — ver [`docker/README.md`](../docker/README.md).  
Os componentes do C4 alvo (IA, brokers, Jenkins, Prometheus/Grafana) **não** sobem no compose da raiz; só nos labs acima.

# Mini-labs — Architecture Lab B1

Mini-labs isolados por desafio.

| Lab | Desafio | Reprodução |
| :--- | :--- | :--- |
| [lab01-spring-modulith](./lab01-spring-modulith/) | D1 — Monólito modular | `../../mvnw -f pom.xml test` |
| [lab02-domain-isolation](./lab02-domain-isolation/) | D2 — Domínio hexagonal | `../../mvnw -f pom.xml test` |
| [lab03-python-circuitbreaker](./lab03-python-circuitbreaker/) | D3 — IA + Circuit Breaker | `docker compose` + demo Python |
| [lab04-rabbitmq-resilience](./lab04-rabbitmq-resilience/) | D4 — Mensageria assíncrona | `docker compose` + publisher |
| [lab05-mqtt-gateway](./lab05-mqtt-gateway/) | D5 — IoT / MQTT | `docker compose` + gateway/sensor |
| [lab06-jenkins-prometheus](./lab06-jenkins-prometheus/) | D6 — CI/CD e observabilidade | `docker compose` + carga |

Artefatos: [`docs/evidencias/labs/`](../docs/evidencias/labs/).

MVP na raiz: `docker compose up --build` (Core + PostgreSQL + pgAdmin).  
Registro arquitetural: [`docs/Arquitetura/README.md`](../docs/Arquitetura/README.md).

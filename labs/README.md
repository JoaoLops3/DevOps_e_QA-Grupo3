# Mini-labs — Architecture Lab B1

Evidências isoladas por desafio. Cada pasta tem o próprio `README.md` (e, quando necessário, `docker-compose.yml` local).

O MVP rodável continua na **raiz** do repositório (`docker compose up --build` → Core + PostgreSQL + pgAdmin).  
Registro arquitetural: [`docs/Arquitetura/README.md`](../docs/Arquitetura/README.md).

| Lab | Desafio | Hipótese |
| :--- | :--- | :--- |
| [lab01-spring-modulith](./lab01-spring-modulith/) | D1 | Fronteiras do monólito modular (Spring Modulith) |
| [lab02-domain-isolation](./lab02-domain-isolation/) | D2 | Domínio hexagonal testável sem Spring/JPA |
| [lab03-python-circuitbreaker](./lab03-python-circuitbreaker/) | D3 | FastAPI + Circuit Breaker / fallback no Core |
| [lab04-rabbitmq-resilience](./lab04-rabbitmq-resilience/) | D4 | Eventos assíncronos + DLQ (consumer down) |
| [lab05-mqtt-gateway](./lab05-mqtt-gateway/) | D5 | MQTT → IoT Gateway → RabbitMQ |
| [lab06-jenkins-prometheus](./lab06-jenkins-prometheus/) | D6 | Pipelines Jenkins + métricas Prometheus/Grafana |

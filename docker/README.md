# Docker — Educação Continuada Gamificada (3QA)

Stack da raiz (MVP): Spring Boot (API + Vue estático) + PostgreSQL + pgAdmin.  
Sem H2 e sem Thymeleaf.

Este compose não inclui FastAPI, Mosquitto, RabbitMQ nem Prometheus/Grafana. Os componentes do Architecture Lab B1 ficam em `labs/lab03` … `labs/lab06` — ver [`docs/Arquitetura/README.md`](../docs/Arquitetura/README.md#11-contrato-de-runtime--raiz-do-repositório-vs-mini-labs).

## Credenciais

| Serviço | Acesso | Credenciais |
|---------|--------|-------------|
| API / Vue | http://localhost:8080/ | — |
| Swagger | http://localhost:8080/swagger-ui.html | — |
| Postgres | `localhost:5432` / db `progresso_db` | user `progresso` / senha `progresso` |
| pgAdmin | http://localhost:5050/ | `admin@progresso.com` / `admin` |

No pgAdmin, o servidor usa host **`postgres`** (nome do serviço na rede Docker), porta `5432`, database `progresso_db`, user/senha `progresso`.

## Subir a stack

Na raiz do projeto:

```bash
docker compose up --build
```

Em segundo plano:

```bash
docker compose up --build -d
```

## Apenas o banco (aplicação via Maven)

```bash
docker compose up -d postgres
./mvnw spring-boot:run
```

## Parar

```bash
docker compose down
```

## Validação rápida

1. http://localhost:8080/ — criação de aluno e registro de conclusões.
2. Swagger — mesmos dados via API.
3. pgAdmin — tabelas `alunos` e `conclusoes_curso`.

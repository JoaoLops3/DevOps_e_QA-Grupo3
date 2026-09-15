# Docker — Educação Continuada Gamificada (3QA)

Stack: **Spring Boot (API + Vue estático)** + **PostgreSQL** + **pgAdmin**.  
Sem H2 e sem Thymeleaf.

## Credenciais

| Serviço | Acesso | Credenciais |
|---------|--------|-------------|
| API / Vue | http://localhost:8080/ | — |
| Swagger | http://localhost:8080/swagger-ui.html | — |
| Postgres | `localhost:5432` / db `progresso_db` | user `progresso` / senha `progresso` |
| pgAdmin | http://localhost:5050/ | `admin@progresso.com` / `admin` |

No pgAdmin, ao registrar o servidor use host **`postgres`** (nome do serviço na rede Docker), porta `5432`, database `progresso_db`, user/senha `progresso`.

## Subir tudo

Na raiz do projeto:

```bash
docker compose up --build
```

Ou em segundo plano:

```bash
docker compose up --build -d
```

## Só o banco (app local com Maven)

```bash
docker compose up -d postgres
./mvnw spring-boot:run
```

## Parar

```bash
docker compose down
```

## Fluxo de teste rápido

1. Acessar http://localhost:8080/ — criar aluno e registrar conclusões.
2. Confirmar no Swagger os mesmos dados.
3. No pgAdmin, consultar as tabelas `alunos` e `conclusoes_curso`.

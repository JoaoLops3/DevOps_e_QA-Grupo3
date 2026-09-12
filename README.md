# Educação Continuada Gamificada — História 3QA

Spring Boot + TDD/ATDD (Red → Green → Blue) da US de progresso da assinatura básica rumo ao Premium.

## Estudo de caso

Educação Continuada Gamificada (case passado em aula).

## User Story (escolhida)

| Campo | Conteúdo |
|--------|----------|
| Código | 3QA |
| Integrante que redigiu | _(preencher nome)_ |
| US | COMO usuário da assinatura básica, QUERO visualizar quantos cursos já concluí e quantos faltam para os 12 necessários, PARA ter clareza sobre minha evolução e o que falta para virar Premium |

## BDD por integrante

| Integrante | Cenário BDD |
|------------|-------------|
| _(nome)_ | Exibir concluídos e faltantes para os 12 (BLUE atual) |
| _(nome)_ | Promover para Premium + 3 moedas ao atingir 12 (RED comentado) |
| _(nome)_ | Não contabilizar curso com média &lt; 7,0 (RED comentado) |

## Evidência — User Stories e BDD

Print da planilha/documento com as **User Stories** e os **BDDs** do grupo.

Arquivo: `docs/evidencias/us-bdd/USER STORIES_BDD.png`

![User Stories e BDD](docs/evidencias/us-bdd/USER%20STORIES_BDD.png)

## Estrutura do projeto (alinhada ao enunciado)

```
src/main/java/org/example/bdd_teste/
├── BddTesteApplication.java          # Spring Boot
├── domain/                           # Domain (obrigatório)
├── entity/                           # JPA Entity (próximo)
├── dto/                              # DTOs (próximo)
├── repository/                       # Spring Data JPA (próximo)
├── service/                          # Service (próximo)
└── controller/                       # REST + Swagger (próximo)

src/test/java/org/example/bdd_teste/
└── domaintest/                       # DomainTest (obrigatório)
    └── CalculadoraProgressoTest.java

docs/evidencias/
├── us-bdd/                           # print User Stories + BDD
├── red/                              # prints testes falhando
├── green/                            # prints passando + JaCoCo
└── blue/                             # 100% cobertura, sem amarelo/vermelho

frontend/                             # Vue.js (próximo)
docker/                               # Dockerfile + compose + Postgres (próximo)
```

## Como rodar (local)

```bash
./mvnw test
./mvnw verify          # gera JaCoCo em target/site/jacoco/
./mvnw spring-boot:run
```

## Dependências

- Spring Web, Spring Data JPA
- H2 (dev/test) e PostgreSQL (produção/container)
- pgAdmin via Docker (próximo)
- Swagger / OpenAPI (próximo)
- Vue.js frontend (próximo)



## Checklist da entrega

- [x] Pacote `domain` + classes de domínio
- [x] Pacote `domaintest` + `CalculadoraProgressoTest`
- [x] BLUE do 1º critério (progresso 3/9) — RED dos demais comentado no teste
- [x] Evidências RED / GREEN / BLUE em `docs/evidencias/`
- [ ] Camadas Service, Repository, Entity, DTO, Controller
- [ ] Endpoint + Swagger
- [ ] Frontend Vue.js
- [ ] H2 e Postgres rodando (evidências)
- [ ] Docker + docker-compose + Postgres + pgAdmin
- [ ] README final com US/BDD identificados por integrante

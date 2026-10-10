# Educação Continuada Gamificada — Architecture Lab B1 + AC1 (História 3QA)

**Equipe:** João Gabriel, Hector Silveira, Miguel Muran  
**Repositório:** https://github.com/JoaoLops3/DevOps_e_QA-Grupo3.git  
**Disciplina:** Modelagem e Construção de Software — Architecture Lab B1 (6,0 pontos)

Este README é o ponto de entrada do repositório. A AC1 entrega o MVP (progresso da assinatura básica → Premium). O **B1** registra as seis decisões arquiteturais evolutivas sobre esse mesmo produto, com ADR, C4 e evidência por desafio.

---

## 1. O que foi feito no B1

O B1 assume o papel de Tech Lead: cada desafio segue o padrão obrigatório do enunciado

**RF → RNF → ASR → RPC → Alternativas → ADR → C4 → Evidência**

Não há seis arquiteturas distintas nem acumulação de tecnologia sem problema do case. Há um **registro arquitetural vivo** e seis mini-labs que testam hipóteses (fronteiras do Core, domínio hexagonal, IA com degradação graciosa, eventos assíncronos, IoT/MQTT, CI/CD e observabilidade).

| Entrega pedida no enunciado | Onde está neste repositório |
| :--- | :--- |
| Registro arquitetural cumulativo | [`docs/Arquitetura/README.md`](docs/Arquitetura/README.md) |
| Matriz cumulativa de ASRs (ASR-01 … ASR-12) | Seção 2 do mesmo README |
| ADRs (ADR-001 … ADR-006) | [`docs/Arquitetura/adr/`](docs/Arquitetura/adr/) |
| C4 (Container obrigatório + Component + Sequence + Deployment) | [`docs/Arquitetura/diagramas/`](docs/Arquitetura/diagramas/) |
| Mini-labs / microsoluções | [`labs/`](labs/) — índice em [`labs/README.md`](labs/README.md) |
| Evidências versionadas | [`docs/evidencias/labs/`](docs/evidencias/labs/) — índice em [`docs/evidencias/labs/README.md`](docs/evidencias/labs/README.md) |
| Decisões do Desafio 1 (Tech Lead / AC1→B1) | [`docs/Arquitetura/EXB1_DESAFIO1_AC1/README.md`](docs/Arquitetura/EXB1_DESAFIO1_AC1/README.md) |
| Síntese para a AF | Seção 5 de [`docs/Arquitetura/README.md`](docs/Arquitetura/README.md) |
| Identificação da equipe | Topo deste README e do README de Arquitetura |

> No enunciado o caminho aparece como `/docs/arquitetura/`. Neste repositório a pasta é **`docs/Arquitetura/`** (mesmo conteúdo; links abaixo usam o caminho real).

### Contrato de runtime (importante para avaliação)

| Escopo | Conteúdo | Comando |
| :--- | :--- | :--- |
| **Raiz (MVP AC1)** | `src/`, `frontend/`, `docker-compose.yml` → Core Spring Boot + PostgreSQL + pgAdmin | `docker compose up --build` |
| **Mini-labs B1** | `labs/lab01` … `labs/lab06` isolados | ver o `README.md` de cada lab |
| **Registro B1** | `docs/Arquitetura/` + evidências em `docs/evidencias/labs/` | leitura / navegação |

O `docker compose` da **raiz** não sobe AI Service, Mosquitto, RabbitMQ, Jenkins, Prometheus nem Grafana. Esses componentes constam no **C4 alvo do B1** e são validados nos mini-labs. Detalhe: [contrato de runtime](docs/Arquitetura/README.md#11-contrato-de-runtime--raiz-do-repositório-vs-mini-labs).

**Stack da raiz:** API REST + Vue 3 (CDN) estático + PostgreSQL (sem H2, sem Thymeleaf).

---

## 2. Mapa dos seis desafios (enunciado → artefatos)

Cada linha liga o desafio do PDF aos arquivos deste repositório.

| # | Desafio (enunciado) | Decisão | ADR | C4 | Mini-lab | Evidência |
| :---: | :--- | :--- | :--- | :--- | :--- | :--- |
| **1** | Drivers e evolução do Core | Monólito modular (não microsserviços prematuros) | [ADR-001](docs/Arquitetura/adr/ADR-001-monolito-modular.md) | [Container](docs/Arquitetura/diagramas/c4-container-overview.puml) · [PNG](docs/Arquitetura/diagramas/c4-container-overview.png) | [lab01-spring-modulith](labs/lab01-spring-modulith/) | [docs/evidencias/labs/lab01/](docs/evidencias/labs/lab01/) |
| **2** | Organização interna do Core | Hexagonal (Ports & Adapters) no domínio | [ADR-002](docs/Arquitetura/adr/ADR-002-arquitetura-hexagonal.md) | [Component](docs/Arquitetura/diagramas/c4-component-core.puml) · [PNG](docs/Arquitetura/diagramas/c4-component-core.png) | [lab02-domain-isolation](labs/lab02-domain-isolation/) | [docs/evidencias/labs/lab02/](docs/evidencias/labs/lab02/) |
| **3** | Serviço Python e IA | FastAPI independente + Circuit Breaker / fallback | [ADR-003](docs/Arquitetura/adr/ADR-003-servico-python-ia.md) | [Container](docs/Arquitetura/diagramas/c4-container-overview.puml) | [lab03-python-circuitbreaker](labs/lab03-python-circuitbreaker/) | [docs/evidencias/labs/lab03/](docs/evidencias/labs/lab03/) |
| **4** | Comunicação e eventos | RabbitMQ assíncrono + DLQ | [ADR-004](docs/Arquitetura/adr/ADR-004-mensageria-assincrona.md) | [Sequence](docs/Arquitetura/diagramas/c4-sequence-desafio4.puml) · [PNG](docs/Arquitetura/diagramas/c4-sequence-desafio4.png) | [lab04-rabbitmq-resilience](labs/lab04-rabbitmq-resilience/) | [docs/evidencias/labs/lab04/](docs/evidencias/labs/lab04/) |
| **5** | IoT e MQTT | Mosquitto + IoT Gateway → AMQP | [ADR-005](docs/Arquitetura/adr/ADR-005-integracao-iot-mqtt.md) | [Container](docs/Arquitetura/diagramas/c4-container-overview.puml) | [lab05-mqtt-gateway](labs/lab05-mqtt-gateway/) | [docs/evidencias/labs/lab05/](docs/evidencias/labs/lab05/) |
| **6** | CI/CD e observabilidade | Pipelines Jenkins autônomas + Prometheus/Grafana | [ADR-006](docs/Arquitetura/adr/ADR-006-cicd-observabilidade.md) | [Deployment](docs/Arquitetura/diagramas/c4-deployment.puml) · [PNG](docs/Arquitetura/diagramas/c4-deployment.png) | [lab06-jenkins-prometheus](labs/lab06-jenkins-prometheus/) | [docs/evidencias/labs/lab06/](docs/evidencias/labs/lab06/) |

Rastreabilidade detalhada (RF/RNF/ASR/RPC por desafio): seções 6 e 7 de [`docs/Arquitetura/README.md`](docs/Arquitetura/README.md).

### ADRs (lista direta)

1. [`docs/Arquitetura/adr/ADR-001-monolito-modular.md`](docs/Arquitetura/adr/ADR-001-monolito-modular.md)
2. [`docs/Arquitetura/adr/ADR-002-arquitetura-hexagonal.md`](docs/Arquitetura/adr/ADR-002-arquitetura-hexagonal.md)
3. [`docs/Arquitetura/adr/ADR-003-servico-python-ia.md`](docs/Arquitetura/adr/ADR-003-servico-python-ia.md)
4. [`docs/Arquitetura/adr/ADR-004-mensageria-assincrona.md`](docs/Arquitetura/adr/ADR-004-mensageria-assincrona.md)
5. [`docs/Arquitetura/adr/ADR-005-integracao-iot-mqtt.md`](docs/Arquitetura/adr/ADR-005-integracao-iot-mqtt.md)
6. [`docs/Arquitetura/adr/ADR-006-cicd-observabilidade.md`](docs/Arquitetura/adr/ADR-006-cicd-observabilidade.md)

### Diagramas C4

| Nível | Fonte PlantUML | PNG (fonte) | Cópia em evidências |
| :--- | :--- | :--- | :--- |
| Container | [`c4-container-overview.puml`](docs/Arquitetura/diagramas/c4-container-overview.puml) | [`c4-container-overview.png`](docs/Arquitetura/diagramas/c4-container-overview.png) | [`docs/evidencias/labs/c4/`](docs/evidencias/labs/c4/) |
| Component (Core) | [`c4-component-core.puml`](docs/Arquitetura/diagramas/c4-component-core.puml) | [`c4-component-core.png`](docs/Arquitetura/diagramas/c4-component-core.png) | idem |
| Sequence (D4) | [`c4-sequence-desafio4.puml`](docs/Arquitetura/diagramas/c4-sequence-desafio4.puml) | [`c4-sequence-desafio4.png`](docs/Arquitetura/diagramas/c4-sequence-desafio4.png) | idem |
| Deployment (D6) | [`c4-deployment.puml`](docs/Arquitetura/diagramas/c4-deployment.puml) | [`c4-deployment.png`](docs/Arquitetura/diagramas/c4-deployment.png) | idem |

---

## 3. Evidências B1 — pasta e arquivos principais

Índice: [`docs/evidencias/labs/README.md`](docs/evidencias/labs/README.md)

| Lab | Pasta | Hipótese | Artefatos principais |
| :--- | :--- | :--- | :--- |
| 01 | [`docs/evidencias/labs/lab01/`](docs/evidencias/labs/lab01/) | Fronteiras Modulith | `modulith_verify_ok.png`, logs Surefire (`modularity_tests.log`, `com.educont.ModularityTests*.txt`) |
| 02 | [`docs/evidencias/labs/lab02/`](docs/evidencias/labs/lab02/) | Domínio sem Spring/JPA | `domain_tests_ok.png`, `domain_tests.log`, `resumo.txt` |
| 03 | [`docs/evidencias/labs/lab03/`](docs/evidencias/labs/lab03/) | Circuit Breaker / fallback | `circuit_close_fallback.png`, `circuit_open_fallback.png`, `circuit_breaker_run.log`, `run_ai_up.log`, `run_ai_down.log` |
| 04 | [`docs/evidencias/labs/lab04/`](docs/evidencias/labs/lab04/) | Fila com consumer OFF | `rabbitmq_fila_50.png`, `rabbitmq_fila_200.png`, `publish_run.log`, `queue_depth.txt` |
| 05 | [`docs/evidencias/labs/lab05/`](docs/evidencias/labs/lab05/) | MQTT → Gateway → AMQP | `mqtt_gateway_fluxo.png`, `gateway.log`, `sensor_run.log`, `fluxo_resumo.log` |
| 06 | [`docs/evidencias/labs/lab06/`](docs/evidencias/labs/lab06/) | Jenkins + Prometheus/Grafana | `jenkins_jobs_dashboard.png`, `jenkins_pipeline_core_*.png`, `pipeline-*_StageView.png`, `pipeline-*_console.log`, `prometheus_targets.png`, `grafico_queries_Throughput_latenciaP95.png` |
| C4 | [`docs/evidencias/labs/c4/`](docs/evidencias/labs/c4/) | Export dos diagramas | `c4-*.png` |

Reprodução de cada lab: README em `labs/lab0x-…/`. O Lab 06 embute as capturas no próprio [`labs/lab06-jenkins-prometheus/README.md`](labs/lab06-jenkins-prometheus/README.md).

---

## 4. Estrutura do repositório

```
├── README.md                          # Este arquivo (entrada AC1 + B1)
├── src/                               # Core Spring Boot (MVP AC1)
├── frontend/                          # Vue (fonte; Maven copia para static/)
├── docker-compose.yml                 # MVP: app + Postgres + pgAdmin
├── Dockerfile
├── docs/
│   ├── Arquitetura/
│   │   ├── README.md                  # Registro vivo B1 (ASRs, rastreabilidade, síntese AF)
│   │   ├── adr/                       # ADR-001 … ADR-006
│   │   ├── diagramas/                 # C4 .puml + .png
│   │   └── EXB1_DESAFIO1_AC1/         # Decisões iniciais Tech Lead
│   └── evidencias/
│       ├── labs/                      # Evidências B1 (lab01…lab06 + c4)
│       ├── red/ green/ blue/          # Evidências TDD AC1
│       ├── us-bdd/                    # User Stories / BDD
│       └── banco/                     # Capturas PostgreSQL
└── labs/
    ├── README.md
    ├── lab01-spring-modulith/         # D1
    ├── lab02-domain-isolation/        # D2
    ├── lab03-python-circuitbreaker/   # D3
    ├── lab04-rabbitmq-resilience/     # D4
    ├── lab05-mqtt-gateway/            # D5
    └── lab06-jenkins-prometheus/      # D6 (Jenkins + Prometheus + Grafana)
```

---

## 5. Checklist B1 (entrega final do enunciado)

- [x] Link do repositório e identificação da equipe (João Gabriel, Hector Silveira, Miguel Muran)
- [x] README arquitetural com as seis decisões e matriz cumulativa de ASRs — [`docs/Arquitetura/README.md`](docs/Arquitetura/README.md)
- [x] ADRs essenciais — [`docs/Arquitetura/adr/`](docs/Arquitetura/adr/)
- [x] C4 final coerente (Container, Component, Sequence D4, Deployment D6) — [`docs/Arquitetura/diagramas/`](docs/Arquitetura/diagramas/)
- [x] Links/pastas das microsoluções — [`labs/`](labs/)
- [x] Evidências versionadas — [`docs/evidencias/labs/`](docs/evidencias/labs/)
- [x] Síntese para a PoC da AF — seção 5 do README de Arquitetura

---

## 6. AC1 — User Story, BDD e MVP

### Estudo de caso

Educação Continuada Gamificada.

### User Story escolhida pelo grupo

| Campo | Conteúdo |
|--------|----------|
| Código | **3QA** |
| Integrante que redigiu | Miguel Muran |
| US | COMO usuário da assinatura básica, QUERO visualizar quantos cursos já concluí e quantos faltam para os 12 necessários, PARA ter clareza sobre minha evolução e o que falta para virar Premium |

### BDD por integrante

| Integrante | Cenário BDD |
|------------|-------------|
| João Gabriel | Exibir concluídos e faltantes para os 12 (BLUE) |
| Hector Silveira | Promover para Premium + 3 moedas ao atingir 12 (BLUE) |
| Miguel Muran | Não contabilizar curso com média &lt; 7,0 (BLUE) |

### Regras de negócio

1. Todo aluno nasce com assinatura **BASICA** e **0 moedas**.
2. Ao registrar um curso, só entra no progresso se a **média ≥ 7,0**.
3. O progresso mostra: **concluídos**, **faltantes** (meta = 12), **assinatura** e **moedas**.
4. Ao atingir **12 cursos válidos**, o aluno vira **PREMIUM** e recebe **3 moedas**.

Camada de domínio (TDD, JaCoCo 100%): `src/main/java/.../domain/`  
Persistência e API: `entity` → `repository` → `service` → `controller`  
Frontend: editar em `frontend/`; Maven copia para `static/` (servido em `/`)

### Como rodar o MVP (raiz)

| Cenário | Necessário |
|---------|------------|
| **Opção A** (stack completa) | Docker Desktop |
| **Opção B** (app via Maven) | Docker Desktop + Java 17+ |
| **Testes / JaCoCo** | Java 17+ (Maven Wrapper: `./mvnw`) |

**Opção A — stack completa**

```bash
docker compose up --build
```

| Serviço | URL / acesso |
|---------|----------------|
| **Vue + API** | http://localhost:8080/ |
| **Swagger** | http://localhost:8080/swagger-ui.html |
| **Postgres** | `localhost:5432` · db `progresso_db` · user/senha `progresso` |
| **pgAdmin** | http://localhost:5050/ · `admin@progresso.com` / `admin` |

**Opção B — Postgres no Docker + app no Maven**

```bash
docker compose up -d postgres
./mvnw spring-boot:run
```

**Testes e cobertura (domain 100%)**

```bash
./mvnw test
./mvnw verify
```

Relatório JaCoCo: `target/site/jacoco/index.html`  
Parar containers: `docker compose down`  
Detalhes: [`docker/README.md`](docker/README.md)

### Interface Vue

1. Acessar http://localhost:8080/
2. Criar um aluno (nome).
3. Registrar conclusões de curso (título + média).
4. Acompanhar o card de progresso: concluídos / faltantes / assinatura / moedas.
5. Com **12 médias ≥ 7** no mesmo aluno → **PREMIUM** + **3 moedas** + **12 / 0**.

### API (Swagger ou curl)

1. `POST /api/alunos` — `{ "nome": "Joao" }`
2. `POST /api/alunos/{id}/conclusoes` — `{ "tituloCurso": "TDD", "media": 8.5 }` → conta no progresso
3. Mesmo endpoint com `"media": 6.0` → **não** aumenta `cursosConcluidos`
4. `GET /api/alunos/{id}/progresso` — concluídos, faltantes, assinatura, moedas
5. Com 12 médias ≥ 7 → `PREMIUM` + 3 moedas

| Método | Rota | Função |
|--------|------|--------|
| POST | `/api/alunos` | Cria aluno (BASICA) |
| GET | `/api/alunos` | Lista alunos |
| GET | `/api/alunos/{id}` | Busca por id |
| POST | `/api/alunos/{id}/conclusoes` | Registra curso e recalcula promoção |
| GET | `/api/alunos/{id}/progresso` | Consulta progresso |

### pgAdmin

1. Subir a stack (`docker compose up --build`).
2. Acessar http://localhost:5050/ com `admin@progresso.com` / `admin`.
3. **Adicionar Novo Servidor** — Host: `postgres` (ou `localhost`), Port: `5432`, Database: `progresso_db`, Username/Password: `progresso`.
4. Tabelas: **`alunos`** e **`conclusoes_curso`**.

### Evidências AC1

| Tipo | Pasta / arquivos |
| :--- | :--- |
| User Stories e BDD | [`docs/evidencias/us-bdd/`](docs/evidencias/us-bdd/) — `USER STORIES_BDD.png`, `USER STORIES_BDD2.png` |
| Banco | [`docs/evidencias/banco/`](docs/evidencias/banco/) — `Tabela_banco_conclusoes_curso.png` |
| TDD RED | [`docs/evidencias/red/`](docs/evidencias/red/) |
| TDD GREEN | [`docs/evidencias/green/`](docs/evidencias/green/) |
| TDD BLUE | [`docs/evidencias/blue/`](docs/evidencias/blue/) |

![User Stories e BDD](docs/evidencias/us-bdd/USER%20STORIES_BDD.png)

![USER STORIES_BDD2](docs/evidencias/us-bdd/USER%20STORIES_BDD2.png)

![Tabela conclusoes_curso](docs/evidencias/banco/Tabela_banco_conclusoes_curso.png)

### Checklist AC1

- [x] Pacote `domain` + classes de domínio
- [x] Pacote `domaintest` + 3 BDDs ativos (João Gabriel / Hector Silveira / Miguel Muran)
- [x] JaCoCo 100% no `domain`
- [x] Evidências RED / GREEN / BLUE / US-BDD
- [x] Entity, DTO, Repository, Service, Controller
- [x] Swagger / OpenAPI
- [x] Frontend Vue.js (CDN) em `/`
- [x] PostgreSQL (sem H2)
- [x] Docker + compose (app + Postgres + pgAdmin)
- [x] Capturas de banco em `docs/evidencias/banco/`

---

## 7. Leitura sugerida para avaliação do B1

1. Este README (seções 1–3 e checklist da seção 5).
2. [`docs/Arquitetura/README.md`](docs/Arquitetura/README.md) — ASRs, ADRs, C4 e rastreabilidade.
3. ADRs em [`docs/Arquitetura/adr/`](docs/Arquitetura/adr/).
4. PNGs C4 em [`docs/Arquitetura/diagramas/`](docs/Arquitetura/diagramas/) ou [`docs/evidencias/labs/c4/`](docs/evidencias/labs/c4/).
5. Evidências por desafio em [`docs/evidencias/labs/lab01`](docs/evidencias/labs/lab01/) … [`lab06`](docs/evidencias/labs/lab06/).
6. Reprodução opcional: `README.md` de cada pasta em [`labs/`](labs/).

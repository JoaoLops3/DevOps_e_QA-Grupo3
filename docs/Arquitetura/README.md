# Documentação Arquitetural Vivo — Plataforma de Gamificação para Educação Continuada (B1)

## Identificação do Projeto

- **Equipe:** João Gabriel, Hector Silveira, Miguel Muran
- **Repositório Git:** https://github.com/JoaoLops3/DevOps_e_QA-Grupo3.git
- **Disciplina:** Architecture Lab B1

## 1. Visão Geral

A plataforma de Educação Continuada Gamificada nasce como um MVP de progresso de assinatura (cursos concluídos, moedas e promoção para Premium) e, no Architecture Lab B1, passa a sustentar escala, inteligência e operação contínua. O registo abaixo acumula as decisões dos seis desafios: o que permanece no Core, o que sai para um serviço próprio e o que fica na borda de integração.

- **Contexto da AC1:** o MVP é um monólito em Java/Spring Boot com banco relacional. Atende cerca de 1.000 usuários nos fluxos de ranking, cursos, moedas, badges e recompensas. Uma base de código, um processo e um schema bastam para a equipa de seis pessoas entregar e operar o produto.
- **Evolução para o B1:** a meta sobe para 100.000 usuários e para múltiplas instituições. Entram um motor de recomendação e um assistente educacional com RAG/LLM em Python, mensageria assíncrona para efeitos secundários da conclusão de curso, ingestão de eventos IoT de laboratórios via MQTT, pipeline de CI/CD no Jenkins e observabilidade com Prometheus e Grafana. A escala e o isolamento de falha passam a ditar a fronteira de cada componente; a decomposição em microsserviços granulares continua fora de escopo.

## 1.1 Contrato de runtime — raiz do repositório vs. mini-labs

O C4 Container abaixo descreve a **arquitetura-alvo do B1**. O runtime que sobe na raiz do Git é deliberadamente o **MVP da AC1**, para a banca validar o Core sem acoplar FastAPI, Mosquitto, RabbitMQ ou Prometheus ao `docker compose` principal.

| Escopo | O que sobe / onde está | Objetivo |
| :--- | :--- | :--- |
| **Raiz do repositório** | `src/`, `frontend/`, `docker-compose.yml`, `Dockerfile` | MVP rodável: Core Spring Boot + PostgreSQL + pgAdmin |
| **Mini-labs B1** | `labs/lab01` … `labs/lab06` (cada um isolado) | Evidência de hipótese por desafio (Modulith, hexagonal, IA+CB, RabbitMQ, MQTT, Jenkins/Prometheus) |
| **Registro arquitetural** | Este README + `adr/` + `diagramas/` | Decisões, ASRs, C4 e rastreabilidade |

```bash
# Na raiz — sobe só o MVP (Core + Postgres + pgAdmin)
docker compose up --build
```

| Serviço (raiz) | URL / acesso |
| :--- | :--- |
| Core (API + Vue) | http://localhost:8080/ |
| PostgreSQL | `localhost:5432` · db `progresso_db` · user/senha `progresso` |
| pgAdmin | http://localhost:5050/ · `admin@progresso.com` / `admin` |

Componentes do C4 que **não** entram no compose da raiz — e onde são validados:

| Componente (C4) | Validação |
| :--- | :--- |
| AI Service (FastAPI) + Circuit Breaker | [labs/lab03-python-circuitbreaker](../../labs/lab03-python-circuitbreaker/) |
| RabbitMQ + DLQ | [labs/lab04-rabbitmq-resilience](../../labs/lab04-rabbitmq-resilience/) |
| Mosquitto + IoT Gateway | [labs/lab05-mqtt-gateway](../../labs/lab05-mqtt-gateway/) |
| Jenkins / Prometheus / Grafana | [labs/lab06-jenkins-prometheus](../../labs/lab06-jenkins-prometheus/) |
| Fronteiras Modulith / domínio hexagonal | [labs/lab01](../../labs/lab01-spring-modulith/), [labs/lab02](../../labs/lab02-domain-isolation/) |

## 2. Matriz Cumulativa de ASRs (Requisitos Arquiteturalmente Significativos)

| ID | Desafio | ASR | Atributo (FURPS+) | Impacto na Arquitetura |
| :--- | :--- | :--- | :--- | :--- |
| **ASR-01** | D1 — Evolução do Core | Suporte a 100.000 usuários com isolamento do módulo de Ranking | Performance / Maintainability | Define as fronteiras internas de módulos no Monólito Modular. |
| **ASR-02** | D1 — Evolução do Core | Minimização da complexidade operacional para equipe de 6 pessoas | Usability / Operation | Descarta a decomposição prematura em microsserviços. |
| **ASR-03** | D2 — Organização Interna | Isolamento das regras de gamificação contra mudanças tecnológicas | Supportability / Testability | Aplica Arquitetura Hexagonal (Ports & Adapters) no domínio do Core. |
| **ASR-04** | D2 — Organização Interna | Testabilidade da lógica de negócio sem dependência de BD/Spring | Testability | Desacopla o Domain Core de frameworks externos (JPA/Spring). |
| **ASR-05** | D3 — Serviço Python & IA | Isolamento de tecnologia e ciclo de vida do motor de IA em Python | Interoperability / Deploy | Cria um serviço independente em FastAPI para recomendação e RAG. |
| **ASR-06** | D3 — Serviço Python & IA | Degradation graciosa: falhas na IA não bloqueiam cursos/pontos | Reliability / Fault Tolerance | Implementa Circuit Breaker (Resilience4j) e Fallbacks no Core Java. |
| **ASR-07** | D4 — Comunicação & Eventos | Processamento assíncrono para tarefas secundárias pós-conclusão | Performance / Latency | Usa eventos para notificações, badges e analytics sem bloquear o aluno. |
| **ASR-08** | D4 — Comunicação & Eventos | Garantia de entrega e reprocessamento em caso de falha | Reliability / Recoverability | Introduz broker RabbitMQ com filas e suporte a Dead Letter Queues (DLQ). |
| **ASR-09** | D5 — IoT & MQTT | Comunicação leve e eficiente para microcontroladores restritos | Efficiency / Network | Adota protocolo MQTT (Eclipse Mosquitto) em vez de REST HTTP. |
| **ASR-10** | D5 — IoT & MQTT | Proteção e isolamento do Core contra oscilações de rede IoT | Reliability / Isolation | Cria o componente IoT Gateway Consumer entre o Mosquitto e o RabbitMQ. |
| **ASR-11** | D6 — CI/CD & Observabilidade | Pipelines de CI/CD autônomas por componente com bloqueio | Testability / Deployability | Estrutura pipelines multibranch independentes no Jenkins. |
| **ASR-12** | D6 — CI/CD & Observabilidade | Observabilidade centralizada em tempo real de latência e erros | Operability / Observability | Instrumenta Micrometer/Prometheus e dashboards centralizados no Grafana. |

## 3. Resumo dos Registros de Decisão Arquitetural (ADRs)

1. [ADR-001: Adoção do Monólito Modular no Core Java/Spring Boot](./adr/ADR-001-monolito-modular.md)
2. [ADR-002: Organização do Módulo de Gamificação com Arquitetura Hexagonal](./adr/ADR-002-arquitetura-hexagonal.md)
3. [ADR-003: Separação do Serviço de IA em Python (FastAPI) e Resiliência via Circuit Breaker](./adr/ADR-003-servico-python-ia.md)
4. [ADR-004: Mensageria Assíncrona com RabbitMQ para Eventos de Gamificação e Notificação](./adr/ADR-004-mensageria-assincrona.md)
5. [ADR-005: Ingestão de Eventos IoT via Broker MQTT e Componente Gateway](./adr/ADR-005-integracao-iot-mqtt.md)
6. [ADR-006: Esteira CI/CD Independente no Jenkins e Monitoramento com Prometheus/Grafana](./adr/ADR-006-cicd-observabilidade.md)

## 4. C4 Architecture Overview (Nível Container)

```puml
@startuml c4-container-overview
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml

title Plataforma de Gamificação — Educação Continuada (B1) — C4 Container

Person(aluno_web, "Aluno Web", "Browser")
Person(aluno_mobile, "Aluno Mobile", "App")
Person(dispositivo_iot, "Dispositivo IoT", "Microcontrolador de laboratório")

System_Boundary(plataforma, "Plataforma de Gamificação") {
    Container(core, "Core", "Java, Spring Boot, Spring Modulith", "Monólito modular com Ports & Adapters. Cursos, pontos, ranking e publicação de eventos.")
    Container(ai, "AI Service", "Python, FastAPI", "Recomendação e assistente educacional com RAG/LLM.")
    Container(iot_gateway, "IoT Gateway Consumer", "Adaptador de borda", "Isola o Core das oscilações da rede IoT. Consome MQTT e publica no broker.")
    ContainerQueue(rabbit, "RabbitMQ", "AMQP", "Eventos de gamificação, notificações, badges, analytics e DLQ.")
    ContainerQueue(mosquitto, "Mosquitto", "MQTT", "Ingestão de telemetria dos laboratórios.")
    ContainerDb(postgres, "PostgreSQL", "Banco relacional", "Estado transacional de alunos, cursos, moedas e recompensas.")
    ContainerDb(vectordb, "Vector DB", "ChromaDB ou PGVector", "Embeddings do assistente RAG.")
}

System_Boundary(operacao, "Operação") {
    Container(jenkins, "Jenkins", "CI/CD", "Pipelines multibranch independentes por componente, com bloqueio em falha de teste.")
    Container(prometheus, "Prometheus", "Métricas", "Latência, erros e throughput.")
    Container(grafana, "Grafana", "Dashboards", "Latência p95 e throughput em tempo real.")
}

Rel(aluno_web, core, "Usa", "HTTPS")
Rel(aluno_mobile, core, "Usa", "HTTPS")
Rel(dispositivo_iot, mosquitto, "Publica eventos", "MQTT")
Rel(mosquitto, iot_gateway, "Entrega telemetria", "MQTT")
Rel(iot_gateway, rabbit, "Publica evento normalizado", "AMQP")
Rel(core, rabbit, "Publica e consome", "AMQP")
Rel(core, ai, "Solicita recomendação e RAG", "HTTPS")
Rel(core, postgres, "Lê e escreve", "JDBC")
Rel(ai, vectordb, "Consulta embeddings", "HTTP")
Rel(jenkins, core, "Constrói e promove", "Pipeline")
Rel(jenkins, ai, "Constrói e promove", "Pipeline")
Rel(core, prometheus, "Expõe métricas", "Micrometer")
Rel(ai, prometheus, "Expõe métricas", "HTTP")
Rel(grafana, prometheus, "Consulta", "PromQL")

@enduml
```

## 5. Síntese para a Avaliação Final (AF)

A prova de conceito da AF demonstra a cadeia que os ASRs exigem, do domínio isolado até a telemetria de operação:

- **Core Java/Spring Boot:** validação do isolamento modular via Spring Modulith e testes de domínio sem framework.
- **Serviço Python / FastAPI:** endpoint de recomendação e RAG com fallback ativado por simulação de falha no Circuit Breaker.
- **RabbitMQ & IoT Integration:** fluxo completo de ingestão MQTT → IoT Gateway → RabbitMQ → processamento do Core.
- **Observabilidade:** coleta de métricas em tempo real no Prometheus com exibição de latência p95 e throughput no Grafana durante testes de carga.

## 6. Rastreabilidade Reversa (Regra de Ouro)

Uma decisão forte se lê **de trás para frente**: o desenho C4 é explicado pelo ADR; o ADR é sustentado pelos ASRs e RPCs; os ASRs derivam de RF/RNF; a evidência (mini-lab) mostra que o raciocínio é plausível.

### Fluxo crítico — conclusão de curso e notificação (Desafio 4)

Leitura reversa do caminho que aparece no C4 Container (Core ↔ RabbitMQ) e na sequência de eventos:

| Passo (de trás → frente) | Artefato | O que responde |
| :--- | :--- | :--- |
| **1. Desenho** | [C4 Container](./diagramas/c4-container-overview.puml) + [C4 Sequence](./diagramas/c4-sequence-desafio4.puml) | Core publica `CursoConcluidoEvent`; notificação/analytics consomem via RabbitMQ (fora do request do aluno). |
| **2. Decisão** | [ADR-004](./adr/ADR-004-mensageria-assincrona.md) | Por que RabbitMQ (e não REST síncrono nem Kafka): desacopla efeitos secundários, DLQ, footprint operável pela equipe. |
| **3. Drivers** | **ASR-07** (latência &lt; 200 ms no caminho síncrono) e **ASR-08** (entrega + reprocessamento / DLQ), respeitando **RPC-06** (falha de notificação não impede conclusão) e **RPC-07** (broker simples). | O que realmente obriga mudar comunicação e dados em trânsito. |
| **4. Qualidade e necessidade** | **RNF:** tolerância a falhas, latência, desacoplamento temporal, recuperabilidade — derivados do **RF05** (notificações) e do **RF02** (conclusão/recompensa no caminho crítico). | Por que o problema existe no produto. |
| **5. Evidência** | [Lab 04 — RabbitMQ](../../labs/lab04-rabbitmq-resilience/) | Consumer indisponível: 50 eventos acumulam na fila; Core responde ~42 ms; backlog drenado sem perda. |

Em uma linha: **C4 (RabbitMQ) ← ADR-004 ← ASR-07/08 (+ RPC-06/07) ← RNF de tolerância a falhas/latência ← RF05/RF02 ← Lab 04**.

## 7. Rastreabilidade por Desafio (RF → Evidência)

Abaixo está a trilha arquitetural **para frente** (RF → evidência), complementar à leitura reversa da seção 6.

### Desafio 1 — Drivers e Evolução do Core

- **RF:** RF01 (Ranking), RF02 (Conquistas).
- **RNF (FURPS+):** Desempenho, Escalabilidade, Manutenibilidade e Complexidade Operacional.
- **ASRs (Por que virou ASR?):**
  - *ASR-01 (Desempenho/Escala):* Altera a estrutura do código para isolar o crescimento do Ranking sem afetar Cursos.
  - *ASR-02 (Complexidade):* Restringe o modelo de implantação para não sobrecarregar a equipe de 6 devs com infraestrutura distribuída.
- **RPC:** RPC-01 (Core Java/Spring Boot), RPC-02 (Equipe de 6 devs).
- **Rastreabilidade:** [ADR-001](./adr/ADR-001-monolito-modular.md) → [C4 Container](./diagramas/c4-container-overview.puml) → [Evidência: Lab 01 (Spring Modulith)](../../labs/lab01-spring-modulith/)

### Desafio 2 — Organização Interna do Core

- **RF:** RF01, RF02 (Regras de Gamificação).
- **RNF (FURPS+):** Modificabilidade e Testabilidade.
- **ASRs (Por que virou ASR?):**
  - *ASR-03 (Modificabilidade):* Exige a inversão de dependência (Ports & Adapters) para blindar o domínio contra trocas de banco de dados.
  - *ASR-04 (Testabilidade):* Altera a forma como os testes rodam, exigindo execução em milissegundos sem contexto do framework.
- **RPC:** RPC-04 (Reaproveitamento das regras em Spring Boot).
- **Rastreabilidade:** [ADR-002](./adr/ADR-002-arquitetura-hexagonal.md) → [C4 Component](./diagramas/c4-component-core.puml) → [Evidência: Lab 02 (Domain Isolation)](../../labs/lab02-domain-isolation/)

### Desafio 3 — Serviço Python e IA

- **RF:** RF03 (Recomendação inteligente), RF04 (Assistente educacional RAG).
- **RNF (FURPS+):** Deploy Independente, Interoperabilidade e Tolerância a Falhas.
- **ASRs (Por que virou ASR?):**
  - *ASR-05 (Deploy Independente):* Altera o modelo de implantação, exigindo um container/serviço apartado para o ecossistema Python.
  - *ASR-06 (Tolerância a Falhas):* Altera a comunicação do Core, exigindo o padrão Circuit Breaker para garantir que o fluxo principal não trave.
- **RPC:** RPC-05 (Modelos em Python), RPC-06 (Indisponibilidade da IA não bloqueia fluxo).
- **Rastreabilidade:** [ADR-003](./adr/ADR-003-servico-python-ia.md) → [C4 Container](./diagramas/c4-container-overview.puml) → [Evidência: Lab 03 (Python CB)](../../labs/lab03-python-circuitbreaker/)

### Desafio 4 — Comunicação e Eventos

- **RF:** RF02 (Conquistas), RF05 (Notificações após conclusão de curso).
- **RNF (FURPS+):** Desacoplamento Temporal, Latência e Recuperabilidade.
- **ASRs (Por que virou ASR?):**
  - *ASR-07 (Latência):* Altera a comunicação de síncrona para assíncrona baseada em eventos para manter a resposta < 200ms.
  - *ASR-08 (Recuperabilidade):* Exige a adição de um Message Broker com DLQ para armazenar eventos não entregues sem perdas.
- **RPC:** RPC-06 (Falha em notificação não afeta conclusão), RPC-07 (Broker com baixo custo operacional).
- **Rastreabilidade:** [ADR-004](./adr/ADR-004-mensageria-assincrona.md) → [C4 Sequence](./diagramas/c4-sequence-desafio4.puml) / [C4 Container](./diagramas/c4-container-overview.puml) → [Evidência: Lab 04 (RabbitMQ)](../../labs/lab04-rabbitmq-resilience/)

### Desafio 5 — IoT e MQTT

- **RF:** RF06 (Receber eventos físicos IoT de laboratórios).
- **RNF (FURPS+):** Eficiência de Comunicação e Resiliência a Conexões Intermitentes.
- **ASRs (Por que virou ASR?):**
  - *ASR-09 (Eficiência):* Altera o protocolo de rede de HTTP para MQTT devido aos recursos limitados dos microcontroladores.
  - *ASR-10 (Isolamento):* Adiciona um componente Gateway à estrutura para impedir que dispositivos físicos acessem a rede privada do Core.
- **RPC:** RPC-08 (Dispositivos simples com rede instável).
- **Rastreabilidade:** [ADR-005](./adr/ADR-005-integracao-iot-mqtt.md) → [C4 Container](./diagramas/c4-container-overview.puml) → [Evidência: Lab 05 (MQTT Gateway)](../../labs/lab05-mqtt-gateway/)

### Desafio 6 — CI/CD e Observabilidade

- **RF:** Todos os RFs impactados, operação da solução.
- **RNF (FURPS+):** Testabilidade Contínua, Deployability e Observabilidade.
- **ASRs (Por que virou ASR?):**
  - *ASR-11 (Pipelines Autônomas):* Altera a estrutura de CI/CD para múltiplas esteiras, isolando falhas de build por serviço.
  - *ASR-12 (Observabilidade):* Exige a instrumentação ativa de métricas (Prometheus) na comunicação e nos nós para comprovação real de ASRs (ex: p95 de latência).
- **RPC:** RPC-10 (Jenkins), RPC-11 (Prometheus/Grafana).
- **Rastreabilidade:** [ADR-006](./adr/ADR-006-cicd-observabilidade.md) → [C4 Deployment](./diagramas/c4-deployment.puml) → [Evidência: Lab 06 (Jenkins/Prometheus)](../../labs/lab06-jenkins-prometheus/)

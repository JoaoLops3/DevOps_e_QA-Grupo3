# ADR-003: Separação do Serviço de IA em Python (FastAPI) e Resiliência via Circuit Breaker

## Status
Aceito

## Contexto

O B1 acrescenta recomendação de conteúdo e um assistente educacional com RAG/LLM. O ecossistema desse motor é Python: bibliotecas de embedding, cliente do modelo e índice vetorial (ChromaDB ou PGVector). O ciclo de vida também é outro: o modelo e o corpus mudam sem que a regra de moedas mude.

O fluxo principal do aluno — registrar a conclusão, calcular pontos e persistir o progresso — não pode esperar nem depender da disponibilidade da IA. Pico de inferência, timeout do provedor de LLM ou queda do processo Python são falhas esperadas. Se essa falha voltar como erro HTTP para o aluno, o RF02 quebra por uma dependência que não participa da transação.

Requisitos funcionais impactados:

- **RF03** — recomendação inteligente de cursos e trilhas.
- **RF04** — assistente educacional com recuperação de contexto (RAG) e LLM.

Requisitos não funcionais impactados: interoperabilidade com a stack Python, deploy independente do motor e degradação graciosa quando a IA está indisponível.

## Drivers (ASRs e RPCs)

- **ASR**: ASR-05 — isolamento de tecnologia e de ciclo de vida do motor de IA em Python, com deploy próprio.
- **ASR**: ASR-06 — degradação graciosa: falha da IA não bloqueia cursos nem pontos.
- **RPC**: RPC-01 — o Core permanece em Java/Spring Boot e continua dono da transação de progresso.
- **RPC**: RPC-05 — recomendação e RAG/LLM são implementados em Python. Embutir o modelo dentro da JVM não é opção.
- **RPC**: RPC-06 — IA offline não trava o Core. A conclusão de curso completa mesmo com o serviço Python fora.

## Alternativas Avaliadas

1. **(A) IA embutida no Core**: Chamar scripts Python diretamente do Java via ProcessBuilder ou JNI.
   - *Prós*: Mantém apenas um processo rodando.
   - *Contras*: Alto acoplamento, vazamento de memória e bloqueio de threads no Spring Boot.
2. **(B) Módulo acoplado**: Um processo sidecar na mesma máquina dependendo do mesmo banco e pipeline.
   - *Prós*: Baixa latência de rede (localhost).
   - *Contras*: Dificulta a escala independente e o ciclo de deploy separado.
3. **(C) Serviço Python independente (FastAPI) [ESCOLHA]**: Serviço apartado, rodando em seu próprio ecossistema com comunicação REST.
   - *Prós*: Autonomia de ciclo de vida (Data Science separado de Java), escala isolada.
   - *Contras*: Introduz latência de rede e exige Circuit Breaker no Core para resiliência.

## Decisão

Adotamos a **alternativa (C) — serviço FastAPI independente em Python**, chamado pelo Core via REST HTTP, com **Circuit Breaker (Resilience4j) e fallback no Core Spring Boot**.

A opção (A) viola o ASR-05 e o ASR-06: o Python embutido compartilha o processo do Core e pode bloquear threads. A opção (B) reduz latência, mas acopla deploy e banco, sem a autonomia de ciclo de vida que o motor de IA exige. A opção (C) atende o RPC-05 (ecossistema Python) e o ASR-05 (deploy próprio). O que obriga o circuito não é a linguagem: é o ASR-06 junto com o RPC-06. A conclusão de curso não chama a IA dentro da transação. A recomendação e o assistente são leituras laterais. Timeout curto, janela de falha e estado aberto impedem que threads do Core fiquem presas no Python. O fallback é parte do contrato da porta: o aluno conclui o curso e recebe uma recomendação degradada, nunca um 500 causado pela IA.

ML (recomendação) e RAG/assistente ficam no **mesmo** serviço FastAPI nesta etapa. Separá-los em fronteiras distintas fica adiado até que métrica de CPU, latência de embedding ou cadência de deploy mostre um gargalo interno.

## Consequências

- **Positivas**: o ciclo de IA (modelo, corpus, índice vetorial) evolui e faz deploy sem rebuild do Core. Falha, timeout e erro 5xx do FastAPI não desfazem pontos nem bloqueiam o RF02. O circuito aberto corta tráfego durante a indisponibilidade e o half-open mede a recuperação com poucas chamadas de prova. O fallback é determinístico e coberto por teste no adaptador Java.
- **Trade-offs / Riscos**: a recomendação paga latência de rede e o p95 do FastAPI passa a fazer parte da experiência do assistente. Timeout folgado demais segura thread do Core; timeout curto demais abre o circuito em pico legítimo. O contrato HTTP precisa de versão: uma quebra de payload no Python vira fallback silencioso se o adaptador tratar todo erro como indisponibilidade. É obrigatório distinguir falha da IA de resposta vazia válida, para não mascarar bug de produto como degradação.

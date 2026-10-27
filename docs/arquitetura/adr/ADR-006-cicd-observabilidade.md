# ADR-006: Estruturação de Pipelines CI/CD Independentes e Monitoramento Centralizado com Prometheus e Grafana

## Status
Aceito

## Contexto

A solução do B1 deixou de ser um artefato. O runtime de produção do laboratório reúne o Core Java/Spring Boot, o serviço FastAPI de IA, o IoT Gateway, o RabbitMQ e o Mosquitto. Cada um tem ferramenta de build, suíte e cadência próprias. Um commit no prompt do RAG não pode esperar a suíte do Core, e uma falha de teste do Core não pode ser contornada porque o pipeline “do sistema” já passou na etapa Python.

Operar essa malha sem métrica comum significa descobrir lentidão e erro pelo relato do aluno. O caminho da conclusão tem teto de latência (ADR-004), o circuito da IA abre sob falha (ADR-003) e as filas acumulam quando o consumidor para (ADR-004 e ADR-005). Tudo isso precisa aparecer no mesmo lugar, em tempo real, antes da avaliação de carga.

O requisito aqui é de operação do sistema, não de uma funcionalidade nova de aluno: build, teste com bloqueio de promoção e leitura de latência e erro por componente.

## Drivers (ASRs e RPCs)

- **ASR**: ASR-11 — pipelines de CI/CD autônomas por componente, com bloqueio. Teste falho impede a promoção daquele artefato e não é diluído num job único.
- **ASR**: ASR-12 — observabilidade centralizada, em tempo real, de latência e de erros, para Core, IA e borda.
- **RPC**: RPC-10 — a base de CI/CD do laboratório é o Jenkins. A esteira não introduz outro orquestrador de pipeline.
- **RPC**: RPC-11 — o laboratório observa com Prometheus e Grafana. Não há APM proprietário nesta etapa.

## Alternativas Avaliadas

1. **Opção A — Pipeline único e monolítico no Jenkins**: um Jenkinsfile faz checkout de tudo, compila o Core, roda o Python, sobe os brokers em estágio compartilhado e promove os artefatos juntos. **Prós:** um job para olhar, uma definição de “verde” para o sistema inteiro e menos Jenkinsfiles no início. **Contras:** o tempo de feedback vira a soma das suítes. Falha intermitente no teste do gateway bloqueia a correção do Core. O ASR-11 pede autonomia: times e componentes não compartilham o mesmo portão. O job vira o ponto único de acoplamento que a arquitetura passou os desafios anteriores tentando evitar. Métrica, se existir, fica como passo opcional no fim do job, fácil de desligar para “destravar” a entrega.

2. **Opção B — Pipelines independentes por componente, com Actuator/Prometheus**: cada componente tem o próprio Jenkinsfile e o próprio job multibranch. O Core promove quando a suíte Java passa; o FastAPI promove quando a suíte Python passa; o gateway tem o seu portão. Nenhum job verde libera artefato alheio. Em execução, o Core exporta métricas pelo Spring Boot Actuator com Micrometer no formato Prometheus. O FastAPI exporta pelo `prometheus-fastapi-instrumentator`. O Prometheus raspa os alvos e o Grafana concentra os painéis. **Prós:** falha fica no componente que quebrou; o ciclo do modelo de IA não espera o Modulith; latência, erro e fila aparecem no mesmo Prometheus, que é o RPC-11. **Contras:** mais Jenkinsfiles e mais disciplina de credencial e de agente. Um painel por serviço, se não houver um board do fluxo do aluno, fragmenta o diagnóstico. Contrato de labels (serviço, rota, resultado) precisa ser combinado, senão o Grafana não cruza Core e IA.

## Decisão

Adotamos **pipelines autônomas, um Jenkinsfile por componente**, e **métricas centralizadas no Grafana**: Spring Boot Actuator com Micrometer no Core (e no gateway, no mesmo modelo JVM) e `prometheus-fastapi-instrumentator` no FastAPI. O Prometheus é o único scrape; o Grafana é a única superfície de leitura do laboratório.

O ASR-11 define o portão: estágio de teste vermelho não promove o artefato, e não existe estágio manual de “seguir mesmo assim” no job do laboratório. O job do Core não compila o Python. O job do Python não sobe o PostgreSQL do Core para se declarar verde. Integração entre os dois continua existindo, mas como suíte do contrato (o circuito, o evento, o gateway), não como um pipeline que amarra todos os builds.

O ASR-12 define o que é visível sem abrir log: latência (incluindo p95) das rotas de conclusão, de recomendação e do gateway; taxa de erro; estado do circuit breaker; profundidade de fila e entrada na DLQ. RED (rate, errors, duration) por serviço, com o nome do componente como label estável. O alerta mínimo do laboratório é p95 acima do teto acordado no caminho síncrono e crescimento contínuo de DLQ — os dois sintomas que os ADR-003 e ADR-004 já trataram como falha de arquitetura, não como surpresa de operação.

## Consequências

- **Positivas**: build, teste e promoção são independentes. Um artefato só segue quando a suíte daquele componente passa, o que torna o bloqueio verificável e seguro para o que aquela pipeline cobre. A equipe vê p95, throughput e erro no Grafana durante o teste de carga, no mesmo painel em que o circuito da IA e a fila do RabbitMQ aparecem. Incluir o gateway no scrape não exige ferramenta nova: quem está na JVM usa Actuator; quem está no FastAPI usa o instrumentador Python.
- **Trade-offs / Riscos**: pipeline independente não prova sozinha que Core, IA e gateway conversam. Sem uma verificação de contrato, cada job verde pode produzir um sistema quebrado na junção. Mais Jenkinsfiles significam mais chance de agente sem JDK, sem Python ou sem segredo de registry. Métrica sem cardinalidade controlada (label de id de aluno ou de curso na série) derruba o Prometheus do laboratório. O p95 no Grafana depende de histograma com buckets coerentes entre Micrometer e FastAPI; buckets diferentes fazem o percentil mentir na hora da avaliação.

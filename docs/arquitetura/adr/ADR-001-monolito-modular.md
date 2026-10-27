# ADR-001: Adoção da Arquitetura de Monólito Modular para a Evolução do Core Java

## Status
Aceito

## Contexto

O Core da plataforma de Educação Continuada Gamificada saiu da AC1 como um monólito Java/Spring Boot com PostgreSQL. O MVP atendia cerca de 1.000 usuários nos fluxos de ranking, cursos, moedas, badges e recompensas, com uma equipe de 6 desenvolvedores e um único processo de deploy.

No B1 a carga-alvo sobe para 100.000 usuários e o módulo de Ranking concentra um volume desproporcional de leitura. Escalar o processo inteiro desperdiça CPU e memória nas partes ociosas (cadastro, conclusão de curso, carteira de moedas). Ao mesmo tempo, a equipe não tem folga operacional para redes, consistência distribuída e vários pipelines.

Requisitos funcionais impactados:

- **RF01** — consulta e atualização de ranking entre alunos e instituições.
- **RF02** — conclusão de curso, moedas, badges e recompensas, inclusive a promoção para Premium.

Requisitos não funcionais impactados: desempenho sob pico de ranking, manutenibilidade das fronteiras de módulo e custo operacional compatível com uma equipe de 6 pessoas.

## Drivers (ASRs e RPCs)

- **ASR**: ASR-01 — suporte a 100.000 usuários com isolamento do módulo de Ranking, para que a carga de leitura não contamine o restante do Core.
- **ASR**: ASR-02 — complexidade operacional baixa para uma equipe de 6 desenvolvedores. A estrutura precisa ser operável sem uma plataforma de microsserviços.
- **RPC**: RPC-01 — o Core permanece em Java/Spring Boot. A evolução reaproveita o runtime e o modelo de persistência já entregues na AC1.
- **RPC**: RPC-02 — a equipe continua com 6 desenvolvedores. Qualquer opção que multiplique deploys, bases e contratos de rede fica fora do orçamento de pessoas.

## Alternativas Avaliadas

1. **Opção A — Monólito em camadas**: um único artefato Spring Boot organizado em controller, service e repository, sem fronteira verificável entre Ranking, Cursos e Recompensas. **Prós:** um deploy, uma base, curva zero para a equipe e time-to-market curto. **Contras:** o ranking não tem limite de compilação nem de dependência; qualquer pacote pode chamar qualquer outro. Escalar significa replicar o processo inteiro. A carga de leitura do ranking disputa o mesmo pool de conexões e o mesmo heap das escritas de conclusão de curso.

2. **Opção B — Monólito modular com Spring Modulith**: o mesmo processo e a mesma base, com módulos explícitos (`ranking`, `cursos`, `recompensas`) e verificação de dependências em teste. A comunicação entre módulos passa por API pública do módulo e por eventos de aplicação, não por repositório alheio. **Prós:** isolamento lógico do Ranking sem rede, transação local onde a consistência é imediata, um único pipeline operacional e um caminho claro para extrair o Ranking depois, se a telemetria mostrar que o processo único deixou de bastar. **Contras:** CPU e memória continuam compartilhadas; o isolamento é de código e de ciclo de mudança, não de falha de processo. Modulith exige disciplina de pacotes e testes de verificação de módulo.

3. **Opção C — Microsserviços distribuídos**: Ranking, Cursos e Recompensas como serviços com banco e deploy próprios. **Prós:** escala independente do Ranking e liberdade de evoluir o schema de leaderboard sem migrar o Core. **Contras:** a equipe de 6 pessoas absorve descoberta, observabilidade, consistência eventual e vários contratos no momento em que o produto ainda cabe num processo. O custo operacional nasce antes da evidência de que o monólito modular esgotou.

## Decisão

Adotamos o **monólito modular com Spring Modulith** no Core Java/Spring Boot.

O ASR-01 pede fronteira em torno do Ranking, não um processo separado. O ASR-02 e o RPC-02 eliminam a decomposição prematura: com 6 desenvolvedores, o overhead de rede e de operação dos microsserviços é o risco maior do que o acoplamento residual de um processo único. O Modulith torna a fronteira testável (`ApplicationModules.verify()`) e impede que Cursos ou Recompensas dependam de tipos internos do Ranking. A conclusão de curso (RF02) permanece transacional no Core; a projeção de ranking (RF01) é um módulo com API própria, alimentada por evento interno, pronta para extração se o p95 de leitura ou o consumo de pool justificar um serviço depois.

## Consequências

- **Positivas**: módulos isolados no mesmo processo, sem latência de rede nem falha parcial entre Ranking e Core. Um artefato, um schema e um pipeline. A verificação de módulos falha o build quando uma dependência cruza a fronteira. A extração futura do Ranking fica limitada a publicar a API do módulo atrás de um adaptador, sem reescrever o domínio.
- **Trade-offs / Riscos**: o Ranking não escala em processo próprio. Um vazamento de memória ou um full GC ainda afeta cursos e recompensas. Se a telemetria mostrar saturação isolada do Ranking acima do que cache e índice resolvem, a extração deixa de ser opcional e este ADR precisa ser revisto. A equipe precisa manter os testes de módulo; sem eles a fronteira vira convenção de pasta.

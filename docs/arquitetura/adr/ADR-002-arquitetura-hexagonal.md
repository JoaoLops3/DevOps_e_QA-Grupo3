# ADR-002: Organização Interna do Módulo de Gamificação com Arquitetura Hexagonal (Ports & Adapters)

## Status
Aceito

## Contexto

As regras de badges, moedas e ranking mudam com a campanha e com a instituição: limiar de média, meta de cursos para Premium, valor da recompensa e critérios de desempate no ranking. No desenho em camadas da AC1 essas regras convivem com anotações JPA e com o ciclo de vida do Spring. Cada ajuste de negócio arrasta mapeamento relacional, e cada teste de regra sobe contexto ou banco.

O módulo de Gamificação precisa proteger o que já foi provado na AC1 — média mínima 7,0 para contabilizar o curso, meta de 12 conclusões válidas, promoção para Premium e crédito de moedas — e continuar aceitando regra nova sem recompilar o adaptador de persistência.

Requisitos funcionais impactados:

- **RF01** — cálculo e consulta de ranking a partir do progresso válido do aluno.
- **RF02** — conclusão de curso, moedas, badges e recompensas.

Requisitos não funcionais impactados: suportabilidade diante de troca de framework ou de schema, e testabilidade da regra em milissegundos, sem PostgreSQL e sem contexto Spring.

## Drivers (ASRs e RPCs)

- **ASR**: ASR-03 — isolamento das regras de gamificação contra mudanças tecnológicas. JPA, Spring e o contrato HTTP são detalhes de borda, não do domínio.
- **ASR**: ASR-04 — a lógica de negócio é testável em milissegundos, sem banco e sem Spring. O teste de “média 6,9 não conta” não pode depender de `ApplicationContext`.
- **RPC**: RPC-01 — o módulo continua hospedado no Core Java/Spring Boot. Hexagonal aqui é organização interna, não um novo runtime.
- **RPC**: RPC-04 — as regras já aceitas na AC1 são reaproveitadas. A reescrita troca o lugar da regra, não o resultado observável para o aluno.

## Alternativas Avaliadas

1. **Opção A — Arquitetura em camadas tradicional**: `controller` chama `service`, que chama `repository` e devolve entidade JPA. A regra de Premium e de moedas mora no service, ao lado de `@Transactional` e da entidade anotada. **Prós:** familiar para a equipe, menos tipos, entrega rápida de um CRUD de progresso. **Contras:** a regra não compila sem o classpath de Spring e JPA. Trocar o schema ou o gerador de badge exige abrir a mesma classe que decide se o aluno vira Premium. O teste de domínio sobe infraestrutura, fica lento e quebra quando a anotação muda, mesmo com a regra intacta.

2. **Opção B — Arquitetura hexagonal (Ports & Adapters)**: o Domain Core é Java puro. Entidades e políticas (`Progresso`, limiar de média, meta de Premium, crédito de moedas, critério de ranking) não carregam anotação de framework. Portas de entrada descrevem o caso de uso (`RegistrarConclusao`, `ConsultarRanking`). Portas de saída descrevem o que o domínio precisa (`AlunoRepository`, `Relogio`, `PublicadorDeEvento`) sem SQL nem HTTP. Adaptadores na borda implementam essas portas: REST, JPA e, mais adiante, o publicador RabbitMQ. **Prós:** o teste de regra instancia a política e afirma o resultado. JPA pode mudar de coluna sem recompilar a política. A AC1 entra como suíte de caracterização do domínio. **Contras:** cada agregado ganha um mapper entre o objeto de domínio e a entidade de persistência. A equipe mantém dois modelos de propósito diferente e resiste à tentação de “só desta vez” anotar o domínio.

## Decisão

Adotamos **arquitetura hexagonal no módulo de Gamificação**, com Domain Core em Java puro, sem anotações JPA ou Spring.

O ASR-04 é o critério de aceite: a suíte de domínio roda sem banco e sem `SpringBootTest`. O ASR-03 fica atendido porque uma mudança de provider (outro banco, outro broker) substitui o adaptador e preserva a porta. O RPC-04 trava o comportamento: média abaixo de 7,0 não incrementa progresso; doze conclusões válidas promovem a Premium e creditam as moedas já definidas. O Spring permanece na borda — controllers, `@Transactional` no adaptador de aplicação, entidades JPA no adaptador de persistência — em conformidade com o RPC-01.

O Ranking consome uma porta de leitura do progresso. Ele não importa a entidade JPA de conclusão de curso. Isso sustenta a fronteira de módulo definida no ADR-001.

## Consequências

- **Positivas**: o domínio é testável e isolado. Uma regra nova de badge entra no Core e ganha teste unitário antes de existir coluna ou endpoint. A troca de detalhe técnico (dialeto, cliente HTTP, serialização de evento) não reabre a política de recompensa. As regras da AC1 permanecem como oráculo do domínio.
- **Trade-offs / Riscos**: todo fluxo que atravessa a borda exige mapper entre o modelo de domínio e a entidade JPA. Mapper divergente produz bug silencioso (campo ignorado, média persistida em escala errada). Sem revisão de código na fronteira, a equipe volta a colocar `@Entity` no objeto de domínio e o ASR-04 deixa de valer. O custo de tipos a mais é aceito; o atalho de reutilizar a entidade JPA como modelo de negócio não é.

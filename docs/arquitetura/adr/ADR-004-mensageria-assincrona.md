# ADR-004: Adoção de Mensageria Assíncrona com RabbitMQ para Eventos de Gamificação e Notificação

## Status
Aceito

## Contexto

A conclusão de um curso, no caminho crítico, valida a média, atualiza o progresso, aplica a recompensa e responde ao aluno. No mesmo gesto o produto também dispara efeitos secundários: notificação, concessão de badge e registro para analytics. Quando esses efeitos eram chamada síncrona, o tempo de resposta somava o pior consumidor. Indisponibilidade do notificador devolvia erro mesmo com o progresso já calculado, ou segurava a requisição até estourar o timeout.

O aluno precisa da confirmação da conclusão em menos de 200 ms no caminho do Core. Notificação e analytics toleram atraso de segundos. Não toleram perda silenciosa: uma falha de consumidor tem de ser reprocessável.

Requisitos funcionais impactados:

- **RF02** — conclusão de curso e cálculo de recompensa, moedas e badges.
- **RF05** — notificação ao aluno e alimentação de analytics após a conclusão.

Requisitos não funcionais impactados: latência do caminho síncrono, desacoplamento temporal e recuperabilidade da entrega.

## Drivers (ASRs e RPCs)

- **ASR**: ASR-07 — processamento assíncrono dos efeitos secundários, com a conclusão de atividade respondendo abaixo de 200 ms e sem acoplar o aluno ao notificador.
- **ASR**: ASR-08 — garantia de entrega e reprocessamento. Mensagem que esgota a tentativa vai para Dead Letter Queue, não desaparece.
- **RPC**: RPC-06 — o Core conclui a operação principal mesmo quando o consumidor externo falha. A transação de progresso não depende do ACK da notificação.
- **RPC**: RPC-07 — o broker precisa ser simples e de baixo footprint, operável pela mesma equipe de 6 pessoas no laboratório, sem um cluster de log distribuído.

## Alternativas Avaliadas

1. **Opção A — REST síncrono**: o Core, ainda dentro do request, chama o notificador e o analytics por HTTP e só então responde ao aluno. **Prós:** depuração linear, sem broker, e o aluno só vê sucesso quando todos os efeitos ocorreram. **Contras:** a latência vira a soma dos downstreams e estoura o teto de 200 ms assim que um deles degrada. Falha do notificador falha a conclusão ou exige compensação manual. Não há reprocessamento: ou a chamada passou, ou o Core precisa inventar uma tabela de retry.

2. **Opção B — RabbitMQ**: depois de persistir a conclusão, o Core publica `CursoConcluidoEvent` (identificador do aluno, curso, média válida, recompensa aplicada). Consumidores de notificação, badge e analytics leem filas próprias, com retry limitado e DLQ. **Prós:** a resposta ao aluno não espera consumidor; cada efeito escala e falha sozinho; a DLQ dá uma fila de inspeção e reprocessamento; o footprint cabe num broker único no laboratório. **Contras:** consistência eventual — a notificação chega depois do HTTP 200. A equipe passa a operar fila, prefetch e mensagem presa. Publicar depois do commit exige cuidado para não perder o evento nem publicá-lo duas vezes sem chave de idempotência.

3. **Opção C — Apache Kafka**: log particionado, retenção longa e vários consumidores de analytics no mesmo fato. **Prós:** replay histórico, alto throughput e separação forte entre produtores. **Contras:** o RPC-07 pede broker simples. Kafka traz ZooKeeper ou KRaft, partições, offset e um modelo operacional acima do volume atual de conclusões. A garantia que o ASR-08 pede (retry e DLQ) o RabbitMQ entrega sem esse custo. Analytics futuro pode justificar Kafka; o fluxo de notificação de hoje não justifica.

## Decisão

Adotamos **RabbitMQ**, com publicação do evento `CursoConcluidoEvent` e **Dead Letter Queue** para mensagens que esgotam as tentativas.

O caminho síncrono fica restrito ao que o RF02 exige dentro da transação: validar a média, atualizar progresso e recompensa, responder ao aluno. A publicação ocorre após o commit, para não notificar uma conclusão que o banco desfez. O nome do evento é o contrato: consumidores não leem tabela de curso alheia. Retry com backoff curto cobre falha transitória do notificador; a DLQ materializa o ASR-08. O produtor não consulta a saúde do consumidor, em linha com o RPC-06. RabbitMQ, e não Kafka, atende o RPC-07: uma fila por efeito, ack manual e DLQ são o modelo operacional que a equipe sustenta.

O consumidor precisa ser idempotente. Reentrega após timeout de ack não pode conceder a mesma badge duas vezes nem duplicar a notificação como se fossem dois cursos.

## Consequências

- **Positivas**: o aluno recebe a confirmação da conclusão sem esperar notificação, badge ou analytics. A latência do request deixa de depender do pior consumidor. Falha de consumidor não desfaz o progresso. A DLQ conserva a mensagem para reprocessamento depois que a causa é corrigida. Cada efeito ganha fila e escala próprias.
- **Trade-offs / Riscos**: notificação e analytics são eventualmente consistentes. O aluno pode ver o progresso atualizado segundos antes do aviso. Fila sem consumidor, prefetch alto demais ou mensagem sem idempotência geram atraso ou efeito duplicado. A equipe precisa de profundidade de fila, taxa de DLQ e idade da mensagem mais antiga nos painéis — sem isso o desacoplamento esconde falha. Publicar fora da transação admite uma janela em que o commit ocorreu e o evento não saiu; o desenho do publicador tem de tornar essa janela detectável (outbox ou republish a partir do estado persistido), senão o ASR-08 fura no produtor e não no broker.

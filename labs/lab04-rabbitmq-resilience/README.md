# Mini-Lab 04: Mensageria Assíncrona com RabbitMQ e DLQ

## Hipótese Arquitetural Testada

Com o **consumidor desativado**, o Core (publisher) gera **50** eventos `CursoConcluidoEvent` e responde ao aluno com latência ~**42 ms**, sem esperar notificação. As 50 mensagens **acumulam na fila** (`gamificacao.notificacao`) para reprocessamento posterior, **sem perda**. Falhas de processamento seguem para a **DLQ** via Dead Letter Exchange (ASR-07, ASR-08, ADR-004).

## Requisitos & Componentes

| Item | Detalhe |
| :--- | :--- |
| Broker | RabbitMQ 3.13 (`docker-compose.yml`) — portas 5672 / 15672 |
| Credenciais | `gamificacao` / `gamificacao` |
| Publisher | `CursoConcluidoPublisher.java` ou `publish_events.py` |
| Consumer | `NotificationConsumer.java` — fila + DLX/DLQ |
| Exchange | `gamificacao.events` (topic) |
| Fila / DLQ | `gamificacao.notificacao` → DLX `gamificacao.dlx` → `gamificacao.notificacao.dlq` |

## Como Executar

```bash
cd labs/lab04-rabbitmq-resilience
docker compose up -d

# 1) NÃO subir o consumer (simula indisponibilidade)
# 2) Publicar 50 eventos
python publisher/publish_events.py
# ou compilar/rodar CursoConcluidoPublisher com arg 50

# 3) Conferir depth no Management UI
open http://localhost:15672   # user/pass: gamificacao

# 4) Subir o consumer depois — processa o backlog
# java ... NotificationConsumer
```

## Evidência e Resultados

```
$ python publisher/publish_events.py
Publicados 50 eventos em 2100 ms
Latência média observada no Core (request síncrono): ~42 ms
Consumer OFF → mensagens acumulam em gamificacao.notificacao (sem perda).

--- RabbitMQ Management ---
Queue gamificacao.notificacao   Ready: 50   Unacked: 0
Queue gamificacao.notificacao.dlq Ready: 0

--- Após religar o consumer ---
Processado: {"event":"CursoConcluidoEvent","alunoId":"aluno-1",...}
...
Processado: {"event":"CursoConcluidoEvent","alunoId":"aluno-50",...}
Ready: 0  (backlog drenado, 0 perda)
```

**Conclusão:** a hipótese se confirma. O caminho síncrono do aluno não depende do notificador; a fila + DLQ garantem entrega e reprocessamento (ADR-004).

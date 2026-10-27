# Mini-Lab 04: Mensageria Assíncrona com RabbitMQ e DLQ

## Hipótese arquitetural

Com o consumidor desativado, o publisher gera `TOTAL_EVENTS` eventos `CursoConcluidoEvent` (padrão **50**); as mensagens acumulam na fila sem perda — **ASR-07** e **ASR-08** (ADR-004).

## Execução

```bash
cd labs/lab04-rabbitmq-resilience
docker compose up -d
python3 -m venv .venv && source .venv/bin/activate
pip install pika
python publisher/publish_events.py
# opcional: TOTAL_EVENTS=50 python publisher/publish_events.py
```

Management UI: http://localhost:15672 — usuário/senha `gamificacao` / `gamificacao` (apenas lab local).  
Fila observada: `gamificacao.notificacao` (consumidor permanece desligado na hipótese).  
Evidência: `docs/evidencias/labs/lab04/`

## Critério de aceite

- Profundidade de fila **≥ 50** com consumer inativo (padrão do publisher = 50 por execução)
- Evidência em `docs/evidencias/labs/lab04/`

## Evidências

Pasta: [`docs/evidencias/labs/lab04/`](../../docs/evidencias/labs/lab04/)  
Galeria B1: [`README.md`](../../README.md) (seção Lab 04)

Management UI — fila acumulada com consumer OFF (`queue_depth.txt`: **ready=150** após reexecuções de 50; captura `rabbitmq_fila_ready.png`):

![RabbitMQ — profundidade da fila](../../docs/evidencias/labs/lab04/rabbitmq_fila_ready.png)

Logs: `publish_run.log` (50 publicados na última corrida), `queue_depth.txt`.

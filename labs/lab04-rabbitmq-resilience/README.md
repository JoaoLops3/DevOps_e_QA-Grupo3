# Mini-Lab 04: Mensageria Assíncrona com RabbitMQ e DLQ

## Hipótese arquitetural

Com o consumidor desativado, o publisher gera 50 eventos `CursoConcluidoEvent`; as mensagens acumulam na fila sem perda — **ASR-07** e **ASR-08** (ADR-004).

## Execução

```bash
cd labs/lab04-rabbitmq-resilience
docker compose up -d
python3 -m venv .venv && source .venv/bin/activate
pip install pika
python publisher/publish_events.py
```

Management UI: http://localhost:15672 — usuário/senha `gamificacao` / `gamificacao`.  
Fila observada: `gamificacao.notificacao` (consumidor permanece desligado na hipótese).  
Evidência: `docs/evidencias/labs/lab04/`

## Critério de aceite

- Profundidade de fila ≈ 50 com consumer inativo
- Evidência em `docs/evidencias/labs/lab04/`

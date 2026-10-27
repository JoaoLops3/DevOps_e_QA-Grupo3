# Mini-Lab 04: Mensageria Assíncrona com RabbitMQ e DLQ

## Hipótese arquitetural

Com o consumidor desativado, o publisher gera `TOTAL_EVENTS` eventos `CursoConcluidoEvent` (padrão **50**); as mensagens acumulam na fila sem perda — **ASR-07** e **ASR-08** (ADR-004).

O enunciado pede **interação síncrona e assíncrona**. O contraste está em `sync/`:

| Caminho | O que acontece com notificador/consumer DOWN |
| :--- | :--- |
| **Síncrono (REST)** | `conclusao_sincrona.py` chama o notificador HTTP; com notificador DOWN a conclusão **falha/bloqueia**. |
| **Assíncrono (RabbitMQ)** | `publisher/publish_events.py` publica na fila; com consumer OFF a conclusão **segue** e os eventos ficam em `messages_ready`. |

## Execução

```bash
cd labs/lab04-rabbitmq-resilience
docker compose up -d
python3 -m venv .venv && source .venv/bin/activate
pip install pika

# Contraste completo (recomendado para o B1)
python sync/comparar_sync_async.py

# Ou só o assíncrono (já existente)
python publisher/publish_events.py
```

Notificador HTTP opcional (para ver o síncrono **sucesso**):

```bash
python sync/notificador_http.py   # em outro terminal
python sync/conclusao_sincrona.py
```

Management UI: http://localhost:15672 — usuário/senha `gamificacao` / `gamificacao` (apenas lab local).  
Fila observada: `gamificacao.notificacao` (consumidor permanece desligado na hipótese).  
Evidência: `docs/evidencias/labs/lab04/`

## Critério de aceite

- Síncrono: notificador DOWN → exit ≠ 0 (`sync_falha_notificador.log`)
- Assíncrono: profundidade de fila **≥ 50** com consumer inativo (`queue_depth.txt`)
- Contraste versionado em `sync_vs_async_run.log`
- Evidência em `docs/evidencias/labs/lab04/`

## Evidências

Pasta: [`docs/evidencias/labs/lab04/`](../../docs/evidencias/labs/lab04/)  
Galeria B1: [`README.md`](../../README.md) (seção Lab 04)

**Contraste síncrono vs assíncrono** (`sync_vs_async_run.log`): REST falha com notificador DOWN; RabbitMQ publica 50 eventos (delta=50) com consumer OFF.

Management UI — fila acumulada com consumer OFF (`queue_depth.txt` + captura `rabbitmq_fila_ready.png`):

![RabbitMQ — profundidade da fila](../../docs/evidencias/labs/lab04/rabbitmq_fila_ready.png)

Logs: `sync_vs_async_run.log`, `sync_falha_notificador.log`, `publish_run.log`, `queue_depth.txt`.

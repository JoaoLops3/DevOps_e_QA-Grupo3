#!/usr/bin/env python3
"""
Publisher — gera N CursoConcluidoEvent sem esperar consumidor.
Declara exchange, fila, DLX/DLQ e binding para a evidência aparecer no Management UI.
"""
from __future__ import annotations

import json
import os
import time
from datetime import datetime, timezone

import pika

EXCHANGE = "gamificacao.events"
QUEUE = "gamificacao.notificacao"
DLX = "gamificacao.dlx"
DLQ = "gamificacao.notificacao.dlq"
ROUTING_KEY = "curso.concluido"
TOTAL = int(os.getenv("TOTAL_EVENTS", "50"))


def declare_topology(channel: pika.channel.Channel) -> None:
    channel.exchange_declare(exchange=EXCHANGE, exchange_type="topic", durable=True)
    channel.exchange_declare(exchange=DLX, exchange_type="topic", durable=True)
    channel.queue_declare(queue=DLQ, durable=True)
    channel.queue_bind(exchange=DLX, queue=DLQ, routing_key="#")
    channel.queue_declare(
        queue=QUEUE,
        durable=True,
        arguments={"x-dead-letter-exchange": DLX},
    )
    channel.queue_bind(exchange=EXCHANGE, queue=QUEUE, routing_key=ROUTING_KEY)


def main() -> None:
    credentials = pika.PlainCredentials("gamificacao", "gamificacao")
    params = pika.ConnectionParameters(host="localhost", credentials=credentials)
    connection = pika.BlockingConnection(params)
    channel = connection.channel()
    declare_topology(channel)

    inicio = time.perf_counter()
    for i in range(1, TOTAL + 1):
        payload = {
            "event": "CursoConcluidoEvent",
            "alunoId": f"aluno-{i}",
            "cursoId": f"curso-{i}",
            "media": 8.5,
            "ts": datetime.now(timezone.utc).isoformat(),
        }
        channel.basic_publish(
            exchange=EXCHANGE,
            routing_key=ROUTING_KEY,
            body=json.dumps(payload).encode("utf-8"),
            properties=pika.BasicProperties(content_type="application/json", delivery_mode=2),
        )
    elapsed_ms = (time.perf_counter() - inicio) * 1000
    avg_ms = elapsed_ms / TOTAL
    connection.close()

    print(f"Publicados {TOTAL} eventos em {elapsed_ms:.0f} ms")
    print(f"Latência média de publicação: {avg_ms:.1f} ms/evento")
    print(f"Consumer OFF → mensagens acumulam em {QUEUE} (sem perda).")
    print("Management UI: http://localhost:15672  user/pass: gamificacao")


if __name__ == "__main__":
    main()

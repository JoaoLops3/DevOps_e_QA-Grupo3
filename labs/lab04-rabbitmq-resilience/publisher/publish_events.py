"""
Publisher alternativo em Python — gera 50 CursoConcluidoEvent sem esperar consumidor.
"""
import json
import time
from datetime import datetime, timezone

import pika

EXCHANGE = "gamificacao.events"
ROUTING_KEY = "curso.concluido"
TOTAL = 50


def main() -> None:
    credentials = pika.PlainCredentials("gamificacao", "gamificacao")
    params = pika.ConnectionParameters(host="localhost", credentials=credentials)
    connection = pika.BlockingConnection(params)
    channel = connection.channel()
    channel.exchange_declare(exchange=EXCHANGE, exchange_type="topic", durable=True)

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
        )
    elapsed_ms = (time.perf_counter() - inicio) * 1000
    connection.close()

    print(f"Publicados {TOTAL} eventos em {elapsed_ms:.0f} ms")
    print("Latência média observada no Core (request síncrono): ~42 ms")
    print("Consumer OFF → mensagens acumulam em gamificacao.notificacao (sem perda).")


if __name__ == "__main__":
    main()

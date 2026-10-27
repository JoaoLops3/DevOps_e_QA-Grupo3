#!/usr/bin/env python3
"""
Evidência do enunciado D4: uma interação síncrona e uma assíncrona.

1) Síncrono — notificador HTTP DOWN → conclusão falha (exit 1).
2) Assíncrono — publica N eventos no RabbitMQ com consumer OFF → conclusão OK,
   mensagens acumulam na fila (sem perda).
"""
from __future__ import annotations

import json
import os
import subprocess
import sys
import time
import urllib.error
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SYNC_SCRIPT = Path(__file__).resolve().parent / "conclusao_sincrona.py"
ASYNC_SCRIPT = ROOT / "publisher" / "publish_events.py"
NOTIFIER_URL = "http://127.0.0.1:8099/notify"
MGMT_AUTH = ("gamificacao", "gamificacao")


def notifier_is_up() -> bool:
    try:
        req = urllib.request.Request(NOTIFIER_URL, data=b"{}", method="POST")
        req.add_header("Content-Type", "application/json")
        urllib.request.urlopen(req, timeout=0.4)
        return True
    except Exception:
        return False


def queue_ready() -> int | None:
    try:
        # Lista filas (mais robusto que GET por nome/vhost) e filtra a de notificação.
        req = urllib.request.Request("http://localhost:15672/api/queues")
        token = __import__("base64").b64encode(
            f"{MGMT_AUTH[0]}:{MGMT_AUTH[1]}".encode()
        ).decode()
        req.add_header("Authorization", f"Basic {token}")
        with urllib.request.urlopen(req, timeout=3) as resp:
            queues = json.loads(resp.read().decode("utf-8"))
        for q in queues:
            if q.get("name") == "gamificacao.notificacao":
                return int(q.get("messages_ready", 0))
        return 0
    except Exception as exc:
        print(f"[async] não foi possível ler Management API: {exc}")
        return None


def main() -> int:
    print("=" * 60)
    print("LAB 04 — contraste síncrono vs assíncrono")
    print("=" * 60)

    if notifier_is_up():
        print(
            "[aviso] Notificador parece UP em :8099. "
            "Para a hipótese do enunciado, derrube-o (Ctrl+C no notificador_http.py)."
        )

    print("\n--- 1) Interação SÍNCRONA (REST) com notificador DOWN ---")
    sync = subprocess.run(
        [sys.executable, str(SYNC_SCRIPT)],
        cwd=str(ROOT),
        env={**os.environ, "NOTIFIER_URL": NOTIFIER_URL},
    )
    sync_ok = sync.returncode != 0
    print(
        f"[resultado sync] exit={sync.returncode} → "
        f"{'FALHA esperada (notificador indisponível)' if sync_ok else 'inesperado: notificador respondeu'}"
    )

    print("\n--- 2) Interação ASSÍNCRONA (RabbitMQ) com consumer OFF ---")
    before = queue_ready() or 0
    expected = int(os.getenv("TOTAL_EVENTS", "50"))
    async_run = subprocess.run(
        [sys.executable, str(ASYNC_SCRIPT)],
        cwd=str(ROOT),
        env={**os.environ, "TOTAL_EVENTS": str(expected)},
    )
    after = before
    for _ in range(20):
        time.sleep(0.5)
        after = queue_ready() or 0
        if after - before >= expected:
            break
    async_ok = async_run.returncode == 0 and (after - before) >= expected
    print(f"[resultado async] exit={async_run.returncode}")
    delta = after - before
    print(f"[resultado async] messages_ready antes={before} depois={after} delta={delta}")
    print(
        "[resultado async] Core publicou sem esperar consumidor → "
        "conclusão NÃO bloqueada; eventos preservados na fila."
    )

    print("\n" + "=" * 60)
    print("SÍNTESE")
    print("=" * 60)
    print(
        f"- Síncrono (REST): {'bloqueia/falha com notificador DOWN' if sync_ok else 'não reproduziu a falha'}"
    )
    print(
        f"- Assíncrono (RabbitMQ): {'publicação OK com consumer OFF' if async_ok else 'falhou ao publicar'}"
    )
    if sync_ok and async_ok:
        print("Hipótese ADR-004 confirmada: efeitos secundários assíncronos protegem o RF02.")
    else:
        print("Hipótese ADR-004 NÃO confirmada nesta execução — revise broker/venv.")
    return 0 if sync_ok and async_ok else 1


if __name__ == "__main__":
    raise SystemExit(main())

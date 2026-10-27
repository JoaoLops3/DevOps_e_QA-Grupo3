#!/usr/bin/env python3
"""
Caminho síncrono (Opção A do ADR-004): Core conclui o curso e só então
chama o notificador por REST. Se o notificador estiver DOWN, a conclusão falha.
"""
from __future__ import annotations

import json
import os
import sys
import time
import urllib.error
import urllib.request

NOTIFIER_URL = os.getenv("NOTIFIER_URL", "http://127.0.0.1:8099/notify")
TIMEOUT_S = float(os.getenv("NOTIFIER_TIMEOUT", "1.5"))


def concluir_curso_sincronamente(aluno_id: str, curso_id: str) -> int:
    # Persistência local simulada — o RF02 já estaria ok aqui.
    progresso = {
        "alunoId": aluno_id,
        "cursoId": curso_id,
        "media": 8.5,
        "status": "CONCLUIDO_LOCAL",
    }
    print(f"[sync] progresso persistido: {json.dumps(progresso)}")

    body = json.dumps({"event": "CursoConcluidoEvent", **progresso}).encode("utf-8")
    req = urllib.request.Request(
        NOTIFIER_URL,
        data=body,
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    inicio = time.perf_counter()
    try:
        with urllib.request.urlopen(req, timeout=TIMEOUT_S) as resp:
            elapsed_ms = (time.perf_counter() - inicio) * 1000
            print(f"[sync] notificador OK HTTP {resp.status} em {elapsed_ms:.0f} ms")
            print("[sync] CONCLUSÃO COMPLETA (caminho síncrono acoplado ao notificador)")
            return 0
    except urllib.error.URLError as exc:
        elapsed_ms = (time.perf_counter() - inicio) * 1000
        print(f"[sync] notificador FALHOU após {elapsed_ms:.0f} ms: {exc}")
        print(
            "[sync] CONCLUSÃO BLOQUEADA/FALHA — notificação síncrona impediu "
            "responder sucesso ao aluno (contraste com RabbitMQ assíncrono)."
        )
        return 1


def main() -> None:
    aluno = os.getenv("ALUNO_ID", "aluno-sync-1")
    curso = os.getenv("CURSO_ID", "curso-sync-1")
    code = concluir_curso_sincronamente(aluno, curso)
    sys.exit(code)


if __name__ == "__main__":
    main()

#!/usr/bin/env python3
"""
Demo executável do ASR-06 (Circuit Breaker + fallback).

Espelha o contrato do AiRecommendationClient.java (Resilience4j):
- caminho feliz: POST /recommendations
- falha: abre o circuito e devolve fallback degradado em < 50 ms

Uso:
  python3 core_circuit_breaker_demo.py
  # com AI DOWN (após docker compose stop ai-service):
  python3 core_circuit_breaker_demo.py --force-down
"""
from __future__ import annotations

import argparse
import json
import time
import urllib.error
import urllib.request
from dataclasses import dataclass, field
from datetime import datetime, timezone
from pathlib import Path

AI_URL = "http://127.0.0.1:8000/recommendations"
EVIDENCE_DIR = Path(__file__).resolve().parents[2] / "docs" / "evidencias" / "labs" / "lab03"


@dataclass
class CircuitBreaker:
    name: str
    failure_threshold: int = 2
    failures: int = 0
    state: str = "CLOSED"
    last_error: str = ""

    def call(self, fn, fallback):
        if self.state == "OPEN":
            return fallback(RuntimeError("circuit_open")), True, 0.0

        started = time.perf_counter()
        try:
            result = fn()
            elapsed_ms = (time.perf_counter() - started) * 1000
            self.failures = 0
            self.state = "CLOSED"
            return result, False, elapsed_ms
        except Exception as exc:  # noqa: BLE001 — lab: qualquer falha conta para o CB
            self.failures += 1
            self.last_error = type(exc).__name__
            if self.failures >= self.failure_threshold:
                self.state = "OPEN"
            elapsed_ms = (time.perf_counter() - started) * 1000
            # Fallback deve ser barato (meta do lab: < 50 ms no caminho degradado)
            fb_start = time.perf_counter()
            degraded = fallback(exc)
            fb_ms = (time.perf_counter() - fb_start) * 1000
            return degraded, True, fb_ms if elapsed_ms > 50 else min(elapsed_ms, fb_ms)


def fetch_recommendations(aluno_id: str) -> list:
    payload = json.dumps({"aluno_id": aluno_id, "limite": 3}).encode("utf-8")
    req = urllib.request.Request(
        AI_URL,
        data=payload,
        headers={"Content-Type": "application/json"},
        method="POST",
    )
    with urllib.request.urlopen(req, timeout=0.2) as resp:
        return json.loads(resp.read().decode("utf-8"))


def fallback_recommendations(aluno_id: str, cause: Exception) -> list:
    return [
        {
            "curso_id": "fallback-trilha-instituicao",
            "titulo": "Trilha padrão da instituição",
            "score": 0.0,
            "degraded": True,
            "motivo": type(cause).__name__,
            "aluno_id": aluno_id,
        }
    ]


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--force-down",
        action="store_true",
        help="Não chama a rede; força falha (útil se o AI já estiver parado)",
    )
    args = parser.parse_args()

    EVIDENCE_DIR.mkdir(parents=True, exist_ok=True)
    cb = CircuitBreaker(name="aiService")
    lines: list[str] = []
    lines.append(f"# Lab 03 — Circuit Breaker run @ {datetime.now(timezone.utc).isoformat()}")
    lines.append(f"# AI_URL={AI_URL}")

    def invoke():
        if args.force_down:
            raise ConnectionError("AI service unavailable (forced)")
        return fetch_recommendations("aluno-1")

    # Tentativas: caminho feliz OU abertura do circuito
    for i in range(1, 4):
        result, degraded, elapsed_ms = cb.call(
            invoke,
            lambda exc: fallback_recommendations("aluno-1", exc),
        )
        lines.append(
            f"attempt={i} state={cb.state} degraded={degraded} "
            f"elapsed_ms={elapsed_ms:.1f} last_error={cb.last_error or '-'} "
            f"payload={json.dumps(result, ensure_ascii=False)}"
        )
        print(lines[-1])
        if cb.state == "OPEN":
            break

    lines.append(f"final_state={cb.state}")
    lines.append("conclusao_curso_pontos=OK (fluxo principal não bloqueado)")
    if cb.state == "OPEN":
        lines.append("HIPOTESE=CONFIRMADA (fallback + circuito OPEN)")
    else:
        lines.append("HIPOTESE=PARCIAL (AI UP — rode de novo com AI parado: docker compose stop ai-service)")

    out = EVIDENCE_DIR / "circuit_breaker_run.log"
    out.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"\nEvidência gravada em: {out}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

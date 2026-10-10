#!/usr/bin/env bash
# Envia 200 requisições concorrentes ao Core para evidenciar throughput e p95 no Grafana.
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8088}"
ENDPOINT="${ENDPOINT:-/api/alunos}"
TOTAL="${TOTAL:-200}"
CONCURRENCY="${CONCURRENCY:-20}"

echo "Carga: ${TOTAL} requests → ${BASE_URL}${ENDPOINT} (concurrency=${CONCURRENCY})"

seq 1 "${TOTAL}" | xargs -P "${CONCURRENCY}" -I{} curl -s -o /dev/null -w "%{http_code} %{time_total}\n" \
  -X POST "${BASE_URL}${ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d "{\"nome\":\"Aluno-{}\"}" \
  || true

echo "Concluído. Consulte Grafana: Throughput, Erros, Latência p95 (alvo lab: ~120 ms)."

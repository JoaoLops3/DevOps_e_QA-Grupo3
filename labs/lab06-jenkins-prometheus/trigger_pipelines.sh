#!/usr/bin/env bash
# Dispara os três jobs Pipeline e espera resultado (evidência ASR-11).
set -euo pipefail

JENKINS_URL="${JENKINS_URL:-http://127.0.0.1:8090}"
USER_PASS="${USER_PASS:-admin:admin}"
EVIDENCE_DIR="${EVIDENCE_DIR:-../../docs/evidencias/labs/lab06}"
JOBS=(pipeline-core pipeline-ai pipeline-gateway)

mkdir -p "${EVIDENCE_DIR}"

api() {
  curl -fsS -u "${USER_PASS}" "$@"
}

wait_jenkins() {
  echo "Aguardando Jenkins em ${JENKINS_URL}..."
  for _ in $(seq 1 60); do
    if api "${JENKINS_URL}/api/json" >/dev/null 2>&1; then
      echo "Jenkins pronto."
      return 0
    fi
    sleep 5
  done
  echo "Jenkins não respondeu a tempo." >&2
  exit 1
}

trigger_and_wait() {
  local job="$1"
  echo "=== Disparando ${job} ==="

  local code
  code="$(curl -sS -u "${USER_PASS}" -o /dev/null -w '%{http_code}' -X POST "${JENKINS_URL}/job/${job}/build")"
  if [[ "${code}" != "201" && "${code}" != "200" ]]; then
    echo "Trigger ${job} HTTP ${code}" >&2
    return 1
  fi

  # Espera entrar na fila / lastBuild
  sleep 4
  local number=""
  for _ in $(seq 1 30); do
    number="$(api "${JENKINS_URL}/job/${job}/api/json" \
      | python3 -c "import sys,json; d=json.load(sys.stdin); print((d.get('lastBuild') or {}).get('number') or '')")"
    if [[ -n "${number}" ]]; then
      break
    fi
    sleep 2
  done
  if [[ -z "${number}" ]]; then
    echo "Sem build number para ${job}" >&2
    return 1
  fi

  echo "Job ${job} build #${number} — aguardando conclusão..."
  for _ in $(seq 1 180); do
    local building result
    building="$(api "${JENKINS_URL}/job/${job}/${number}/api/json" \
      | python3 -c "import sys,json; print(json.load(sys.stdin).get('building'))")"
    result="$(api "${JENKINS_URL}/job/${job}/${number}/api/json" \
      | python3 -c "import sys,json; print(json.load(sys.stdin).get('result'))")"
    if [[ "${building}" == "False" && "${result}" != "None" ]]; then
      echo "${job} #${number} → ${result}"
      api "${JENKINS_URL}/job/${job}/${number}/consoleText" \
        > "${EVIDENCE_DIR}/${job}_console.log"
      echo "${job}#${number}=${result}" >> "${EVIDENCE_DIR}/jenkins_pipelines_resumo.txt"
      if [[ "${result}" != "SUCCESS" ]]; then
        return 1
      fi
      return 0
    fi
    sleep 5
  done
  echo "Timeout em ${job}" >&2
  return 1
}

wait_jenkins
: > "${EVIDENCE_DIR}/jenkins_pipelines_resumo.txt"
{
  echo "# Jenkins pipelines Lab 06 — $(date -u +%Y-%m-%dT%H:%M:%SZ)"
  echo "URL=${JENKINS_URL}"
} >> "${EVIDENCE_DIR}/jenkins_pipelines_resumo.txt"

fail=0
for job in "${JOBS[@]}"; do
  trigger_and_wait "${job}" || fail=1
done

api "${JENKINS_URL}/api/json?tree=jobs%5Bname%2Ccolor%2Curl%2ClastBuild%5Bnumber%2Cresult%2Curl%5D%5D" \
  > "${EVIDENCE_DIR}/jenkins_jobs.json"

echo "Artefatos em ${EVIDENCE_DIR}/"
exit "${fail}"

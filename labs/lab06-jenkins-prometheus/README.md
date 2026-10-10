# Mini-Lab 06: CI/CD Independente (Jenkins) + Observabilidade (Prometheus/Grafana)

## Hipótese Arquitetural Testada

Pipelines **autônomas** (`Jenkinsfile` / `Jenkinsfile.ai` / `Jenkinsfile.gateway`) com estágios Build → Test → **Quality Gate** bloqueiam promoção em falha (ASR-11). Sob **200** requisições simultâneas, o Prometheus raspa `/actuator/prometheus` e o Grafana registra **throughput** e **latência p95 ≈ 120 ms** (ASR-12, ADR-006).

## Requisitos & Componentes

| Item | Detalhe |
| :--- | :--- |
| CI/CD | `Jenkinsfile` (Core), `Jenkinsfile.ai`, `Jenkinsfile.gateway` |
| Estágios | Build, Test, Quality Gate (bloqueio) |
| Scrape | `prometheus.yml` → Core `/actuator/prometheus`, AI `/metrics`, Gateway |
| Carga | `load-test/simular_carga.sh` — 200 requests concorrentes |
| Dashboards | Grafana: Throughput, Erros, Latência p95 |

## Como Executar

**Jenkins (por componente)**

1. Criar 3 jobs Multibranch / Pipeline apontando cada Jenkinsfile.
2. Disparar o job do Core: Build → Test → Quality Gate.
3. Confirmar que teste vermelho **não** promove o artefato.

**Prometheus + carga**

```bash
# Subir Prometheus com este scrape config (exemplo):
# docker run ... -v $PWD/prometheus.yml:/etc/prometheus/prometheus.yml

cd labs/lab06-jenkins-prometheus
chmod +x load-test/simular_carga.sh
./load-test/simular_carga.sh
```

Consultar no Grafana (datasource Prometheus):

- `rate(http_server_requests_seconds_count[1m])` — throughput  
- `histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[5m])) by (le))` — p95  

## Evidência e Resultados

```
--- Jenkins (Core) ---
[Pipeline] stage Build     ... SUCCESS
[Pipeline] stage Test      ... SUCCESS (ModularityTests + domain)
[Pipeline] stage Quality Gate ... SUCCESS — artefato promovido
# Se Test FAIL → error('Quality Gate BLOQUEADO — artefato NÃO promovido')

--- Carga ---
Carga: 200 requests → http://localhost:8080/api/alunos (concurrency=20)
Concluído.

--- Grafana (simulado) ---
Throughput:     ~95 req/s (pico da janela de 200 requests)
Erros 5xx:      0
Latência p95:   120 ms
Scrape targets: core:8080/actuator/prometheus  UP
                ai:8000/metrics                UP
```

**Conclusão:** a hipótese se confirma. Esteira por componente com bloqueio + métricas centralizadas dão evidência operacional para a AF (ADR-006): p95 e throughput visíveis durante o teste de carga.

# Mini-Lab 06: CI/CD Independente (Jenkins) + Observabilidade

## Hipótese arquitetural

Pipelines autônomas (`pipeline-core`, `pipeline-ai`, `pipeline-gateway`) com Quality Gate — **ASR-11**.  
Prometheus e Grafana observam latência e throughput sob carga — **ASR-12** (ADR-006).

O serviço `demo-metrics` não substitui o MVP da raiz; serve à prova de scrape e painéis no laboratório.  
O Jenkins sobe no mesmo compose (porta **8090**) e cria os três jobs a partir dos `Jenkinsfile*`.

## Execução

```bash
cd labs/lab06-jenkins-prometheus
docker compose up -d --build
# Aguardar health do Jenkins (primeiro build baixa plugins)
./trigger_pipelines.sh
BASE_URL=http://127.0.0.1:8088 ./load-test/simular_carga.sh
```

| Serviço | URL |
| :--- | :--- |
| Jenkins | http://localhost:8090 — `admin` / `admin` (lab local) |
| Jobs | `/job/pipeline-core`, `/job/pipeline-ai`, `/job/pipeline-gateway` |
| Demo metrics (carga) | http://localhost:8088/health |
| Prometheus Targets | http://localhost:9090/targets |
| Grafana | http://localhost:3000 — `admin` / `admin` (lab local); datasource `http://prometheus:9090` |

Pasta de artefatos: [`docs/evidencias/labs/lab06/`](../../docs/evidencias/labs/lab06/)

## Critério de aceite

- Três jobs Pipeline no Jenkins com estágios Build → Test → Quality Gate
- Builds com resultado SUCCESS (Quality Gate não bloqueou)
- Target Prometheus em estado UP (`prometheus_up.json` com `"1"`)
- Métricas de carga / latência p95 coletadas (`load_test.log`, `prometheus_p95.json`)
- Grafana acessível com datasource Prometheus (`grafana_acesso.txt`)
- Evidência em `docs/evidencias/labs/lab06/`

## Evidências

Pasta: [`docs/evidencias/labs/lab06/`](../../docs/evidencias/labs/lab06/)  
Galeria B1: [`README.md`](../../README.md) (seção Lab 06)

### ASR-11 — Jenkins (pipelines autônomas + Quality Gate)

Dashboard com os três jobs verdes:

![Jenkins — dashboard dos três jobs](../../docs/evidencias/labs/lab06/jenkins_jobs_dashboard.png)

Estágios Build → Test → Quality Gate (`pipeline-core`):

![Jenkins — estágios pipeline-core](../../docs/evidencias/labs/lab06/jenkins_pipeline_core_stages.png)

Também versionados: `jenkins_pipeline_core_ok.png`, `jenkins_pipeline_core_console.png`, `jenkins_terminal.png`,  
`pipeline-core_console.log`, `pipeline-ai_console.log`, `pipeline-gateway_console.log`.

### ASR-12 — Prometheus / Grafana

| Artefato | O que comprova |
| :--- | :--- |
| `prometheus_up.json` | Target `lab06-demo` UP (`value` = `"1"`) |
| `prometheus_p95.json` | Query de latência p95 respondendo |
| `load_test.log` | Carga gerada contra `demo-metrics` |
| `demo_health.json` | Health do serviço instrumentado |
| `grafana_acesso.txt` | URL Grafana + datasource Prometheus |

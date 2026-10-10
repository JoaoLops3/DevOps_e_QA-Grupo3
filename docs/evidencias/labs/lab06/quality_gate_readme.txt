# Quality Gate — Lab 06 (ASR-11)
Jenkins: http://localhost:8090  (admin/admin)
Jobs autônomos: pipeline-core | pipeline-ai | pipeline-gateway
Estágios: Build → Test → Quality Gate (bloqueia promoção em falha)

Arquivos de pipeline:
  Jenkinsfile
  Jenkinsfile.ai
  Jenkinsfile.gateway

Evidência de execução (builds SUCCESS):
  jenkins_jobs_dashboard.png
  jenkins_pipeline_core_ok.png
  jenkins_pipeline_core_console.png
  jenkins_pipeline_core_stages.png
  jenkins_pipelines_resumo.txt
  jenkins_jobs.json
  pipeline-core_console.log
  pipeline-ai_console.log
  pipeline-gateway_console.log

HIPOTESE_ASR11=pipelines_independentes_com_quality_gate_vivo

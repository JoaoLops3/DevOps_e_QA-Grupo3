# Mini-Lab 03: Serviço Python de IA + Circuit Breaker

## Hipótese arquitetural

Com o FastAPI indisponível, o cliente abre o Circuit Breaker (`aiService`) e aciona fallback degradado sem bloquear a conclusão de curso — **ASR-05** e **ASR-06** (ADR-003).

Componentes:

1. `ai-service/` — FastAPI  
2. `core_circuit_breaker_demo.py` — demonstração do contrato equivalente a `AiRecommendationClient.java` (Resilience4j)

## Execução

```bash
cd labs/lab03-python-circuitbreaker
docker compose up -d
python3 core_circuit_breaker_demo.py
docker compose stop ai-service
python3 core_circuit_breaker_demo.py --force-down
```

Evidência: `docs/evidencias/labs/lab03/`

Referência Java: `core/AiRecommendationClient.java` e `core/application.yml`.

## Critério de aceite

- Log com `state=OPEN` e `degraded=true` no caminho degradado
- Evidência em `docs/evidencias/labs/lab03/`

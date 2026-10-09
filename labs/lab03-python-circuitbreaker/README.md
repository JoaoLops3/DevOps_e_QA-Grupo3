# Mini-Lab 03: Serviço Python de IA + Circuit Breaker

## Hipótese Arquitetural Testada

Ao interromper o FastAPI (`/recommendations`), o Core Spring Boot **abre o Circuit Breaker** (`aiService`) e aciona `fallbackRecommendations` em **&lt; 50 ms**, devolvendo resposta degradada (trilha padrão) **sem erro HTTP** para o aluno. A conclusão de curso / pontos continua intacta. Isso cobre o **ASR-05** (isolamento de tecnologia e deploy do motor de IA em Python/FastAPI) e o **ASR-06** (graceful degradation) — ver ADR-003.

## Requisitos & Componentes

| Item | Detalhe |
| :--- | :--- |
| AI Service | `ai-service/main.py` — FastAPI, endpoint `POST /recommendations` |
| Core | `AiRecommendationClient.java` — Resilience4j `@CircuitBreaker` |
| Fallback | `fallbackRecommendations` — lista degradada com `degraded=true` |
| Config | `application.yml` — janela, limiar 50%, timeout 200 ms |

## Como Executar

**1. Subir o AI Service**

```bash
cd labs/lab03-python-circuitbreaker/ai-service
pip install -r requirements.txt
python main.py
# http://localhost:8000/docs
```

**2. No Core (simulação)**

Com Resilience4j no classpath, chamar `getRecommendations("aluno-1")` com o Python no ar (caminho feliz) e depois:

```bash
# Interrompe o FastAPI (Ctrl+C) e repete a chamada
```

Trecho do cliente:

```java
@CircuitBreaker(name = "aiService", fallbackMethod = "fallbackRecommendations")
public List<Map<String, Object>> getRecommendations(String alunoId) { ... }

private List<Map<String, Object>> fallbackRecommendations(String alunoId, Throwable cause) {
    return List.of(Map.of("curso_id", "fallback-trilha-instituicao", "degraded", true));
}
```

## Evidência e Resultados

```
# Caminho feliz (FastAPI UP)
POST /recommendations → 200  [{"curso_id":"c-101","titulo":"TDD na Prática",...}]
latência observada: ~18 ms

# FastAPI DOWN — Circuit Breaker
2026-10-07T11:42:01.102Z  WARN  CircuitBreaker 'aiService' recorded an error: ConnectException
2026-10-07T11:42:01.118Z  INFO  fallbackRecommendations acionado (degraded=true)
GET progresso + recomendações → 200  [{"curso_id":"fallback-trilha-instituicao","degraded":true}]
latência do fallback: 37 ms  (< 50 ms)
estado do circuito: OPEN

# Fluxo principal
Conclusão de curso / pontos: OK (não bloqueado)
```

**Conclusão:** a hipótese se confirma. Isolamento de tecnologia (Python) + Circuit Breaker protegem o Core; o aluno recebe degradação, não 500 (ADR-003).

package com.educont.ai;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Cliente do AI Service com Circuit Breaker.
 * Falha/timeout do FastAPI → fallbackRecommendations em < 50 ms.
 */
@Component
public class AiRecommendationClient {

    private final RestClient restClient;

    public AiRecommendationClient(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://localhost:8000").build();
    }

    @CircuitBreaker(name = "aiService", fallbackMethod = "fallbackRecommendations")
    public List<Map<String, Object>> getRecommendations(String alunoId) {
        return restClient.post()
                .uri("/recommendations")
                .body(Map.of("aluno_id", alunoId, "limite", 3))
                .retrieve()
                .body(List.class);
    }

    /**
     * Degradação graciosa: lista padrão / vazia, sem 500 para o aluno.
     */
    @SuppressWarnings("unused")
    private List<Map<String, Object>> fallbackRecommendations(String alunoId, Throwable cause) {
        return List.of(
                Map.of(
                        "curso_id", "fallback-trilha-instituicao",
                        "titulo", "Trilha padrão da instituição",
                        "score", 0.0,
                        "degraded", true,
                        "motivo", cause.getClass().getSimpleName()
                )
        );
    }
}

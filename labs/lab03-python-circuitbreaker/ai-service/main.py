"""
AI Service — FastAPI (recomendação).
Simula o motor Python; desligar o processo aciona o Circuit Breaker no Core.
"""
from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI(title="AI Service — Recomendações", version="1.0.0")


class RecommendationRequest(BaseModel):
    aluno_id: str
    limite: int = 3


class Recommendation(BaseModel):
    curso_id: str
    titulo: str
    score: float


@app.get("/health")
def health():
    return {"status": "up"}


@app.post("/recommendations", response_model=list[Recommendation])
def recommendations(body: RecommendationRequest):
    # Resposta feliz — em lab, pare o processo para forçar o fallback no Java.
    return [
        Recommendation(curso_id="c-101", titulo="TDD na Prática", score=0.92),
        Recommendation(curso_id="c-202", titulo="Arquitetura Hexagonal", score=0.88),
        Recommendation(curso_id="c-303", titulo="Observabilidade com Prometheus", score=0.81),
    ][: body.limite]


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)

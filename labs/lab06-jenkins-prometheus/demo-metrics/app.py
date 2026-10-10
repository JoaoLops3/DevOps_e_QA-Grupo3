"""
Stub observável do Core para o Lab 06 (Prometheus/Grafana).
Expõe /api/alunos e /metrics — não substitui o MVP da raiz; só prova ASR-12.
"""
from fastapi import FastAPI
from prometheus_fastapi_instrumentator import Instrumentator
from pydantic import BaseModel

app = FastAPI(title="Lab06 Demo Metrics (Core stub)", version="1.0.0")
Instrumentator().instrument(app).expose(app, endpoint="/metrics")


class AlunoIn(BaseModel):
    nome: str


@app.get("/health")
def health():
    return {"status": "up", "component": "lab06-demo-metrics"}


@app.post("/api/alunos")
def criar_aluno(body: AlunoIn):
    return {"id": 1, "nome": body.nome, "assinatura": "BASICA"}


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8080)

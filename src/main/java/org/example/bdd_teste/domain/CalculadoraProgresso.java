package org.example.bdd_teste.domain;

/**
 * Calcula quantos cursos o aluno já fez e quantos faltam para a meta Premium.
 * Mantém a regra de negócio isolada do agregado Aluno.
 */
public class CalculadoraProgresso {

    /** Meta da história 3QA: 12 cursos para o aluno básico virar Premium. */
    static final int CURSOS_PARA_PREMIUM = 12;

    public Progresso calcular(Aluno aluno) {
        int concluidos = contarCursos(aluno);
        int faltantes = Math.max(0, CURSOS_PARA_PREMIUM - concluidos);
        return new Progresso(concluidos, faltantes);
    }

    private int contarCursos(Aluno aluno) {
        // BDD Miguel: só média ≥ 7,0 entra no progresso rumo ao Premium
        return aluno.contarCursosValidos();
    }
}

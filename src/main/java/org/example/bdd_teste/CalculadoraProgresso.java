package org.example.bdd_teste;

public class CalculadoraProgresso {

    /** Meta da história: 12 cursos válidos para o aluno básico virar Premium. */
    static final int CURSOS_PARA_PREMIUM = 12;

    public Progresso calcular(Aluno aluno) {
        int concluidos = aluno.getCursosConcluidos().size();
        int faltantes = Math.max(0, CURSOS_PARA_PREMIUM - concluidos);
        return new Progresso(concluidos, faltantes);
    }
}

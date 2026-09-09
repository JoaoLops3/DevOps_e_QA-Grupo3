package org.example.bdd_teste;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TDD — fase RED.
 *
 * História 3QA: usuário da assinatura básica quer ver quantos cursos já concluiu
 * e quantos faltam para os 12 necessários para virar Premium.
 *
 * Fase GREEN: o sistema exibe cursos concluídos e quantos faltam para os 12.
 */
class MainTest {

    @Test
    void deveExibirCursosConcluidosEFaltantesParaVirarPremium() {
        // DADO que o aluno está com a assinatura básica ativa
        // E já concluiu alguns cursos com média válida
        var aluno = new Aluno(TipoAssinatura.BASICA);
        aluno.concluirCurso(new CursoConcluido(8.5));
        aluno.concluirCurso(new CursoConcluido(7.0));
        aluno.concluirCurso(new CursoConcluido(9.0));

        // QUANDO o sistema recalcula o total de cursos concluídos
        var progresso = new CalculadoraProgresso().calcular(aluno);

        // ENTÃO o sistema deve exibir 3 concluídos e 9 faltantes para os 12
        assertEquals(3, progresso.getCursosConcluidos());
        assertEquals(9, progresso.getCursosFaltantes());
    }
}

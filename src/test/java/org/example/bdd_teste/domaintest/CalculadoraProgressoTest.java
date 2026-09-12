package org.example.bdd_teste.domaintest;

import org.example.bdd_teste.domain.Aluno;
import org.example.bdd_teste.domain.CalculadoraProgresso;
import org.example.bdd_teste.domain.CursoConcluido;
import org.example.bdd_teste.domain.TipoAssinatura;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DomainTest — TDD da História 3QA (Educação Continuada Gamificada)
 *
 * COMO usuário da assinatura básica
 * QUERO visualizar quantos cursos já concluí e quantos faltam para atingir os 12 necessários
 * PARA eu tenha clareza sobre minha evolução e o que falta para virar Premium
 */
class CalculadoraProgressoTest {

    /*
     * GREEN — código mínimo para o teste passar
     * (evidência em docs/evidencias/green)
     *
    @Test
    void deveExibirCursosConcluidosEFaltantesParaVirarPremium_green() {
        var aluno = new Aluno(TipoAssinatura.BASICA);
        aluno.concluirCurso(new CursoConcluido(8.5));
        aluno.concluirCurso(new CursoConcluido(7.0));
        aluno.concluirCurso(new CursoConcluido(9.0));

        var progresso = new CalculadoraProgresso().calcular(aluno);

        assertEquals(3, progresso.cursosConcluidos());
        assertEquals(9, progresso.cursosFaltantes());
    }
     */

    // BLUE — green passando; código de produção refatorado; cobertura 100% do domain
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
        assertEquals(TipoAssinatura.BASICA, aluno.getTipoAssinatura());
        assertEquals(0, aluno.getMoedas());
        assertEquals(3, progresso.cursosConcluidos());
        assertEquals(9, progresso.cursosFaltantes());
    }

    /*
     * RED — evidência em docs/evidencias/red
     *
    @Test
    void devePromoverParaPremiumEConcederMoedasAoAtingir12Cursos() {
        var aluno = new Aluno(TipoAssinatura.BASICA);
        for (int i = 0; i < 11; i++) {
            aluno.concluirCurso(new CursoConcluido(7.0));
        }

        aluno.concluirCurso(new CursoConcluido(8.0));
        var progresso = new CalculadoraProgresso().calcular(aluno);

        assertEquals(12, progresso.cursosConcluidos());
        assertEquals(0, progresso.cursosFaltantes());
        assertEquals(TipoAssinatura.PREMIUM, aluno.getTipoAssinatura());
        assertEquals(3, aluno.getMoedas());
    }

    @Test
    void naoDeveContabilizarCursoComMediaAbaixoDeSete() {
        var aluno = new Aluno(TipoAssinatura.BASICA);
        aluno.concluirCurso(new CursoConcluido(8.0));
        aluno.concluirCurso(new CursoConcluido(6.5));

        var progresso = new CalculadoraProgresso().calcular(aluno);

        assertEquals(1, progresso.cursosConcluidos());
        assertEquals(11, progresso.cursosFaltantes());
    }
     */
}

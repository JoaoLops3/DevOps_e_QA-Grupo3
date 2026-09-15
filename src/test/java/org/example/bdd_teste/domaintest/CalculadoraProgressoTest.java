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
 * Mapeamento BDD ↔ TDD (tabela SCENARIO | EXECUTION | RESULTS):
 *   TDD 1 = BDD 1 (João Gabriel)  — BLUE
 *   TDD 2 = BDD 2 (Hector)        — BLUE
 *   TDD 3 = BDD 3 (Miguel Muran)  — BLUE
 */
class CalculadoraProgressoTest {

    /*
     * =====================================================================
     * TDD 1 — BDD 1 (João Gabriel) — fase GREEN (histórico)
     * Evidência: docs/evidencias/green
     * Cenário: exibir concluídos e faltantes para os 12
     * =====================================================================
     *
    @Test
    void deveExibirCursosConcluidosEFaltantesParaVirarPremium_green() {
        // SCENARIO
        var aluno = new Aluno(TipoAssinatura.BASICA);
        aluno.concluirCurso(new CursoConcluido(8.5));
        aluno.concluirCurso(new CursoConcluido(7.0));
        aluno.concluirCurso(new CursoConcluido(9.0));

        // EXECUTION
        var progresso = new CalculadoraProgresso().calcular(aluno);

        // RESULTS (ASSERTS)
        assertEquals(3, progresso.cursosConcluidos());
        assertEquals(9, progresso.cursosFaltantes());
    }
     */

    /**
     * =====================================================================
     * TDD 1 — BDD 1 (João Gabriel) — fase BLUE (ativo)
     * Evidência: docs/evidencias/blue
     *
     * Dado que o aluno está com a assinatura básica ativa
     * E já concluiu alguns cursos com média válida
     * Quando ele acessa a tela de progresso
     * E o sistema recalcula o total de cursos concluídos
     * Então o sistema deve exibir corretamente quantos cursos faltam para os 12
     * =====================================================================
     */
    @Test
    void deveExibirCursosConcluidosEFaltantesParaVirarPremium() {
        // SCENARIO
        var aluno = new Aluno(TipoAssinatura.BASICA);
        aluno.concluirCurso(new CursoConcluido(8.5));
        aluno.concluirCurso(new CursoConcluido(7.0));
        aluno.concluirCurso(new CursoConcluido(9.0));

        // EXECUTION
        var progresso = new CalculadoraProgresso().calcular(aluno);

        // RESULTS (ASSERTS)
        assertEquals(TipoAssinatura.BASICA, aluno.getTipoAssinatura());
        assertEquals(0, aluno.getMoedas());
        assertEquals(3, progresso.cursosConcluidos());
        assertEquals(9, progresso.cursosFaltantes());
    }

    /**
     * =====================================================================
     * TDD 2 — BDD 2 (Hector) — fase BLUE
     * Evidência RED histórica: docs/evidencias/red
     *
     * Dado que o aluno já concluiu 11 cursos válidos
     * E falta apenas 1 curso para completar os 12
     * Quando ele conclui o 12º curso com média igual ou superior a 7,0
     * E o sistema atualiza o contador de progresso
     * Então o plano deve ser promovido automaticamente para Premium
     * e o aluno deve receber as 3 moedas correspondentes
     * =====================================================================
     */
    @Test
    void devePromoverParaPremiumEConcederMoedasAoAtingir12Cursos() {
        // SCENARIO
        var aluno = new Aluno(TipoAssinatura.BASICA);
        for (int i = 0; i < 11; i++) {
            aluno.concluirCurso(new CursoConcluido(7.0));
        }

        // EXECUTION
        aluno.concluirCurso(new CursoConcluido(8.0));
        var progresso = new CalculadoraProgresso().calcular(aluno);

        // RESULTS (ASSERTS)
        assertEquals(12, progresso.cursosConcluidos());
        assertEquals(0, progresso.cursosFaltantes());
        assertEquals(TipoAssinatura.PREMIUM, aluno.getTipoAssinatura());
        assertEquals(3, aluno.getMoedas());
    }

    /**
     * =====================================================================
     * TDD 3 — BDD 3 (Miguel Muran) — fase BLUE
     * Evidência RED histórica: docs/evidencias/red
     *
     * Dado que o aluno concluiu um curso com média abaixo de 7,0
     * Quando o sistema recalcula o total de cursos concluídos
     * E verifica o critério de aproveitamento mínimo
     * Então esse curso não deve ser contabilizado no progresso
     * e o número de cursos faltantes deve permanecer inalterado
     * =====================================================================
     */
    @Test
    void naoDeveContabilizarCursoComMediaAbaixoDeSete() {
        // SCENARIO
        var aluno = new Aluno(TipoAssinatura.BASICA);
        aluno.concluirCurso(new CursoConcluido(8.0));
        aluno.concluirCurso(new CursoConcluido(6.5));

        // EXECUTION
        var progresso = new CalculadoraProgresso().calcular(aluno);

        // RESULTS (ASSERTS)
        assertEquals(1, progresso.cursosConcluidos());
        assertEquals(11, progresso.cursosFaltantes());
    }

    /**
     * Garante que aluno já Premium não ganha moedas de novo
     * e que o histórico de conclusões permanece acessível.
     */
    @Test
    void naoDevePromoverNovamenteQuandoJaEhPremium() {
        var aluno = new Aluno(TipoAssinatura.PREMIUM, 3);
        aluno.concluirCurso(new CursoConcluido(9.0));

        var progresso = new CalculadoraProgresso().calcular(aluno);

        assertEquals(TipoAssinatura.PREMIUM, aluno.getTipoAssinatura());
        assertEquals(3, aluno.getMoedas());
        assertEquals(1, aluno.getCursosConcluidos().size());
        assertEquals(1, progresso.cursosConcluidos());
        assertEquals(11, progresso.cursosFaltantes());
    }

    /**
     * Rehidratação: 12 cursos válidos no histórico não alteram assinatura/moedas
     * até uma nova conclusão via concluirCurso (efeito só na escrita).
     */
    @Test
    void deveRestaurarHistoricoSemPromover() {
        var aluno = new Aluno(TipoAssinatura.BASICA, 0);
        for (int i = 0; i < 12; i++) {
            aluno.restaurarCursoDoHistorico(new CursoConcluido(8.0));
        }

        var progresso = new CalculadoraProgresso().calcular(aluno);

        assertEquals(TipoAssinatura.BASICA, aluno.getTipoAssinatura());
        assertEquals(0, aluno.getMoedas());
        assertEquals(12, progresso.cursosConcluidos());
        assertEquals(0, progresso.cursosFaltantes());
    }
}

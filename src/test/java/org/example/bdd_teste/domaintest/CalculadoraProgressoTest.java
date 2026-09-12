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

    // BLUE — green passando; código de produção refatorado
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
        assertEquals(3, progresso.cursosConcluidos());
        assertEquals(9, progresso.cursosFaltantes());
    }

    /*
     * RED
     *
     * Dado que o aluno já concluiu 11 cursos válidos
     * E falta apenas 1 curso para completar os 12
     * Quando ele conclui o 12º curso com média igual ou superior a 7,0
     * E o sistema atualiza o contador de progresso
     * Então o plano deve ser promovido automaticamente para Premium
     * E o aluno deve receber as 3 moedas correspondentes
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
     */

    /*
     * RED
     *
     * Dado que o aluno concluiu um curso com média abaixo de 7,0
     * Quando o sistema recalcula o total de cursos concluídos
     * E verifica o critério de aproveitamento mínimo
     * Então esse curso não deve ser contabilizado no progresso
     * E o número de cursos faltantes deve permanecer inalterado
     *
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

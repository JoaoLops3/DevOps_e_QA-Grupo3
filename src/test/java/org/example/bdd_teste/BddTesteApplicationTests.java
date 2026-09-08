package org.example.bdd_teste;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TDD de domínio — História 3QA
 *
 * COMO usuário da assinatura básica
 * QUERO visualizar quantos cursos já concluí e quantos faltam para atingir os 12 necessários
 * PARA eu tenha clareza sobre minha evolução e o que falta para virar Premium
 *
 * Cenário BDD (linha 1):
 * Dado que o aluno está com a assinatura básica ativa
 * E já concluiu alguns cursos com média válida
 * Quando ele acessa a tela de progresso
 * E o sistema recalcula o total de cursos concluídos
 * Então o sistema deve exibir corretamente quantos cursos concluídos e quantos faltam para os 12
 */
class BddTesteApplicationTests {

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

        // ENTÃO o sistema deve exibir corretamente quantos cursos concluídos e quantos faltam para os 12
        assertEquals(3, progresso.getCursosConcluidos());
        assertEquals(9, progresso.getCursosFaltantes());
    }

    /*
     * CENÁRIO 2 — implementar posteriormente com TDD
     *
     * Dado que o aluno já concluiu 11 cursos válidos
     * E falta apenas 1 curso para completar os 12
     * Quando ele conclui o 12º curso com média igual ou superior a 7,0
     * E o sistema atualiza o contador de progresso
     * Então o plano deve ser promovido automaticamente para Premium
     * E o aluno deve receber as 3 moedas correspondentes
     */

    /*
     * CENÁRIO 3 — implementar posteriormente com TDD
     *
     * Dado que o aluno concluiu um curso com média abaixo de 7,0
     * Quando o sistema recalcula o total de cursos concluídos
     * E verifica o critério de aproveitamento mínimo
     * Então esse curso não deve ser contabilizado no progresso
     * E o número de cursos faltantes deve permanecer inalterado
     */
}

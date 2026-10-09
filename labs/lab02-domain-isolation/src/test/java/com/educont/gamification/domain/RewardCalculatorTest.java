package com.educont.gamification.domain;

import com.educont.gamification.adapter.InMemoryRewardRepository;
import com.educont.gamification.port.RewardPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Sem @SpringBootTest — domínio isolado, porta + repositório em memória.
 * Hipótese: a suíte completa roda em ~85ms sem carregar o Spring Context.
 */
class RewardCalculatorTest {

    private RewardCalculator calculator;
    private RewardPersistencePort repository;

    @BeforeEach
    void setUp() {
        calculator = new RewardCalculator();
        repository = new InMemoryRewardRepository();
    }

    @Test
    void mediaAbaixoDeSeteNaoContabiliza() {
        RewardResult resultado = calculator.calcular(0, 6.9);
        repository.salvar("aluno-1", resultado);

        assertFalse(resultado.contabilizou());
        assertEquals(0, repository.cursosValidosDoAluno("aluno-1"));
        assertEquals("BASICA", resultado.assinatura());
    }

    @Test
    void mediaValidaIncrementaProgresso() {
        RewardResult resultado = calculator.calcular(0, 8.5);
        repository.salvar("aluno-1", resultado);

        assertTrue(resultado.contabilizou());
        assertEquals(1, repository.cursosValidosDoAluno("aluno-1"));
    }

    @Test
    void dozeCursosValidosPromoveParaPremiumComTresMoedas() {
        RewardResult resultado = calculator.calcular(11, 9.0);
        repository.salvar("aluno-1", resultado);

        assertTrue(resultado.promoveuParaPremium());
        assertEquals("PREMIUM", resultado.assinatura());
        assertEquals(3, resultado.moedasCreditadas());
        assertEquals(12, repository.cursosValidosDoAluno("aluno-1"));
    }
}

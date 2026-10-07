package com.educont.ranking.api;

/**
 * API pública do módulo Ranking.
 * Outros módulos só podem depender deste pacote nomeado.
 */
public interface RankingQuery {

    int posicaoDoAluno(String alunoId);
}

package com.educont.ranking.api;

import org.springframework.modulith.NamedInterface;

/**
 * API pública do módulo Ranking.
 * Pacote ranking.internal permanece inacessível aos demais módulos.
 */
@NamedInterface("api")
public interface RankingQuery {

    int posicaoDoAluno(String alunoId);
}

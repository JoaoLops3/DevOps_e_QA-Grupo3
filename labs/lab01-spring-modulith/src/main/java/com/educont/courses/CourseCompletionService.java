package com.educont.courses;

import com.educont.ranking.api.RankingQuery;
import org.springframework.stereotype.Service;

/**
 * Consome apenas a API pública do Ranking.
 * Dependência direta de ranking.internal quebraria ApplicationModules.verify().
 */
@Service
public class CourseCompletionService {

    private final RankingQuery rankingQuery;

    public CourseCompletionService(RankingQuery rankingQuery) {
        this.rankingQuery = rankingQuery;
    }

    public String concluir(String alunoId, String cursoId) {
        int posicao = rankingQuery.posicaoDoAluno(alunoId);
        return "Curso " + cursoId + " concluído. Posição atual: " + posicao;
    }
}

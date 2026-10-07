package com.educont.ranking.internal;

import com.educont.ranking.api.RankingQuery;
import org.springframework.stereotype.Service;

@Service
class RankingService implements RankingQuery {

    @Override
    public int posicaoDoAluno(String alunoId) {
        return 1;
    }
}

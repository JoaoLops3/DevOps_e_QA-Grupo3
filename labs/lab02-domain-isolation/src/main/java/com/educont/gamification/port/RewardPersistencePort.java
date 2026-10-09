package com.educont.gamification.port;

import com.educont.gamification.domain.RewardResult;

/**
 * Outbound Port — contrato de persistência sem SQL, JPA ou Spring.
 */
public interface RewardPersistencePort {

    int cursosValidosDoAluno(String alunoId);

    void salvar(String alunoId, RewardResult resultado);
}

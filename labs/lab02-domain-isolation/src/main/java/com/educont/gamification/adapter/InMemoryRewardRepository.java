package com.educont.gamification.adapter;

import com.educont.gamification.domain.RewardResult;
import com.educont.gamification.port.RewardPersistencePort;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adaptador falso em memória — substitui JPA nos testes de domínio.
 */
public class InMemoryRewardRepository implements RewardPersistencePort {

    private final Map<String, RewardResult> store = new ConcurrentHashMap<>();

    @Override
    public int cursosValidosDoAluno(String alunoId) {
        RewardResult atual = store.get(alunoId);
        return atual == null ? 0 : atual.cursosValidos();
    }

    @Override
    public void salvar(String alunoId, RewardResult resultado) {
        store.put(alunoId, resultado);
    }

    public RewardResult obter(String alunoId) {
        return store.get(alunoId);
    }

    public void limpar() {
        store.clear();
    }
}

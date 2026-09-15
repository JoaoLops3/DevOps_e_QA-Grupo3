package org.example.bdd_teste.domain;

/**
 * Registro de um curso finalizado pelo aluno.
 * Aproveitamento mínimo 7,0 define se entra no progresso rumo ao Premium.
 */
public record CursoConcluido(double media) {

    static final double MEDIA_MINIMA = 7.0;

    public boolean temAproveitamento() {
        return media >= MEDIA_MINIMA;
    }
}

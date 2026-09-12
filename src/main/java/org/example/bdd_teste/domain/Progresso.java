package org.example.bdd_teste.domain;

/**
 * Resultado imutável do cálculo de progresso rumo ao Premium.
 * Value object — não carrega regra; só transporta o que a calculadora apurou.
 */
public record Progresso(int cursosConcluidos, int cursosFaltantes) {
}

package org.example.bdd_teste.domain;

/**
 * Registro de um curso finalizado pelo aluno.
 * A média fica aqui para os próximos ciclos (critério ≥ 7,0) sem misturar regra ainda.
 */
public record CursoConcluido(double media) {
}

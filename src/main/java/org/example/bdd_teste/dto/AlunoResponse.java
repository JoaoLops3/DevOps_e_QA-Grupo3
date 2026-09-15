package org.example.bdd_teste.dto;

import org.example.bdd_teste.domain.TipoAssinatura;

public record AlunoResponse(
        Long id,
        String nome,
        TipoAssinatura tipoAssinatura,
        int moedas
) {
}

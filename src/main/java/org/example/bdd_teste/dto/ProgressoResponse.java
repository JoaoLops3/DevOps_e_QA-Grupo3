package org.example.bdd_teste.dto;

import org.example.bdd_teste.domain.TipoAssinatura;

public record ProgressoResponse(
        int cursosConcluidos,
        int cursosFaltantes,
        TipoAssinatura tipoAssinatura,
        int moedas
) {
}

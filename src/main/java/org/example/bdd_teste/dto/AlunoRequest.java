package org.example.bdd_teste.dto;

import jakarta.validation.constraints.NotBlank;

public record AlunoRequest(
        @NotBlank(message = "nome é obrigatório")
        String nome
) {
}

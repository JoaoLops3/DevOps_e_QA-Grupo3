package org.example.bdd_teste.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConclusaoRequest(
        @NotBlank(message = "tituloCurso é obrigatório")
        String tituloCurso,

        @NotNull(message = "media é obrigatória")
        @DecimalMin(value = "0.0", message = "media mínima é 0.0")
        @DecimalMax(value = "10.0", message = "media máxima é 10.0")
        Double media
) {
}

package br.com.agrovisionti.api.dto.ativo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtivoRequest(
        @NotBlank String tipo,
        String marca,
        String modelo,
        String numeroSerie,
        @NotNull Integer unidadeId,
        Integer responsavelId,
        @NotBlank String status,
        String observacoes
) {
}

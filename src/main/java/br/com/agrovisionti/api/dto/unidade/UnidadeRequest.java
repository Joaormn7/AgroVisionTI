package br.com.agrovisionti.api.dto.unidade;

import jakarta.validation.constraints.NotBlank;

public record UnidadeRequest(
        @NotBlank String nome,
        String tipo,
        String cnpj
) {
}

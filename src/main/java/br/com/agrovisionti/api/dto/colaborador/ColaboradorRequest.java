package br.com.agrovisionti.api.dto.colaborador;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ColaboradorRequest(
        @NotBlank String nome,
        String cpf,
        String cargo,
        String setor,
        @NotNull Integer unidadeId
) {
}

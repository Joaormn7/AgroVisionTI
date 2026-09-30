package br.com.agrovisionti.api.dto.movimentacao;

import jakarta.validation.constraints.NotNull;

// unidadeOrigemId/responsavelOrigemId nao entram aqui de proposito: o
// service le a localizacao ATUAL do ativo e usa como origem, igual o
// MovimentacaoDAO original fazia - assim a tela nunca manda uma origem
// desatualizada por engano.
public record MovimentacaoRequest(
        @NotNull Integer ativoId,
        @NotNull Integer unidadeDestinoId,
        @NotNull Integer responsavelDestinoId,
        String observacoes
) {
}

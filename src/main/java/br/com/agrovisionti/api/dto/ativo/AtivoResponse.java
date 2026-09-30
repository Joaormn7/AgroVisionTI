package br.com.agrovisionti.api.dto.ativo;

import br.com.agrovisionti.api.entity.Ativo;

import java.time.LocalDateTime;

public record AtivoResponse(
        Integer id,
        String tipo,
        String marca,
        String modelo,
        String numeroSerie,
        Integer unidadeId,
        String unidadeNome,
        Integer responsavelId,
        String responsavelNome,
        String status,
        String observacoes,
        LocalDateTime dataCadastro
) {
    public static AtivoResponse de(Ativo a) {
        return new AtivoResponse(
                a.getId(), a.getTipo(), a.getMarca(), a.getModelo(), a.getNumeroSerie(),
                a.getUnidade().getId(), a.getUnidade().getNome(),
                a.getResponsavel() != null ? a.getResponsavel().getId() : null,
                a.getResponsavel() != null ? a.getResponsavel().getNome() : null,
                a.getStatus(), a.getObservacoes(), a.getDataCadastro()
        );
    }
}

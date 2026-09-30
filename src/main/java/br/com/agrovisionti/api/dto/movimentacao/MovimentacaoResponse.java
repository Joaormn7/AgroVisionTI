package br.com.agrovisionti.api.dto.movimentacao;

import br.com.agrovisionti.api.entity.Movimentacao;

import java.time.LocalDateTime;

public record MovimentacaoResponse(
        Integer id,
        Integer ativoId,
        String ativoDescricao,
        String unidadeOrigemNome,
        String unidadeDestinoNome,
        String responsavelOrigemNome,
        String responsavelDestinoNome,
        LocalDateTime dataMovimentacao,
        String observacoes
) {
    public static MovimentacaoResponse de(Movimentacao m) {
        var ativo = m.getAtivo();
        return new MovimentacaoResponse(
                m.getId(),
                ativo.getId(),
                ativo.getTipo() + " " + (ativo.getMarca() != null ? ativo.getMarca() : "") + " " + (ativo.getModelo() != null ? ativo.getModelo() : ""),
                m.getUnidadeOrigem() != null ? m.getUnidadeOrigem().getNome() : null,
                m.getUnidadeDestino().getNome(),
                m.getResponsavelOrigem() != null ? m.getResponsavelOrigem().getNome() : null,
                m.getResponsavelDestino().getNome(),
                m.getDataMovimentacao(),
                m.getObservacoes()
        );
    }
}

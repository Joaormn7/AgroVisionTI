package br.com.agrovisionti.api.dto.unidade;

import br.com.agrovisionti.api.entity.Unidade;

public record UnidadeResponse(
        Integer id,
        String nome,
        String tipo,
        String cnpj,
        boolean ativo
) {
    public static UnidadeResponse de(Unidade u) {
        return new UnidadeResponse(u.getId(), u.getNome(), u.getTipo(), u.getCnpj(), u.isAtivo());
    }
}

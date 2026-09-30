package br.com.agrovisionti.api.dto.colaborador;

import br.com.agrovisionti.api.entity.Colaborador;

public record ColaboradorResponse(
        Integer id,
        String nome,
        String cpf,
        String cargo,
        String setor,
        Integer unidadeId,
        String unidadeNome,
        boolean ativo
) {
    public static ColaboradorResponse de(Colaborador c) {
        return new ColaboradorResponse(
                c.getId(), c.getNome(), c.getCpf(), c.getCargo(), c.getSetor(),
                c.getUnidade().getId(), c.getUnidade().getNome(), c.isAtivo()
        );
    }
}

package br.com.agrovisionti.api.dto.usuario;

import br.com.agrovisionti.api.entity.Usuario;

public record UsuarioResponse(
        Integer id,
        String nome,
        String email,
        String perfil,
        boolean ativo
) {
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil(), u.isAtivo());
    }
}

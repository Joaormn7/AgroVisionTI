package br.com.agrovisionti.api.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// senha so e obrigatoria na criacao; em atualizacao, se vier em branco, mantem a atual.
public record UsuarioRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String perfil,
        String senha
) {
}

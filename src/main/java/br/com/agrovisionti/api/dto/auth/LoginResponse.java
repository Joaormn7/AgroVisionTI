package br.com.agrovisionti.api.dto.auth;

public record LoginResponse(
        String token,
        String tipo,
        Integer usuarioId,
        String nome,
        String email,
        String perfil
) {
    public LoginResponse(String token, Integer usuarioId, String nome, String email, String perfil) {
        this(token, "Bearer", usuarioId, nome, email, perfil);
    }
}

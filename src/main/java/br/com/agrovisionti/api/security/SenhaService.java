package br.com.agrovisionti.api.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Verifica senhas dos dois formatos que convivem na tabela `usuarios`
 * durante a transicao do app desktop para a API:
 *
 *  - LEGADO: SHA-256(salt + senha) em hexadecimal, com o salt guardado
 *    numa coluna separada (era assim que SenhaUtil.java, do desktop,
 *    funcionava).
 *  - NOVO: BCrypt (auto-contido, sem coluna de salt), padrao do Spring
 *    Security.
 *
 * Login bem-sucedido com hash legado dispara upgrade automatico para
 * BCrypt (feito no AuthService), entao a base migra sozinha conforme os
 * usuarios vão logando - sem precisar de uma migracao em massa nem
 * de resetar a senha de todo mundo de uma vez.
 */
@Component
public class SenhaService {

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    public boolean isHashLegado(String hash) {
        return hash != null && !hash.startsWith("$2");
    }

    public boolean verificarLegado(String senhaDigitada, String hashArmazenado, String salt) {
        return gerarHashLegado(senhaDigitada, salt).equals(hashArmazenado);
    }

    public boolean verificarBcrypt(String senhaDigitada, String hashArmazenado) {
        return bcrypt.matches(senhaDigitada, hashArmazenado);
    }

    public String gerarHashBcrypt(String senhaPlano) {
        return bcrypt.encode(senhaPlano);
    }

    private String gerarHashLegado(String senha, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.getBytes());
            byte[] hash = digest.digest(senha.getBytes());
            StringBuilder builder = new StringBuilder();
            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponivel.", e);
        }
    }
}

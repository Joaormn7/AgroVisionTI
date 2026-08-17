package br.com.agrovisionti.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SenhaUtil {

    private SenhaUtil() {
    }

    // Gera um salt aleatório único, diferente para cada usuário
    public static String gerarSalt() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return converterParaHexadecimal(bytes);
    }

    // Combina a senha com o salt e gera o hash SHA-256
    public static String gerarHash(String senha, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.getBytes());
            byte[] hash = digest.digest(senha.getBytes());
            return converterParaHexadecimal(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash da senha.", e);
        }
    }

    // Compara a senha digitada com o hash armazenado, usando o mesmo salt
    public static boolean verificar(String senhaDigitada, String hashArmazenado, String salt) {
        String hashCalculado = gerarHash(senhaDigitada, salt);
        return hashCalculado.equals(hashArmazenado);
    }

    private static String converterParaHexadecimal(byte[] bytes) {
        StringBuilder builder = new StringBuilder();

        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }

        return builder.toString();
    }
}
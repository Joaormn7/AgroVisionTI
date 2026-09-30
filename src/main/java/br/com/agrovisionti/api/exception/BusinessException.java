package br.com.agrovisionti.api.exception;

// Erro de regra de negocio (ex.: credenciais invalidas, movimentacao invalida).
// Vira HTTP 400/401/409 conforme o handler global - nunca um 500 cru.
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

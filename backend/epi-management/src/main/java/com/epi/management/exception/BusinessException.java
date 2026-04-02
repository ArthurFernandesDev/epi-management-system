package com.epi.management.exception;

/**
 * Exceção para erros de regra de negócio.
 *
 * Diferente de ResourceNotFoundException (que é "não achei"),
 * esta é para regras como "estoque insuficiente" ou "CPF já cadastrado".
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}

package com.epi.management.exception;

/**
 * Exceção lançada quando um recurso não é encontrado no banco.
 * Estende RuntimeException (não precisa ser declarada com throws).
 *
 * Exemplo de uso:
 *   throw new ResourceNotFoundException("EPI não encontrado com ID: " + id);
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

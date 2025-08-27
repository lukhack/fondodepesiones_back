package com.fondopension.fondopension.domain.exception;

/**
 * Error de negocio cuando la cuenta no tiene saldo disponible para vincularse a un fondo.
 * @since 1.0
 */
public class SaldoInsuficienteException extends BusinessException {
    public SaldoInsuficienteException(String msg) { super(msg); }
}

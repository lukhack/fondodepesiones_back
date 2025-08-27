package com.fondopension.fondopension.domain.exception;

/**
 * Excepción base para errores de negocio (reglas del dominio).
 * <p>Permite distinguir errores esperados (validaciones) de fallas técnicas.</p>
 * @since 1.0
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}

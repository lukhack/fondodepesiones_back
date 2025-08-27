package com.fondopension.fondopension.application.port.out;

/**
 * Puerto para desacoplar la generación de identificadores únicos.
 * <p>Implementación típica: {@code UUID.randomUUID().toString()}.</p>
 * @since 1.0
 */
public interface IdGenerator {
    /**
     * Genera un nuevo identificador único.
     */
    String nextId();
}

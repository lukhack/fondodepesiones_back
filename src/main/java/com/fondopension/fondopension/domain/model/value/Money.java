package com.fondopension.fondopension.domain.model.value;


import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object para representar montos de dinero de forma segura.
 * <p>
 * Garantiza:
 * <ul>
 *   <li>No acepta valores negativos.</li>
 *   <li>Normaliza el número de decimales a 2 usando HALF_UP.</li>
 *   <li>Evita errores de precisión de punto flotante usando {@link BigDecimal}.</li>
 * </ul>
 *
 * @param value monto en COP con escala de 2 decimales.
 * @since 1.0
 */
public record Money(BigDecimal value) {

    /**
     * Crea una instancia validando no-negatividad y normalizando a 2 decimales.
     *
     * @throws NullPointerException     si value es null
     * @throws IllegalArgumentException si value es negativo
     */
    public Money {
        Objects.requireNonNull(value, "value");
        if (value.scale() > 2) value = value.setScale(2, java.math.RoundingMode.HALF_UP);
        if (value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Negative money");
    }

    /**
     * Crea un {@code Money} desde un long (por ejemplo 500000 = COP $500.000).
     */
    public static Money of(long v) {
        return new Money(BigDecimal.valueOf(v));
    }

    /**
     * Suma dos montos.
     */
    public Money plus(Money other) {
        return new Money(value.add(other.value));
    }

    /**
     * Resta dos montos, lanzando excepción si el resultado es negativo.
     *
     * @throws IllegalArgumentException si el resultado es negativo
     */
    public Money minus(Money other) {
        var r = value.subtract(other.value);
        if (r.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Negative result");
        return new Money(r);
    }

    /**
     * Compara este monto con otro.
     *
     * @return &lt;0 si menor, 0 si igual, &gt;0 si mayor
     */
    public int compareTo(Money other) {
        return value.compareTo(other.value);
    }
}

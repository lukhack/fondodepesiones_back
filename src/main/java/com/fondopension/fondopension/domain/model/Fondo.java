package com.fondopension.fondopension.domain.model;

import com.fondopension.fondopension.domain.enums.Categoria;
import com.fondopension.fondopension.domain.model.value.Money;
import lombok.Builder;
import lombok.Getter;

import lombok.Builder;
import lombok.Getter;

/**
 * Entidad de catálogo que describe un Fondo disponible para suscripción.
 * <p>Inmutable desde el punto de vista del dominio (usado en lectura/validación).</p>
 * @since 1.0
 */
@Getter
@Builder
public class Fondo {
    /** Identificador del fondo (puede ser un código o string numérico). */
    private final String id;
    /** Nombre legible del fondo. */
    private final String nombre;
    /** Monto mínimo requerido para vincularse. */
    private final Money montoMinimo;
    /** Categoría (FPV o FIC). */
    private final Categoria categoria;
}

package com.fondopension.fondopension.domain.model;

import com.fondopension.fondopension.domain.enums.TipoTransaccion;
import com.fondopension.fondopension.domain.model.value.Money;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Entidad de auditoría/bitácora que registra aperturas y cancelaciones.
 * <p>Siempre tiene un identificador único y la foto de saldos antes y después.</p>
 * @since 1.0
 */
@Getter
@Builder
public class Transaccion {
    /** Identificador único de la transacción. */
    private final String id;
    /** Tipo de transacción (Apertura o Cancelación). */
    private final TipoTransaccion tipo;
    /** Fondo relacionado (id). */
    private final String fondoId;
    /** Fondo relacionado (nombre, para facilitar reportes). */
    private final String fondoNombre;
    /** Monto de la operación (bloqueado o liberado). */
    private final Money monto;
    /** Saldo disponible antes de la operación. */
    private final Money balanceAntes;
    /** Saldo disponible después de la operación. */
    private final Money balanceDespues;
    /** Fecha/hora en que ocurrió. */
    private final Instant ocurrioEn;
}

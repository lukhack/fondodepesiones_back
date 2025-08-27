package com.fondopension.fondopension.domain.model;

import com.fondopension.fondopension.domain.model.value.Money;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Entidad que representa la suscripción activa de una {@link Cuenta} a un {@link Fondo}.
 * <p>Modela el bloqueo del monto mínimo mientras la suscripción esté vigente.</p>
 * @since 1.0
 */
@Getter
@Builder
public class Tenencia {
    /** Identificador único de la tenencia. */
    private final String id;
    /** Identificador de la cuenta (único usuario en este ejercicio). */
    private final String cuentaId;
    /** Identificador del fondo suscrito. */
    private final String fondoId;
    /** Monto bloqueado igual al mínimo de vinculación del fondo. */
    private final Money montoBloqueado;
    /** Fecha/hora de creación. */
    private final Instant creadaEn;
}

package com.fondopension.fondopension.infrastructure.rest.dto;

import com.fondopension.fondopension.domain.enums.TipoTransaccion;
import lombok.*;

import java.time.Instant;

/**
 * DTO de respuesta para transacciones.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransaccionResponse {
    private String id;
    private TipoTransaccion tipo;
    private String fondoId;
    private String fondoNombre;
    private long monto; // COP
    private long balanceAntes;
    private long balanceDespues;
    private Instant ocurrioEn;
}
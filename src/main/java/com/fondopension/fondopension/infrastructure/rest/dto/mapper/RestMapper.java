package com.fondopension.fondopension.infrastructure.rest.dto.mapper;
import com.fondopension.fondopension.domain.model.Transaccion;
import com.fondopension.fondopension.infrastructure.rest.dto.TransaccionResponse;
import lombok.experimental.UtilityClass;

/**
 * Mapper utilitario para REST.
 */
@UtilityClass
public class RestMapper {
    public TransaccionResponse toResponse(Transaccion t) {
        return TransaccionResponse.builder()
                .id(t.getId())
                .tipo(t.getTipo())
                .fondoId(t.getFondoId())
                .fondoNombre(t.getFondoNombre())
                .monto(t.getMonto().value().longValue())
                .balanceAntes(t.getBalanceAntes().value().longValue())
                .balanceDespues(t.getBalanceDespues().value().longValue())
                .ocurrioEn(t.getOcurrioEn())
                .build();
    }
}

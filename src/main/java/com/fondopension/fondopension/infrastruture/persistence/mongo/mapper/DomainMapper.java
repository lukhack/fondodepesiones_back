package com.fondopension.fondopension.infrastruture.persistence.mongo.mapper;

import com.fondopension.fondopension.domain.model.*;
import com.fondopension.fondopension.domain.model.value.Money;
import com.fondopension.fondopension.infrastruture.persistence.mongo.doc.*;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

/**
 * Mapper utilitario para convertir entre documentos Mongo y modelos de dominio.
 */
@UtilityClass
public class DomainMapper {

    public Fondo toDomain(FondoDoc d) {
        if (d == null) return null;
        return Fondo.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .montoMinimo(new Money(BigDecimal.valueOf(d.getMontoMinimo())))
                .categoria(d.getCategoria())
                .build();
    }

    public Tenencia toDomain(TenenciaDoc d) {
        if (d == null) return null;
        return Tenencia.builder()
                .id(d.getId())
                .cuentaId(d.getCuentaId())
                .fondoId(d.getFondoId())
                .montoBloqueado(Money.of(d.getMontoBloqueado()))
                .creadaEn(d.getCreadaEn())
                .build();
    }

    public Transaccion toDomain(TransaccionDoc d) {
        if (d == null) return null;
        return Transaccion.builder()
                .id(d.getId())
                .tipo(d.getTipo())
                .fondoId(d.getFondoId())
                .fondoNombre(d.getFondoNombre())
                .monto(Money.of(d.getMonto()))
                .balanceAntes(Money.of(d.getBalanceAntes()))
                .balanceDespues(Money.of(d.getBalanceDespues()))
                .ocurrioEn(d.getOcurrioEn())
                .build();
    }

    public Cuenta toDomain(CuentaDoc d) {
        if (d == null) return null;
        return Cuenta.builder()
                .id(d.getId())
                .saldoInicial(Money.of(d.getSaldoInicial()))
                .build();
    }

    public TenenciaDoc toDoc(Tenencia t) {
        if (t == null) return null;
        return TenenciaDoc.builder()
                .id(t.getId())
                .cuentaId(t.getCuentaId())
                .fondoId(t.getFondoId())
                .montoBloqueado(t.getMontoBloqueado().value().longValue())
                .creadaEn(t.getCreadaEn())
                .build();
    }

    public TransaccionDoc toDoc(Transaccion t) {
        if (t == null) return null;
        return TransaccionDoc.builder()
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

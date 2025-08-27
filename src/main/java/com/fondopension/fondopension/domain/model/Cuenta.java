package com.fondopension.fondopension.domain.model;

import com.fondopension.fondopension.domain.model.value.Money;
import lombok.Builder;
import lombok.Getter;

import java.util.Collection;

/**
 * Agregado Cuenta del usuario final (modelo simplificado a un único titular).
 * <p>
 * El saldo disponible se calcula como: {@code saldoInicial - sum(montos bloqueados por tenencias)}.
 * </p>
 * @since 1.0
 */
@Getter
@Builder
public class Cuenta {
    /** Identificador de la cuenta. */
    private final String id;
    /** Saldo inicial configurado (COP $500.000 en el reto). */
    private final Money saldoInicial;

    /**
     * Calcula el saldo disponible en función de las tenencias activas.
     *
     * @param tenencias colección de tenencias actualmente activas
     * @return dinero disponible para nuevas suscripciones (nunca negativo)
     */
    public Money saldoDisponible(Collection<Tenencia> tenencias) {
        var bloqueado = tenencias.stream()
                .map(Tenencia::getMontoBloqueado)
                .reduce(Money.of(0), Money::plus);

        int cmp = saldoInicial.compareTo(bloqueado);
        return (cmp > 0) ? saldoInicial.minus(bloqueado) : Money.of(0);
    }
}

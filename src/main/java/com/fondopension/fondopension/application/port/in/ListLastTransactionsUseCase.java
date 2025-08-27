package com.fondopension.fondopension.application.port.in;

import com.fondopension.fondopension.domain.model.Transaccion;

import java.util.List;

/**
 * Caso de uso para consultar el historial de últimas transacciones.
 * @since 1.0
 */
public interface ListLastTransactionsUseCase {

    /**
     * Lista las últimas {@code limit} transacciones en orden descendente por fecha.
     *
     * @param limit número máximo de registros a retornar
     * @return lista de transacciones
     */
    List<Transaccion> execute(int limit);
}
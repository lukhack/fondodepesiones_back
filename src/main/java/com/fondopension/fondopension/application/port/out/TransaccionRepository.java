package com.fondopension.fondopension.application.port.out;


import com.fondopension.fondopension.domain.model.Transaccion;

import java.util.List;

/**
 * Puerto de salida para registrar y consultar transacciones.
 * @since 1.0
 */
public interface TransaccionRepository {
    Transaccion save(Transaccion tx);
    List<Transaccion> findLast(int limit);
}

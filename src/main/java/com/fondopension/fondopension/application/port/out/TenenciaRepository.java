package com.fondopension.fondopension.application.port.out;


import com.fondopension.fondopension.domain.model.Tenencia;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para gestionar tenencias activas (suscripciones).
 * @since 1.0
 */
public interface TenenciaRepository {
    Optional<Tenencia> findByCuentaAndFondo(String cuentaId, String fondoId);
    List<Tenencia> findByCuenta(String cuentaId);
    Tenencia save(Tenencia tenencia);
    void deleteById(String tenenciaId);
}

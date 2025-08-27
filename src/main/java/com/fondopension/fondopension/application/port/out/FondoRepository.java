package com.fondopension.fondopension.application.port.out;


import com.fondopension.fondopension.domain.model.Fondo;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para consultar el catálogo de Fondos.
 * <p>Implementación típica: adapter de persistencia (MongoRepository/JPA/etc.).</p>
 * @since 1.0
 */
public interface FondoRepository {
    Optional<Fondo> findById(String id);
    List<Fondo> findAll();
}
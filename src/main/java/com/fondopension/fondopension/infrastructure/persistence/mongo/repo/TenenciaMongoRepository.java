package com.fondopension.fondopension.infrastructure.persistence.mongo.repo;


import com.fondopension.fondopension.infrastructure.persistence.mongo.doc.TenenciaDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data para Tenencias.
 */
public interface TenenciaMongoRepository extends MongoRepository<TenenciaDoc, String> {
    Optional<TenenciaDoc> findByCuentaIdAndFondoId(String cuentaId, String fondoId);
    List<TenenciaDoc> findByCuentaId(String cuentaId);
}
package com.fondopension.fondopension.infrastruture.persistence.mongo.repo;

import com.fondopension.fondopension.infrastruture.persistence.mongo.doc.TransaccionDoc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repositorio Spring Data para Transacciones.
 */
public interface TransaccionMongoRepository extends MongoRepository<TransaccionDoc, String> {
    List<TransaccionDoc> findAllByOrderByOcurrioEnDesc(Pageable pageable);
}

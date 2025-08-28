package com.fondopension.fondopension.infrastructure.persistence.mongo.repo;


import com.fondopension.fondopension.infrastructure.persistence.mongo.doc.FondoDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
/**
 * Repositorio Spring Data para Fondos.
 */
public interface FondoMongoRepository extends MongoRepository<FondoDoc, String> { }

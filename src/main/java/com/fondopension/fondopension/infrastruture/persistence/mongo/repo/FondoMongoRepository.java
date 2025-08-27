package com.fondopension.fondopension.infrastruture.persistence.mongo.repo;


import com.fondopension.fondopension.infrastruture.persistence.mongo.doc.FondoDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
/**
 * Repositorio Spring Data para Fondos.
 */
public interface FondoMongoRepository extends MongoRepository<FondoDoc, String> { }

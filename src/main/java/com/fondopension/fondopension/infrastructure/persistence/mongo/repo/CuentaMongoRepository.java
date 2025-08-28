package com.fondopension.fondopension.infrastructure.persistence.mongo.repo;

import com.fondopension.fondopension.infrastructure.persistence.mongo.doc.CuentaDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CuentaMongoRepository extends MongoRepository<CuentaDoc, String> { }

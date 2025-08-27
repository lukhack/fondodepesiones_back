package com.fondopension.fondopension.infrastruture.persistence.mongo.repo;

import com.fondopension.fondopension.infrastruture.persistence.mongo.doc.CuentaDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CuentaMongoRepository extends MongoRepository<CuentaDoc, String> { }

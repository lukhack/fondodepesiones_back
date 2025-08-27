package com.fondopension.fondopension.infrastruture.persistence.mongo.adapter;

import com.fondopension.fondopension.application.port.out.FondoRepository;
import com.fondopension.fondopension.domain.model.Fondo;
import com.fondopension.fondopension.infrastruture.persistence.mongo.mapper.DomainMapper;
import com.fondopension.fondopension.infrastruture.persistence.mongo.repo.FondoMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter Mongo para catálogo de Fondos.
 */
@Repository
@RequiredArgsConstructor
public class FondoRepositoryMongoAdapter implements FondoRepository {

    private final FondoMongoRepository repo;

    @Override
    public Optional<Fondo> findById(String id) {
        return repo.findById(id).map(DomainMapper::toDomain);
    }

    @Override
    public List<Fondo> findAll() {
        return repo.findAll().stream().map(DomainMapper::toDomain).toList();
    }
}

package com.fondopension.fondopension.infrastruture.persistence.mongo.adapter;

import com.fondopension.fondopension.application.port.out.TransaccionRepository;
import com.fondopension.fondopension.domain.model.Transaccion;
import com.fondopension.fondopension.infrastruture.persistence.mongo.mapper.DomainMapper;
import com.fondopension.fondopension.infrastruture.persistence.mongo.repo.TransaccionMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Adapter Mongo para Transacciones (auditoría).
 */
@Repository
@RequiredArgsConstructor
public class TransaccionRepositoryMongoAdapter implements TransaccionRepository {

    private final TransaccionMongoRepository repo;

    @Override
    public Transaccion save(Transaccion tx) {
        var saved = repo.save(DomainMapper.toDoc(tx));
        return DomainMapper.toDomain(saved);
    }

    @Override
    public List<Transaccion> findLast(int limit) {
        return repo.findAllByOrderByOcurrioEnDesc(PageRequest.of(0, Math.max(limit, 1)))
               .stream().map(DomainMapper::toDomain).toList();
    }
}

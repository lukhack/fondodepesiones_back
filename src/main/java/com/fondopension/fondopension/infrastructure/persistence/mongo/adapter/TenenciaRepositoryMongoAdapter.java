package com.fondopension.fondopension.infrastructure.persistence.mongo.adapter;

import com.fondopension.fondopension.application.port.out.TenenciaRepository;
import com.fondopension.fondopension.domain.model.Tenencia;
import com.fondopension.fondopension.infrastructure.persistence.mongo.doc.TenenciaDoc;
import com.fondopension.fondopension.infrastructure.persistence.mongo.mapper.DomainMapper;
import com.fondopension.fondopension.infrastructure.persistence.mongo.repo.TenenciaMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter Mongo para gestionar Tenencias.
 */
@Repository
@RequiredArgsConstructor
public class TenenciaRepositoryMongoAdapter implements TenenciaRepository {

    private final TenenciaMongoRepository repo;

    @Override
    public Optional<Tenencia> findByCuentaAndFondo(String cuentaId, String fondoId) {
        return repo.findByCuentaIdAndFondoId(cuentaId, fondoId).map(DomainMapper::toDomain);
    }

    @Override
    public List<Tenencia> findByCuenta(String cuentaId) {
        return repo.findByCuentaId(cuentaId).stream().map(DomainMapper::toDomain).toList();
    }

    @Override
    public Tenencia save(Tenencia tenencia) {
        TenenciaDoc saved = repo.save(DomainMapper.toDoc(tenencia));
        return DomainMapper.toDomain(saved);
    }

    @Override
    public void deleteById(String tenenciaId) {
        repo.deleteById(tenenciaId);
    }
}
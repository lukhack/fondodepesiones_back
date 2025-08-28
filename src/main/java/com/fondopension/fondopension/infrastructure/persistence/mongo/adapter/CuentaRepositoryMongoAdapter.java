package com.fondopension.fondopension.infrastructure.persistence.mongo.adapter;

import com.fondopension.fondopension.application.port.out.CuentaRepository;
import com.fondopension.fondopension.domain.model.Cuenta;
import com.fondopension.fondopension.infrastructure.persistence.mongo.mapper.DomainMapper;
import com.fondopension.fondopension.infrastructure.persistence.mongo.repo.CuentaMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CuentaRepositoryMongoAdapter implements CuentaRepository {

    private final CuentaMongoRepository repo;

    @Override
    public Optional<Cuenta> getSingleton() {
        // En el seed cargamos una sola cuenta con id "CUENTA_UNICA"
        return repo.findById("CUENTA_UNICA").map(DomainMapper::toDomain);
    }
}

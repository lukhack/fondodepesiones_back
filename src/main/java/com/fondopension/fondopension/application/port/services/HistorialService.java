package com.fondopension.fondopension.application.port.services;

import com.fondopension.fondopension.application.port.in.ListLastTransactionsUseCase;
import com.fondopension.fondopension.application.port.out.TransaccionRepository;
import com.fondopension.fondopension.domain.model.Transaccion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del caso de uso para consultar el historial de transacciones.
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class HistorialService implements ListLastTransactionsUseCase {
    private final TransaccionRepository txRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaccion> execute(int limit) {
        return txRepo.findLast(limit);
    }
}
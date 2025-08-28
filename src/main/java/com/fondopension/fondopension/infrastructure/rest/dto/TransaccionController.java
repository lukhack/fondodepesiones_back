package com.fondopension.fondopension.infrastructure.rest.dto;
import com.fondopension.fondopension.application.port.in.ListLastTransactionsUseCase;
import com.fondopension.fondopension.infrastructure.rest.dto.mapper.RestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints para consultar transacciones.
 */
@RestController
@RequestMapping("/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final ListLastTransactionsUseCase historialUC;

    /**
     * Retorna las últimas N transacciones (por defecto 10).
     */
    @GetMapping
    public List<TransaccionResponse> listar(@RequestParam(defaultValue = "10") int limit) {
        return historialUC.execute(limit).stream().map(RestMapper ::toResponse).toList();
    }
}
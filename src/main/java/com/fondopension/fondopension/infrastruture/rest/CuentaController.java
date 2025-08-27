package com.fondopension.fondopension.infrastruture.rest;
import com.fondopension.fondopension.application.port.out.CuentaRepository;
import com.fondopension.fondopension.application.port.out.TenenciaRepository;
import com.fondopension.fondopension.domain.model.Cuenta;
import lombok.*;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoint para consultar saldo disponible de la cuenta.
 */
@RestController
@RequestMapping("/cuenta")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaRepository cuentaRepo;
    private final TenenciaRepository tenenciaRepo;

    /**
     * Retorna saldo inicial y saldo disponible.
     */
    @GetMapping
    public Map<String, Object> obtenerCuenta() {
        Cuenta cuenta = cuentaRepo.getSingleton().orElseThrow(() -> new IllegalStateException("Cuenta no configurada"));
        var tenencias = tenenciaRepo.findByCuenta(cuenta.getId());
        var disponible = cuenta.saldoDisponible(tenencias);
        return Map.of(
                "id", cuenta.getId(),
                "saldoInicial", cuenta.getSaldoInicial().value().longValue(),
                "saldoDisponible", disponible.value().longValue()
        );
    }
}
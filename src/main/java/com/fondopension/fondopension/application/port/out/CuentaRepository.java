package com.fondopension.fondopension.application.port.out;


import com.fondopension.fondopension.domain.model.Cuenta;

import java.util.Optional;

/**
 * Puerto de salida para consultar la Cuenta del usuario (modelo de 1 cuenta).
 * @since 1.0
 */
public interface CuentaRepository {
    /**
     * Retorna la única cuenta configurada en el sistema.
     */
    Optional<Cuenta> getSingleton();
}

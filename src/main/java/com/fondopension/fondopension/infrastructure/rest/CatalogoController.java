package com.fondopension.fondopension.infrastructure.rest;

import com.fondopension.fondopension.application.port.out.FondoRepository;
import com.fondopension.fondopension.domain.model.Fondo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de catálogo: listar fondos.
 */
@RestController
@RequestMapping("/fondos")
@RequiredArgsConstructor
public class CatalogoController {

    private final FondoRepository fondos;

    /**
     * Retorna todos los fondos disponibles.
     */
    @GetMapping
    public List<Fondo> listarFondos() {
        return fondos.findAll();
    }
}

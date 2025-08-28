package com.fondopension.fondopension.infrastructure.system;

import com.fondopension.fondopension.application.port.out.IdGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Generador de IDs basado en UUID.
 */
@Component
public class IdGeneratorUUID implements IdGenerator {
    @Override public String nextId() { return UUID.randomUUID().toString(); }
}

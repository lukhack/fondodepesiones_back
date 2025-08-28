package com.fondopension.fondopension.infrastructure.system;

import com.fondopension.fondopension.application.port.out.ClockProvider;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Proveedor de tiempo del sistema (UTC).
 */
@Component
public class ClockProviderSystem implements ClockProvider {
    @Override public Instant now() { return Instant.now(); }
}

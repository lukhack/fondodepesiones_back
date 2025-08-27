package com.fondopension.fondopension.application.port.out;

import java.time.Instant;

/**
 * Puerto para desacoplar la provisión del tiempo actual.
 * <p><b>Nota:</b> Evita llamar directamente a Instant.now() o new Date().
 * Hace tu dominio testeable: en pruebas unitarias puedes inyectar un “reloj fijo” (Clock.fixed(...)) y validar que las fechas generadas sean exactamente las esperadas.
 * En entornos con zonas horarias o auditoría, te permite controlar el origen del tiempo (ej: sincronizar con NTP o base de datos)</p>
 *
 * <p>Permite pruebas determinísticas inyectando un reloj fijo.</p>
 * @since 1.0
 */
public interface ClockProvider {
    /**
     * Retorna el instante actual.
     */
    Instant now();
}

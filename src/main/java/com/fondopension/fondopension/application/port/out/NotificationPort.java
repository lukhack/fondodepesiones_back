package com.fondopension.fondopension.application.port.out;

import com.fondopension.fondopension.domain.model.Transaccion;

/**
 * Puerto de salida para envío de notificaciones al usuario.
 * <p>Se puede implementar con servicios de correo (SMTP/API) o SMS (Twilio, etc.).</p>
 * @since 1.0
 */
public interface NotificationPort {

    /** Canales soportados por los adapters de notificación. */
    enum Channel { EMAIL, SMS }

    /**
     * Envía la notificación asociada a una transacción.
     *
     * @param channel     canal de salida
     * @param destination correo o número de teléfono
     * @param tx          transacción a notificar
     */
    void notify(Channel channel, String destination, Transaccion tx);
}

package com.fondopension.fondopension.infrastructure.notification;

import com.fondopension.fondopension.application.port.out.NotificationPort;
import com.fondopension.fondopension.domain.model.Transaccion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adapter de notificaciones que registra en log (stub).
 * <p>Reemplazable por SMTP/SMS reales (SendGrid, Twilio, etc.).</p>
 */
@Slf4j
@Component
public class EmailSmsLogNotificationAdapter implements NotificationPort {

    @Override
    public void notify(Channel channel, String destination, Transaccion tx) {
        log.info("Notificación [{}] -> {} | TX {} {} ${} (antes ${} / después ${})",
                channel, destination, tx.getId(), tx.getTipo(),
                tx.getMonto().value(), tx.getBalanceAntes().value(), tx.getBalanceDespues().value());
    }
}

package com.fondopension.fondopension.application.port.services;

import com.fondopension.fondopension.application.port.in.SubscribeToFondoUseCase;
import com.fondopension.fondopension.application.port.out.*;
import com.fondopension.fondopension.domain.enums.TenenciaDuplicadaException;
import com.fondopension.fondopension.domain.enums.TipoTransaccion;
import com.fondopension.fondopension.domain.exception.BusinessException;
import com.fondopension.fondopension.domain.exception.SaldoInsuficienteException;
import com.fondopension.fondopension.domain.model.Tenencia;
import com.fondopension.fondopension.domain.model.Transaccion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementación del caso de uso de suscripción a un fondo.
 * <p>
 * Flujo:
 * <ol>
 *   <li>Obtiene cuenta única y fondo.</li>
 *   <li>Valida que no haya tenencia duplicada para ese fondo.</li>
 *   <li>Calcula saldo disponible y compara contra monto mínimo del fondo.</li>
 *   <li>Persiste la tenencia (bloqueo) y registra transacción.</li>
 *   <li>Envía notificación (email o SMS).</li>
 * </ol>
 * </p>
 * <p><b>Invariante:</b> nunca se permite un saldo disponible negativo.</p>
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class SuscripcionService implements SubscribeToFondoUseCase {

    private final CuentaRepository cuentaRepo;
    private final FondoRepository fondoRepo;
    private final TenenciaRepository tenenciaRepo;
    private final TransaccionRepository txRepo;
    private final NotificationPort notifier;
    private final IdGenerator idGen;
    private final ClockProvider clock;

    /**
     * {@inheritDoc}
     */
    @Override
    public String execute(Command cmd) {
        var cuenta = cuentaRepo.getSingleton()
                .orElseThrow(() -> new BusinessException("Cuenta no encontrada"));
        var fondo = fondoRepo.findById(cmd.fondoId())
                .orElseThrow(() -> new BusinessException("Fondo no existe"));

        tenenciaRepo.findByCuentaAndFondo(cuenta.getId(), fondo.getId())
                .ifPresent(t -> { throw new TenenciaDuplicadaException("Ya está vinculado al fondo"); });

        var tenencias = tenenciaRepo.findByCuenta(cuenta.getId());
        var disponible = cuenta.saldoDisponible(tenencias);

        if (disponible.compareTo(fondo.getMontoMinimo()) < 0) {
            throw new SaldoInsuficienteException(
                    "No tiene saldo disponible para vincularse al fondo " + fondo.getNombre());
        }

        // Crear tenencia (bloqueo del mínimo)
        var tenencia = Tenencia.builder()
                .id(idGen.nextId())
                .cuentaId(cuenta.getId())
                .fondoId(fondo.getId())
                .montoBloqueado(fondo.getMontoMinimo())
                .creadaEn(clock.now())
                .build();
        tenenciaRepo.save(tenencia);

        // Registrar transacción (foto de saldos)
        var before = disponible;
        var after = disponible.minus(fondo.getMontoMinimo());
        var tx = Transaccion.builder()
                .id(idGen.nextId())
                .tipo(TipoTransaccion.APERTURA)
                .fondoId(fondo.getId())
                .fondoNombre(fondo.getNombre())
                .monto(fondo.getMontoMinimo())
                .balanceAntes(before)
                .balanceDespues(after)
                .ocurrioEn(clock.now())
                .build();
        txRepo.save(tx);

        // Notificar
        notifier.notify(NotificationPort.Channel.valueOf(cmd.channel().name()), cmd.destination(), tx);
        return tx.getId();
    }
}

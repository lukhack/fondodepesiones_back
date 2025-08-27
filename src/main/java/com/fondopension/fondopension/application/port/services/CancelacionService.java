package com.fondopension.fondopension.application.port.services;

import com.fondopension.fondopension.application.port.in.CancelFromFondoUseCase;
import com.fondopension.fondopension.application.port.out.*;
import com.fondopension.fondopension.domain.enums.TipoTransaccion;
import com.fondopension.fondopension.domain.exception.BusinessException;
import com.fondopension.fondopension.domain.model.Transaccion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementación del caso de uso de cancelación de una suscripción.
 * <p>
 * Flujo:
 * <ol>
 *   <li>Obtiene cuenta y fondo.</li>
 *   <li>Verifica existencia de tenencia.</li>
 *   <li>Calcula saldo disponible antes.</li>
 *   <li>Elimina tenencia (liberación del bloqueo).</li>
 *   <li>Registra transacción y notifica.</li>
 * </ol>
 * </p>
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CancelacionService implements CancelFromFondoUseCase {

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
        var tenencia = tenenciaRepo.findByCuentaAndFondo(cuenta.getId(), fondo.getId())
                .orElseThrow(() -> new BusinessException("No hay suscripción activa en el fondo"));

        // Saldo disponible antes de liberar
        var disponibleAntes = cuenta.saldoDisponible(tenenciaRepo.findByCuenta(cuenta.getId()));

        // Eliminar tenencia (libera saldo)
        tenenciaRepo.deleteById(tenencia.getId());

        // Saldo disponible después
        var disponibleDespues = disponibleAntes.plus(tenencia.getMontoBloqueado());

        var tx = Transaccion.builder()
                .id(idGen.nextId())
                .tipo(TipoTransaccion.CANCELACION)
                .fondoId(fondo.getId())
                .fondoNombre(fondo.getNombre())
                .monto(tenencia.getMontoBloqueado())
                .balanceAntes(disponibleAntes)
                .balanceDespues(disponibleDespues)
                .ocurrioEn(clock.now())
                .build();
        txRepo.save(tx);

        notifier.notify(NotificationPort.Channel.valueOf(cmd.channel().name()), cmd.destination(), tx);
        return tx.getId();
    }
}

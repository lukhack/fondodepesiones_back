package com.fondopension.fondopension.application.service;

import com.fondopension.fondopension.application.port.in.CancelFromFondoUseCase;
import com.fondopension.fondopension.application.port.out.*;
import com.fondopension.fondopension.application.port.services.CancelacionService;
import com.fondopension.fondopension.domain.enums.Categoria;
import com.fondopension.fondopension.domain.enums.TipoTransaccion;
import com.fondopension.fondopension.domain.exception.BusinessException;
import com.fondopension.fondopension.domain.model.Cuenta;
import com.fondopension.fondopension.domain.model.Fondo;
import com.fondopension.fondopension.domain.model.Tenencia;
import com.fondopension.fondopension.domain.model.Transaccion;
import com.fondopension.fondopension.domain.model.value.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelacionServiceTest {

    @Mock
    CuentaRepository cuentaRepo;
    @Mock FondoRepository fondoRepo;
    @Mock TenenciaRepository tenenciaRepo;
    @Mock TransaccionRepository txRepo;
    @Mock NotificationPort notifier;
    @Mock IdGenerator idGen;
    @Mock ClockProvider clock;

    CancelacionService service;

    private static Money money(long v){ return new Money(BigDecimal.valueOf(v)); }

    @BeforeEach
    void setUp() {
        service = new CancelacionService(cuentaRepo, fondoRepo, tenenciaRepo, txRepo, notifier, idGen, clock);
    }

    /**
     * CASO FELIZ: existe tenencia → eliminarla retorna el monto bloqueado.
     * Cubre:
     *  - Acción (2) Cancelación
     *  - Acción (4) Notificación
     *  - Regla: "Al retirarse, el valor de vinculación se retorna al cliente"
     */
    @Test
    void should_cancel_and_return_blocked_amount() {
        var cuenta = Cuenta.builder().id("CUENTA_UNICA").saldoInicial(money(500_000)).build();
        var fondo  = Fondo.builder().id("3").nombre("DEUDAPRIVADA").montoMinimo(money(50_000)).categoria(Categoria.FIC).build();
        var tenencia = Tenencia.builder()
                .id("TEN-1").cuentaId("CUENTA_UNICA").fondoId("3").montoBloqueado(money(50_000)).creadaEn(Instant.now()).build();

        // Antes: una tenencia de 50k → disponibleAntes = 450k
        when(cuentaRepo.getSingleton()).thenReturn(Optional.of(cuenta));
        when(fondoRepo.findById("3")).thenReturn(Optional.of(fondo));
        when(tenenciaRepo.findByCuentaAndFondo("CUENTA_UNICA", "3")).thenReturn(Optional.of(tenencia));
        when(tenenciaRepo.findByCuenta("CUENTA_UNICA")).thenReturn(List.of(tenencia));

        when(idGen.nextId()).thenReturn("TX-CAN-1");
        var now = Instant.parse("2025-01-02T00:00:00Z");
        when(clock.now()).thenReturn(now);

        ArgumentCaptor<Transaccion> txCaptor = ArgumentCaptor.forClass(Transaccion.class);
        when(txRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        String txId = service.execute(
                new CancelFromFondoUseCase.Command(
                        "3",
                        CancelFromFondoUseCase.Channel.SMS,
                        "3001234567"
                )
        );

        // Assert
        assertThat(txId).isEqualTo("TX-CAN-1");
        verify(tenenciaRepo).deleteById("TEN-1"); // elimina la suscripción

        verify(txRepo).save(txCaptor.capture());
        var tx = txCaptor.getValue();
        assertThat(tx.getTipo()).isEqualTo(TipoTransaccion.CANCELACION); // Acción (2)
        assertThat(tx.getMonto().value().longValue()).isEqualTo(50_000);
        assertThat(tx.getBalanceAntes().value().longValue()).isEqualTo(450_000);
        assertThat(tx.getBalanceDespues().value().longValue()).isEqualTo(500_000); // retorna el valor

        verify(notifier).notify(NotificationPort.Channel.SMS, "3001234567", tx); // Acción (4)
    }

    /**
     * NEGATIVO: no existe tenencia activa para ese fondo.
     */
    @Test
    void should_fail_when_no_active_subscription() {
        var cuenta = Cuenta.builder().id("CUENTA_UNICA").saldoInicial(money(500_000)).build();
        var fondo  = Fondo.builder().id("3").nombre("DEUDAPRIVADA").montoMinimo(money(50_000)).categoria(Categoria.FIC).build();

        when(cuentaRepo.getSingleton()).thenReturn(Optional.of(cuenta));
        when(fondoRepo.findById("3")).thenReturn(Optional.of(fondo));
        when(tenenciaRepo.findByCuentaAndFondo("CUENTA_UNICA", "3")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.execute(new CancelFromFondoUseCase.Command("3", CancelFromFondoUseCase.Channel.SMS, "300")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No hay suscripción activa");

        verify(tenenciaRepo, never()).deleteById(any());
        verify(txRepo, never()).save(any());
        verify(notifier, never()).notify(any(), any(), any());
    }
}

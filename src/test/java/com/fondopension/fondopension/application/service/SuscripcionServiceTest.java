package com.fondopension.fondopension.application.service;

import com.fondopension.fondopension.application.port.in.SubscribeToFondoUseCase;
import com.fondopension.fondopension.application.port.out.*;
import com.fondopension.fondopension.application.port.services.SuscripcionService;
import com.fondopension.fondopension.domain.enums.Categoria;
import com.fondopension.fondopension.domain.enums.TenenciaDuplicadaException;
import com.fondopension.fondopension.domain.enums.TipoTransaccion;
import com.fondopension.fondopension.domain.exception.SaldoInsuficienteException;
import com.fondopension.fondopension.domain.model.Cuenta;
import com.fondopension.fondopension.domain.model.Fondo;
import com.fondopension.fondopension.domain.model.Tenencia;
import com.fondopension.fondopension.domain.model.Transaccion;
import com.fondopension.fondopension.domain.model.value.Money;
import org.junit.jupiter.api.*;                                 // JUnit 5
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;                                           // Mockito
import org.mockito.junit.jupiter.MockitoExtension;              // integración JUnit 5 + Mockito

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;                // AssertJ (aserciones fluidas)
import static org.mockito.Mockito.*;                            // métodos estáticos de Mockito

@ExtendWith(MockitoExtension.class) // habilita inyección de @Mock y validación de Mockito
class SuscripcionServiceTest {
    // ====== MOCKS DE PUERTOS (capa out) ======
    @Mock CuentaRepository cuentaRepo;            // obtener la cuenta única (saldo inicial)
    @Mock FondoRepository fondoRepo;              // catálogo de fondos
    @Mock TenenciaRepository tenenciaRepo;        // suscripciones (tenencias) activas
    @Mock TransaccionRepository txRepo;           // auditoría de transacciones
    @Mock NotificationPort notifier;              // envío de notificaciones (EMAIL/SMS)
    @Mock IdGenerator idGen;                      // generación de IDs
    @Mock ClockProvider clock;                    // tiempo controlado (tests determinísticos)

    SuscripcionService service;                   // SUT (System Under Test)
    // helper: crear Money desde long
    private static Money money(long v){ return new Money(BigDecimal.valueOf(v)); }

    @BeforeEach
    void setUp() {
        // construimos el servicio con los mocks (inyección manual)
        service = new SuscripcionService(cuentaRepo, fondoRepo, tenenciaRepo, txRepo, notifier, idGen, clock);
    }

    /**
     * CASO FELIZ: saldo suficiente → crea tenencia, registra TX APERTURA, notifica EMAIL.
     * Cubre:
     *  - Acción (1) Suscribirse
     *  - Acción (4) Notificación
     *  - Regla: ID único por transacción
     *  - Regla: saldo disponible >= monto mínimo del fondo
     */
    @Test
    void should_subscribe_when_balance_is_sufficient() {
        // Arrange (dados)
        var cuenta = Cuenta.builder().id("CUENTA_UNICA").saldoInicial(money(500_000)).build();
        var fondo = Fondo.builder()
                .id("3").nombre("DEUDAPRIVADA").montoMinimo(money(50_000)).categoria(Categoria.FIC).build();

        when(cuentaRepo.getSingleton()).thenReturn(Optional.of(cuenta));    // hay cuenta
        when(fondoRepo.findById("3")).thenReturn(Optional.of(fondo));       // y fondo id=3
        when(tenenciaRepo.findByCuentaAndFondo("CUENTA_UNICA", "3")).thenReturn(Optional.empty()); // no duplicado
        when(tenenciaRepo.findByCuenta("CUENTA_UNICA")).thenReturn(List.of()); // sin tenencias → disponible=500k

        // ID para tenencia y para transacción (dos llamadas a nextId)
        when(idGen.nextId()).thenReturn("TEN-1").thenReturn("TX-1");
        var now = Instant.parse("2025-01-01T00:00:00Z");
        when(clock.now()).thenReturn(now); // fija el tiempo a una marca determinística

        // capturamos la TX guardada para validar sus campos
        ArgumentCaptor<Transaccion> txCaptor = ArgumentCaptor.forClass(Transaccion.class);
        when(txRepo.save(any())).thenAnswer(inv -> inv.getArgument(0)); // devolución sencilla

        // Act (cuando)
        String txId = service.execute(new SubscribeToFondoUseCase.Command(
                "3", SubscribeToFondoUseCase.Channel.EMAIL, "user@example.com"));

        // Assert (entonces)
        assertThat(txId).isEqualTo("TX-1"); // ID único de la transacción → Regla

        // valida que se creó la tenencia con el bloqueo correcto
        verify(tenenciaRepo).save(argThat(t ->
                t.getId().equals("TEN-1") &&
                        t.getCuentaId().equals("CUENTA_UNICA") &&
                        t.getFondoId().equals("3") &&
                        t.getMontoBloqueado().value().longValue() == 50_000 &&
                        t.getCreadaEn().equals(now)
        ));

        // valida contenido de la transacción de APERTURA (foto de saldos)
        verify(txRepo).save(txCaptor.capture());
        var tx = txCaptor.getValue();
        assertThat(tx.getId()).isEqualTo("TX-1");
        assertThat(tx.getTipo()).isEqualTo(TipoTransaccion.APERTURA);  // Acción (1)
        assertThat(tx.getFondoId()).isEqualTo("3");
        assertThat(tx.getFondoNombre()).isEqualTo("DEUDAPRIVADA");
        assertThat(tx.getMonto().value().longValue()).isEqualTo(50_000);
        assertThat(tx.getBalanceAntes().value().longValue()).isEqualTo(500_000);
        assertThat(tx.getBalanceDespues().value().longValue()).isEqualTo(450_000);
        assertThat(tx.getOcurrioEn()).isEqualTo(now);

        // verifica Notificación → Acción (4)
        verify(notifier).notify(NotificationPort.Channel.EMAIL, "user@example.com", tx);
    }

    /**
     * NEGATIVO: ya hay tenencia de ese fondo → bloquea duplicados.
     * Cubre: "una tenencia por fondo" del documento (regla de negocio implícita).
     */
    @Test
    void should_fail_on_duplicate_subscription() {
        var cuenta = Cuenta.builder().id("CUENTA_UNICA").saldoInicial(money(500_000)).build();
        var fondo = Fondo.builder().id("3").nombre("DEUDAPRIVADA").montoMinimo(money(50_000)).categoria(Categoria.FIC).build();
        var tenencia = Tenencia.builder().id("TEN-EXISTE").cuentaId("CUENTA_UNICA").fondoId("3")
                .montoBloqueado(money(50_000)).creadaEn(Instant.now()).build();

        when(cuentaRepo.getSingleton()).thenReturn(Optional.of(cuenta));
        when(fondoRepo.findById("3")).thenReturn(Optional.of(fondo));
        when(tenenciaRepo.findByCuentaAndFondo("CUENTA_UNICA", "3")).thenReturn(Optional.of(tenencia));

        assertThatThrownBy(() ->
                service.execute(new SubscribeToFondoUseCase.Command("3",
                        SubscribeToFondoUseCase.Channel.EMAIL, "user@example.com")))
                .isInstanceOf(TenenciaDuplicadaException.class)
                .hasMessageContaining("Ya está vinculado al fondo");

        verify(tenenciaRepo, never()).save(any()); // nada se guarda
        verify(txRepo, never()).save(any());       // nada se registra
        verify(notifier, never()).notify(any(), any(), any()); // nada se notifica
    }

    /**
     * NEGATIVO: saldo insuficiente para el monto mínimo del fondo.
     * Cubre la regla: "No tiene saldo disponible para vincularse al fondo <Nombre del fondo>".
     */
    @Test
    void should_fail_when_balance_is_insufficient() {
        var cuenta = Cuenta.builder().id("CUENTA_UNICA").saldoInicial(money(100_000)).build();
        var fondo = Fondo.builder().id("4").nombre("FDO-ACCIONES").montoMinimo(money(250_000)).categoria(Categoria.FIC).build();

        when(cuentaRepo.getSingleton()).thenReturn(Optional.of(cuenta));
        when(fondoRepo.findById("4")).thenReturn(Optional.of(fondo));
        when(tenenciaRepo.findByCuentaAndFondo("CUENTA_UNICA", "4")).thenReturn(Optional.empty());
        when(tenenciaRepo.findByCuenta("CUENTA_UNICA")).thenReturn(List.of()); // disponible = 100k

        assertThatThrownBy(() ->
                service.execute(new SubscribeToFondoUseCase.Command(
                        "4",
                        SubscribeToFondoUseCase.Channel.EMAIL,
                        "user@example.com"
                ))
        )
                .isInstanceOf(SaldoInsuficienteException.class)
                .hasMessageContaining("No tiene saldo disponible para vincularse al fondo FDO-ACCIONES");

        verify(tenenciaRepo, never()).save(any());
        verify(txRepo, never()).save(any());
        verify(notifier, never()).notify(any(), any(), any());
    }
}

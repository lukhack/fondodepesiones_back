package com.fondopension.fondopension.application.service;
import com.fondopension.fondopension.application.port.out.TransaccionRepository;
import com.fondopension.fondopension.application.port.services.HistorialService;
import com.fondopension.fondopension.domain.enums.TipoTransaccion;
import com.fondopension.fondopension.domain.model.Transaccion;
import com.fondopension.fondopension.domain.model.value.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistorialServiceTest {

    @Mock
    TransaccionRepository txRepo; // fuente de datos de historial

    private static Money money(long v){ return new Money(BigDecimal.valueOf(v)); }

    /**
     * Cubre Acción (3): devolver las últimas N transacciones (ya ordenadas desc en el adapter).
     */
    @Test
    void should_return_last_N_transactions() {
        // Arrange
        var t1 = Transaccion.builder()
                .id("TX-1").tipo(TipoTransaccion.APERTURA).fondoId("3").fondoNombre("DEUDAPRIVADA")
                .monto(money(50_000)).balanceAntes(money(500_000)).balanceDespues(money(450_000))
                .ocurrioEn(Instant.parse("2025-01-01T00:00:00Z")).build();
        var t2 = Transaccion.builder()
                .id("TX-2").tipo(TipoTransaccion.CANCELACION).fondoId("3").fondoNombre("DEUDAPRIVADA")
                .monto(money(50_000)).balanceAntes(money(450_000)).balanceDespues(money(500_000))
                .ocurrioEn(Instant.parse("2025-01-02T00:00:00Z")).build();

        when(txRepo.findLast(10)).thenReturn(List.of(t2, t1)); // simula orden desc por fecha

        var service = new HistorialService(txRepo);

        // Act
        var result = service.execute(10);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo("TX-2"); // primero la más reciente
        assertThat(result.get(1).getId()).isEqualTo("TX-1");
        verify(txRepo).findLast(10); // se delega correctamente
    }
}
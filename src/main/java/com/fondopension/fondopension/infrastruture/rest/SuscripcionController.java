package com.fondopension.fondopension.infrastruture.rest;
import com.fondopension.fondopension.application.port.in.CancelFromFondoUseCase;
import com.fondopension.fondopension.application.port.in.SubscribeToFondoUseCase;
import com.fondopension.fondopension.infrastruture.rest.dto.NotificacionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoints de suscripción (apertura/cancelación).
 */
@RestController
@RequestMapping("/suscripciones")
@RequiredArgsConstructor
public class SuscripcionController {

    private final SubscribeToFondoUseCase subscribeUC;
    private final CancelFromFondoUseCase cancelUC;

    /**
     * Suscribe la cuenta al fondo indicado.
     */
    @PostMapping("/{fondoId}")
    public Map<String, String> suscribir(@PathVariable String fondoId,
                                         @Valid @RequestBody NotificacionRequest req) {
        var cmd = new SubscribeToFondoUseCase.Command(
                fondoId,
                SubscribeToFondoUseCase.Channel.valueOf(req.getChannel().name()),
                req.getDestination()
        );
        var txId = subscribeUC.execute(cmd);
        return Map.of("transactionId", txId);
    }

    /**
     * Cancela la suscripción al fondo indicado.
     */
    @DeleteMapping("/{fondoId}")
    public Map<String, String> cancelar(@PathVariable String fondoId,
                                        @Valid @RequestBody NotificacionRequest req) {
        var cmd = new CancelFromFondoUseCase.Command(
                fondoId,
                CancelFromFondoUseCase.Channel.valueOf(req.getChannel().name()),
                req.getDestination()
        );
        var txId = cancelUC.execute(cmd);
        return Map.of("transactionId", txId);
    }
}
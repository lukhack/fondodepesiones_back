package com.fondopension.fondopension.application.port.in;
/**
 * Caso de uso de cancelación (salida) de un fondo.
 * <p>Elimina la tenencia, libera saldo, registra transacción y notifica.</p>
 * @since 1.0
 */
public interface CancelFromFondoUseCase {

    /**
     * Comando de entrada con datos del fondo y la preferencia de notificación.
     */
    record Command(String fondoId, Channel channel, String destination) {}

    /** Canal de notificación preferido por el usuario. */
    enum Channel { EMAIL, SMS }

    /**
     * Ejecuta la cancelación de la suscripción.
     *
     * @param command datos de entrada
     * @return id de la transacción generada
     * @throws com.btgpfv.domain.model.exceptions.BusinessException si no existe la tenencia
     */
    String execute(Command command);
}
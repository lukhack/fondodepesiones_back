package com.fondopension.fondopension.application.port.in;

/**
 * Caso de uso de suscripción (apertura) a un fondo.
 * <p>Valida saldo, evita duplicados, crea tenencia, registra transacción y notifica.</p>
 * @since 1.0
 */
public interface SubscribeToFondoUseCase {

    /**
     * Comando de entrada con datos del fondo y la preferencia de notificación.
     *
     * @param fondoId     identificador del fondo a suscribir
     * @param channel     canal de notificación (EMAIL o SMS)
     * @param destination destino (correo o número)
     */
    record Command(String fondoId, Channel channel, String destination) {}

    /**
     * Canal de notificación preferido por el usuario.
     */
    enum Channel { EMAIL, SMS }

    /**
     * Ejecuta la suscripción.
     *
     * @param command datos de entrada
     * @return id de la transacción generada
     * @throws com.fondopension.fondopension.domain.exception.BusinessException si falla una regla de negocio
     */
    String execute(Command command);
}
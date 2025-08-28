package com.fondopension.fondopension.infrastructure.rest.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
/**
 * DTO para la preferencia de notificación en apertura/cancelación.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificacionRequest {
    @NotNull private Channel channel;
    @NotBlank private String destination;
    public enum Channel { EMAIL, SMS }
}
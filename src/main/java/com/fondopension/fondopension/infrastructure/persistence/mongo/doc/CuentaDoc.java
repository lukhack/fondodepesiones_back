package com.fondopension.fondopension.infrastructure.persistence.mongo.doc;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Documento Mongo para la Cuenta (modelo de 1 sola cuenta).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "cuentas")
public class CuentaDoc {
    @Id
    private String id;
    private long saldoInicial; // COP
}

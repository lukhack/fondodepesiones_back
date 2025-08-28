package com.fondopension.fondopension.infrastructure.persistence.mongo.doc;

import com.fondopension.fondopension.domain.enums.Categoria;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Documento Mongo para Tenencias (suscripciones activas).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "fondos")
public class FondoDoc {
    @Id
    private String id;
    private String nombre;
    private long montoMinimo; // almacenado en centavos (COP) o entero simple; aquí usamos entero simple COP
    private Categoria categoria;
}

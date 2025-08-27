package com.fondopension.fondopension.infrastruture.persistence.mongo.doc;


import com.fondopension.fondopension.domain.enums.TipoTransaccion;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
/**
 * Documento Mongo para Transacciones (auditoría).
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Document(collection = "transacciones")
public class TransaccionDoc {
    @Id private String id;
    private TipoTransaccion tipo;
    private String fondoId;
    private String fondoNombre;
    private long monto; // COP
    private long balanceAntes; // COP
    private long balanceDespues; // COP
    @Indexed(name = "idx_ocurrioEn_desc") private Instant ocurrioEn;
}

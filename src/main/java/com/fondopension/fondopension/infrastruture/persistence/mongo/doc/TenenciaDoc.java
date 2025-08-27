package com.fondopension.fondopension.infrastruture.persistence.mongo.doc;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Document(collection = "tenencias")
@CompoundIndex(name = "ux_cuenta_fondo", def = "{'cuentaId':1,'fondoId':1}", unique = true)
public class TenenciaDoc {
    @Id private String id;
    private String cuentaId;
    private String fondoId;
    private long montoBloqueado; // COP
    private Instant creadaEn;
}
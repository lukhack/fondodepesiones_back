package com.fondopension.fondopension;

import com.fondopension.fondopension.domain.enums.Categoria;
import com.fondopension.fondopension.infrastructure.persistence.mongo.doc.CuentaDoc;
import com.fondopension.fondopension.infrastructure.persistence.mongo.doc.FondoDoc;
import com.fondopension.fondopension.infrastructure.persistence.mongo.repo.CuentaMongoRepository;
import com.fondopension.fondopension.infrastructure.persistence.mongo.repo.FondoMongoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FondopensionApplication {

	public static void main(String[] args) {
		SpringApplication.run(FondopensionApplication.class, args);
	}

    /**
     * Carga datos semilla si las colecciones están vacías.
     */
    @Bean
    CommandLineRunner seed(FondoMongoRepository fondos, CuentaMongoRepository cuentas) {
        return args -> {
            if (fondos.count() == 0) {
                fondos.save(FondoDoc.builder().id("1").nombre("FPV_BTG_PACTUAL_RECAUDADORA").montoMinimo(75000).categoria(Categoria.FPV).build());
                fondos.save(FondoDoc.builder().id("2").nombre("FPV_BTG_PACTUAL_ECOPETROL").montoMinimo(125000).categoria(Categoria.FPV).build());
                fondos.save(FondoDoc.builder().id("3").nombre("DEUDAPRIVADA").montoMinimo(50000).categoria(Categoria.FIC).build());
                fondos.save(FondoDoc.builder().id("4").nombre("FDO-ACCIONES").montoMinimo(250000).categoria(Categoria.FIC).build());
                fondos.save(FondoDoc.builder().id("5").nombre("FPV_BTG_PACTUAL_DINAMICA").montoMinimo(100000).categoria(Categoria.FPV).build());
            }
            if (cuentas.count() == 0) {
                cuentas.save(CuentaDoc.builder().id("CUENTA_UNICA").saldoInicial(500000).build());
            }
        };
    }
}

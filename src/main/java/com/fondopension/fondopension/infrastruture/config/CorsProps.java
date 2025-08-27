package com.fondopension.fondopension.infrastruture.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@Getter @Setter
@ConfigurationProperties(prefix = "app.cors")
public class CorsProps {
    /**
     * Orígenes permitidos. Admite:
     * - "*" (como único elemento) para wildcard
     * - Cadena separada por comas en properties/env (Spring la parsea a lista)
     */
    private List<String> allowedOrigins = List.of("*");

    public boolean isWildcard() {
        return allowedOrigins.size() == 1 && "*".equals(allowedOrigins.get(0));
    }

    public String[] asArray() {
        return allowedOrigins.toArray(String[]::new);
    }

    /** Soporta también inyección como cadena "a,b,c" desde env */
    public void setAllowedOrigins(String value) {
        if (value == null || value.isBlank()) {
            this.allowedOrigins = List.of("*");
        } else {
            this.allowedOrigins = Arrays.stream(value.split(","))
                    .map(String::trim).filter(s -> !s.isEmpty()).toList();
        }
    }
}
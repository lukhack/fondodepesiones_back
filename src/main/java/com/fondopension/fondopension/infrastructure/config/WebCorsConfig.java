package com.fondopension.fondopension.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(CorsProps.class)
@RequiredArgsConstructor
public class WebCorsConfig implements WebMvcConfigurer {

    private final CorsProps props;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var mapping = registry.addMapping("/**")
                .allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS")
                .allowedHeaders("*");

        if (props.isWildcard()) {
            // Si hay wildcard, usa patterns (permite credenciales opcionalmente)
            mapping.allowedOriginPatterns("*").allowCredentials(false);
        } else {
            mapping.allowedOrigins(props.asArray()).allowCredentials(true);
        }
    }
}

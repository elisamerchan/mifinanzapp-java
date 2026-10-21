package com.mifinanzapp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadatos de la documentación OpenAPI. Los servicios protegidos usan HTTP Basic con el email y la
 * contraseña con los que se registró el usuario.
 *
 * @since 1.2.0
 */
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA = "basicAuth";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mi FinanzApp - API REST")
                        .version("1.2.0")
                        .description("Servicios web de finanzas personales: autenticación, categorías, ingresos, "
                                + "gastos, presupuestos, recordatorios y resumen. Todo excepto /api/auth/** exige "
                                + "HTTP Basic (usuario = email)."))
                .components(new Components().addSecuritySchemes(ESQUEMA,
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA));
    }
}

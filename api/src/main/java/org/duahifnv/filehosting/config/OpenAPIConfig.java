package org.duahifnv.filehosting.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI authenticationAPI() {
        return new OpenAPI()
                .info(new Info().title("API для файлообменника с AES-256 защитой файлов")
                        .version("0.0.1")
                )
                .addSecurityItem(new SecurityRequirement().addList("JWT аутентификация"))
                .components(new Components()
                        .addSecuritySchemes(
                        "JWT аутентификация",
                        new SecurityScheme()
                                .name("JWT аутентификация")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
                );
    }
}
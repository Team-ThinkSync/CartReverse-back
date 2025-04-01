package com.project.webshopproject.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                // SecurityScheme 설정
                SecurityScheme securityScheme = new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization");

                SecurityRequirement securityRequirement = new SecurityRequirement();


                return new OpenAPI()
                        .info(new Info().title("WebShop API")
                                .version("1.0")
                                .description("WebShop project API documentation"))
                        .addSecurityItem(securityRequirement)
                        .schemaRequirement("bearerAuth", securityScheme);

        }
}

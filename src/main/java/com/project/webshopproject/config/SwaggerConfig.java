package com.project.webshopproject.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ECLO API 문서",
                version = "1.0",
                description = "ECLO API 문서"
        )
)
public class SwaggerConfig {
}

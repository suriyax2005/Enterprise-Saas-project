package com.saas.saas.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class bootstrapping Swagger OpenAPI v3 documentation endpoints.
 * Mounts standard OpenAPI specs and exposes Bearer JWT authorization inputs.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Declares custom OpenAPI models, including server info and JWT auth keys.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Supply your Bearer Access Token (JWT) in the input block below to authorize requests.")
                        )
                )
                .info(new Info()
                        .title("Enterprise Multi-Tenant SaaS API")
                        .version("1.0.0")
                        .description("REST API specifications for the multi-tenant enterprise SaaS backend application. Covers Authentication, Audit logs, User directories, and Tenant management.")
                        .license(new License().name("Apache 2.0").url("https://springdoc.org"))
                );
    }
}

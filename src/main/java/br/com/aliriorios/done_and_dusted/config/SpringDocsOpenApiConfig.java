package br.com.aliriorios.done_and_dusted.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocsOpenApiConfig {
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("Security", securityScheme()))
                .info(
                        new Info()
                                .title("REST API - Done & Dusted")
                                .description("API for to do list project")
                                .version("v1")
                                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0"))
                                .contact(new Contact().name("Alirio Rios").email("contato.aliriorios@gmail.com"))
                );
    }

    // Enable a field to insert the JWT Token into Swagger-ui
    private SecurityScheme securityScheme () {
        return new SecurityScheme()
                .description("Enter a valid Bearer Token (JWT) to proceed")
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("Security");
    }
}

package com.poptsov.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Bank Card Management API",
                version = "1.0",
                description = "API for managing bank cards and transactions"
        ),
        servers = @Server(url = "/")
)
public class SwaggerConfig {

}
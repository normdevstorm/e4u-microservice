package com.renting.authentication_service.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info =@Info(
                title = "Authentication API",
                version = "1.0.0",
                contact = @Contact(
                        name = "normdevstorm", email = "normdevstorm@gmail.com"
                ),
                license = @License(
                        name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"
                ),
                description = "This api is for managing authentication and authorization in the renting application"
        ),
        servers = @Server(
                url = "${api.server.url}",
                description = "Development Server"
        )
)
public class OpenAPIConfiguration {
}

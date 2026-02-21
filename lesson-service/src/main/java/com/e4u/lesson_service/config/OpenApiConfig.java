package com.e4u.lesson_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

        @Value("${spring.application.name:Lesson Service}")
        private String applicationName;

        @Value("${api.server.url}")
        private String serverUrl;

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title(applicationName + " API")
                                                .version("1.0.0")
                                                .description("API documentation for Lesson Service - E4U Learning Platform")
                                                .contact(new Contact()
                                                                .name("E4U Team")
                                                                .email("support@e4u.com")
                                                                .url("https://e4u.com"))
                                                .license(new License()
                                                                .name("Apache 2.0")
                                                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                                .servers(List.of(
                                                new Server()
                                                                .url(serverUrl)));
        }
}

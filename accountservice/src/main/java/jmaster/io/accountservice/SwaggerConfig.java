package jmaster.io.accountservice;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

//    @Bean
//    public OpenAPI config() {
//        return new OpenAPI()
//                .addServersItem(serverInfo().description("User"));
//    }
//
//    // tuy bien swagger ui server url - via gateway
//    private Server serverInfo() {
//        return new Server()
//                .url("http://localhost:8080/user");
//    }
}
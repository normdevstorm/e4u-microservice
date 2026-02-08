package jmaster.io.gatewayservice;

import jmaster.io.gatewayservice.filter.AuthenticationFilter;
import jmaster.io.gatewayservice.filter.LoggingGatewayFilterFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class GatewayserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayserviceApplication.class, args);


    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOriginPattern("*");
//        corsConfig.addAllowedOrigin("http://localhost:3000");
//        corsConfig.addAllowedOrigin("http://localhost:5173");
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("PUT");
        corsConfig.addAllowedMethod("DELETE");
        corsConfig.addAllowedMethod("OPTIONS");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, LoggingGatewayFilterFactory loggingFactory, AuthenticationFilter authenticationFilter) {
        return builder.routes()
                .route("auth-route", r -> r.path("/auth/**")
                        .filters(f -> f.stripPrefix(1)
                                .filter(loggingFactory.apply(new LoggingGatewayFilterFactory.Config()))
//                                .circuitBreaker(c -> c.setName("CircuitBreaker")
//                                        .getFallbackUri())
                )
                        .uri("lb://authentication-service"))
                .route("item-route", r -> r.path("/item/**")
                        .filters(f -> f.stripPrefix(1)
                                .filter(loggingFactory.apply(new LoggingGatewayFilterFactory.Config()))
                                        .filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
//                                .circuitBreaker(c -> c.setName("CircuitBreaker")
//                                        .getFallbackUri())
                        )
                        .uri("lb://item-service"))
                .route("user-route", r -> r.path("/user/**")
                        .filters(f -> f.stripPrefix(1)
                                .filter(loggingFactory.apply(new LoggingGatewayFilterFactory.Config()))
                                .filter(authenticationFilter.apply(new AuthenticationFilter.Config()))
                                .circuitBreaker(c -> c.setName("CircuitBreaker")
                                        .getFallbackUri()))
                        .uri("lb://account-service"))

                .route("report-route", r -> r.path("/report/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://statistic-service"))

                .route("notification-route", r -> r.path("/notification/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://notification-service"))

                ///swagger ui
                .route("openapi", r -> r.path("/v3/api-docs/**")
                        .filters(f -> f.rewritePath("/v3/api-docs/(?<service>.*)", "/${service}/v3/api-docs"))
                        .uri("lb://gateway-service"))
                .build();
    }
}

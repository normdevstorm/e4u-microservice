package com.renting.authentication_service.config.security;

import com.renting.authentication_service.common.constants.ConstantManager;
import com.renting.authentication_service.config.security.filter.JwtAuthenticationFilter;
import com.renting.authentication_service.config.security.filter.LocalAuthenticationFilter;
import com.renting.authentication_service.repository.UserRepository;
import com.renting.authentication_service.service.JwtService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Log4j2
public class SecurityConfiguration {

    private final String[] WHITE_LIST = ConstantManager.URL_WHITE_LIST;
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtService jwtService;
    private final UserRepository userRepository;


    @Autowired
    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider,
                                 JwtService jwtService,
                                 UserRepository userRepository
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, LocalAuthenticationFilter localAuthenticationFilter) throws Exception {
        http.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()));
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers(WHITE_LIST).permitAll().anyRequest().authenticated());
//
//        http.exceptionHandling(ex -> ex
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            if (!response.isCommitted()) {
//                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                                response.getWriter().write("Unauthorized");
//                            }
//                        })
//                        .accessDeniedHandler((request, response, accessDeniedException) -> {
//                            if (!response.isCommitted()) {
//                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                                response.getWriter().write("Access Denied");
//                            }
//                        }));

                http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authenticationProvider(authenticationProvider)
                .addFilterBefore(localAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    LocalAuthenticationFilter localAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new LocalAuthenticationFilter(authenticationManager, jwtService, userRepository, ConstantManager.LOGIN_ENDPOINT);
    }
}

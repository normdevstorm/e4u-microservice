package jmaster.io.gatewayservice.config;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.Collections;

@Configuration
public class HttpMessageConvertersConfig {
    @Bean
    public HttpMessageConverters httpMessageConverters() {
        return new HttpMessageConverters(Collections.<HttpMessageConverter<?>>emptyList());
    }
}

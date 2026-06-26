package org.example.srm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.math.MathContext;
import java.math.RoundingMode;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public MathContext mathContext() {
        return new MathContext(10, RoundingMode.HALF_EVEN);
    }
}
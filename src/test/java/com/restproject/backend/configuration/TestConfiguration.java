package com.restproject.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@org.springframework.boot.test.context.TestConfiguration
public class TestConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

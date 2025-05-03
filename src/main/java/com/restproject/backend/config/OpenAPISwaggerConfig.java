package com.restproject.backend.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPISwaggerConfig {

    @Bean
    public GroupedOpenApi privateAdminApi() {
        return GroupedOpenApi.builder()
            .group("Private Admin APIs")
            .pathsToMatch("/api/private/admin/**")
            .build();
    }

    @Bean
    public GroupedOpenApi privateUserApi() {
        return GroupedOpenApi.builder()
            .group("Private User APIs")
            .pathsToMatch("/api/private/user/**")
            .build();
    }

    @Bean
    public GroupedOpenApi privateAuthApi() {
        return GroupedOpenApi.builder()
            .group("Private Auth APIs")
            .pathsToMatch("/api/private/auth/**")
            .build();
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("Public APIs")
            .pathsToMatch("/api/public/**")
            .build();
    }
}

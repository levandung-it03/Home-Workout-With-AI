package com.restproject.backend.config;

import com.cloudinary.Cloudinary;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloudinaryConfig {
    @Value("${services.cloudinary.cloud-name}")
    String CLOUD_NAME;
    @Value("${services.cloudinary.api-key}")
    String API_KEY;
    @Value("${services.cloudinary.api-secret}")
    String API_SECRET;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(Map.ofEntries(
            Map.entry("cloud_name", this.CLOUD_NAME),
            Map.entry("api_key", this.API_KEY),
            Map.entry("api_secret", this.API_SECRET)
        ));
    }

}

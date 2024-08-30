package com.restproject.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.dtos.reponse.ApiResponseObject;
import com.restproject.backend.enums.TokenTypes;
import com.restproject.backend.exceptions.ExpiredTokenException;
import com.restproject.backend.repositories.UserRepository;
import com.restproject.backend.services.Auth.InvalidTokenService;
import com.restproject.backend.services.Auth.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${services.security.secret-key}")
    private String SECRET_KEY;

    private final UserRepository userRepository;

    @Bean
    public SecretKeySpec mySecretKeySpec() {
        return new SecretKeySpec(SECRET_KEY.getBytes(), "HS512");
    }

    @Bean
    public PasswordEncoder userPasswordEncoder() {
        return new BCryptPasswordEncoder(8, new SecureRandom());
    }

    @Bean
    public PasswordEncoder oauthClientPasswordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return (username) -> userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid username"));
    }
}

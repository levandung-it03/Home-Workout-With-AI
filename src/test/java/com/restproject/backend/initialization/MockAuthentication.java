package com.restproject.backend.initialization;

import com.nimbusds.jwt.JWTClaimsSet;
import com.restproject.backend.services.Auth.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MockAuthentication {
    @Autowired
    JwtService jwtService;

    public String[] generateJwtTokens() {
        String accessToken = new NimbusJwtEncoder();

        return new String[] {

        };
    }
}

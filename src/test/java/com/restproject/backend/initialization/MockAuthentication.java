package com.restproject.backend.initialization;

import com.restproject.backend.entities.Auth.Authority;
import com.restproject.backend.entities.Auth.User;
import com.restproject.backend.services.Auth.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MockAuthentication {
    @Autowired
    JwtService jwtService;
    @Autowired
    PasswordEncoder userPasswordEncoder;

    public HashMap<String, String> generateJwtTokens() {
        var authorities = List.of(
            Authority.builder().authorityName("ROLE_ADMIN").build(),
            Authority.builder().authorityName("ROLE_USER").build()
        );
        var users = List.of(
            User.builder()
                .username("root")
                .password(userPasswordEncoder.encode("rootroot"))
                .authorities(List.of(authorities.getFirst()))
                .createdTime(LocalDateTime.now())
                .active(true)
                .build(),
            User.builder()
                .username("user")
                .password(userPasswordEncoder.encode("useruser"))
                .authorities(List.of(authorities.getLast()))
                .createdTime(LocalDateTime.now())
                .active(true)
                .build()
        );
        var result = new HashMap<String, String>();
        result.put("root_accessToken", jwtService.generateAccessToken(users.getFirst()).get("token"));
        result.put("root_refreshToken", jwtService.generateAccessToken(users.getFirst()).get("token"));
        result.put("user_accessToken", jwtService.generateAccessToken(users.getLast()).get("token"));
        result.put("user_refreshToken", jwtService.generateAccessToken(users.getLast()).get("token"));
        return result;
    }
}

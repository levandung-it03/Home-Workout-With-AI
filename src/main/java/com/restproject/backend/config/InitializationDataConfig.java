package com.restproject.backend.config;

import com.restproject.backend.entities.Auth.Authority;
import com.restproject.backend.entities.Auth.User;
import com.restproject.backend.entities.UserInfo;
import com.restproject.backend.enums.Gender;
import com.restproject.backend.repositories.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InitializationDataConfig implements CommandLineRunner {
    AuthorityRepository authorityRepository;
    UserRepository userRepository;
    PasswordEncoder userPasswordEncoder;
    InvalidTokenCrud invalidTokenCrud;
    RefreshTokenCrud refreshTokenCrud;

    @Override
    public void run(String... args) {
        if (authorityRepository.count() == 0)   authorityRepository.saveAll(List.of(
            Authority.builder().authorityName("ROLE_ADMIN").build(),
            Authority.builder().authorityName("ROLE_USER").build()
        ));
        if (userRepository.count() == 0) {
            List<Authority> authorities = authorityRepository.findAll();
            userRepository.saveAll(List.of(
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
            ));
        }
        invalidTokenCrud.deleteAll();
        refreshTokenCrud.deleteAll();
    }
}

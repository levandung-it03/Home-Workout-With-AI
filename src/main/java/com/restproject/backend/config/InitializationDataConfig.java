package com.restproject.backend.config;

import com.restproject.backend.entities.Auth.Authority;
import com.restproject.backend.entities.Auth.User;
import com.restproject.backend.repositories.AuthorityRepository;
import com.restproject.backend.repositories.InvalidTokenCrud;
import com.restproject.backend.repositories.RefreshTokenCrud;
import com.restproject.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitializationDataConfig implements CommandLineRunner {
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder userPasswordEncoder;
    private final InvalidTokenCrud invalidTokenCrud;
    private final RefreshTokenCrud refreshTokenCrud;

    @Override
    public void run(String... args) throws Exception {
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

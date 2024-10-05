package com.restproject.backend.repositories;

import com.restproject.backend.config.RedisConfig;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.PageEnum;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@DataJpaTest
@Import(RedisConfig.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionRepositoryTest {
    @Autowired
    SessionRepository sessionRepository;

    @Test
    public void save_admin_duplicatedUniqueConstraint() {
        sessionRepository.save(Session.builder().name("Push-ups").levelEnum(Level.INTERMEDIATE).description("a").build());
        assertThrows(DataIntegrityViolationException.class, () -> sessionRepository
            .save(Session.builder().name("Push-ups").levelEnum(Level.INTERMEDIATE).description("a").build()));
    }
}

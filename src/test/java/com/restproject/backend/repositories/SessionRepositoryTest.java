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
    public void findAllByFilteringSession_admin_valid() {
        var pageable = PageRequest.of(0, PageEnum.SIZE.getSize());
        var request = Session.builder().name("Strength").level(Level.BEGINNER).build();
        var sessions = List.of(
            Session.builder().name("Morning Yoga").level(Level.BEGINNER)
                .description("Relaxing yoga session").build(),
            Session.builder().name("Leg Day").level(Level.INTERMEDIATE)
                .description("Strength-focused leg workout").build(),
            Session.builder().name("Upper Body Strength").level(Level.ADVANCE)
                .description("Intensive upper body workout").build(),
            Session.builder().name("HIIT Blast").level(Level.ADVANCE)
                .description("High-intensity interval training").build(),
            Session.builder().name("Stretch and Mobility").level(Level.BEGINNER)
                .description("Improve flexibility and mobility").build(),
            Session.builder().name("Core Power").level(Level.INTERMEDIATE)
                .description("Core strength exercises").build(),
            Session.builder().name("Full Body Burn").level(Level.INTERMEDIATE)
                .description("Burn calories with a full-body workout").build(),
            Session.builder().name("Endurance Challenge").level(Level.ADVANCE)
                .description("Push your endurance to the limit").build(),
            Session.builder().name("Push-Pull Workout").level(Level.INTERMEDIATE)
                .description("Balanced push-pull routine").build(),
            Session.builder().name("Cardio Blitz").level(Level.BEGINNER)
                .description("Cardio-focused session").build(),
            Session.builder().name("Strength Circuit").level(Level.ADVANCE)
                .description("Circuit training for strength").build(),
            Session.builder().name("Pilates Flow Strength").level(Level.BEGINNER)
                .description("Pilates-inspired flow").build(),
            Session.builder().name("Sprint Intervals").level(Level.INTERMEDIATE)
                .description("High-intensity sprint workout").build(),
            Session.builder().name("Powerlifting Basics").level(Level.INTERMEDIATE)
                .description("Introduction to powerlifting").build(),
            Session.builder().name("Bodyweight Mastery").level(Level.ADVANCE)
                .description("Master bodyweight exercises").build()
        );
        sessionRepository.saveAll(sessions);

        Page<Session> repoRes = sessionRepository.findAllByFilteringSession(request, pageable);

        repoRes.stream().forEach(found -> {
            assertTrue(found.getName().contains(request.getName()));
            assertSame(found.getLevel(), request.getLevel());
        });
    }
}

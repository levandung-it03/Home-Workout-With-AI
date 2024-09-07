package com.restproject.backend.repositories;

import com.restproject.backend.config.RedisConfig;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.ExercisesOfSessions;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

@DataJpaTest
@Import(RedisConfig.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExercisesOfSessionsRepositoryTest {
    @Autowired
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    ExerciseRepository exerciseRepository;

    @Test
    public void findAllBySessionSessionId_admin_valid() {
        Pageable pgb = PageRequest.of(0, PageEnum.SIZE.getSize());
        Session savedSession = sessionRepository.save(Session.builder().level(Level.INTERMEDIATE).name("Push-ups")
            .muscleList("Chest, Triceps").description("None of description").build());
        Exercise savedExercise1 = exerciseRepository.save(Exercise.builder().name("Push-ups").level(Level.INTERMEDIATE)
            .basicReps(14).build());
        Exercise savedExercise2 = exerciseRepository.save(Exercise.builder().name("Diamond push-ups").level(Level.INTERMEDIATE)
            .basicReps(14).build());
        var exercises = List.of(
            ExercisesOfSessions.builder().session(savedSession).exercise(savedExercise1).build(),
            ExercisesOfSessions.builder().session(savedSession).exercise(savedExercise2).build()
        );
        //--make sure the id created with the declared order.
        exercises.forEach(e -> exercisesOfSessionsRepository.save(e));
        Page<ExercisesOfSessions> repoRes = exercisesOfSessionsRepository
            .findAllBySessionSessionId(savedSession.getSessionId(), pgb);
        ArrayList<ExercisesOfSessions> actual = new ArrayList<>(repoRes.stream().toList());
        actual.sort(Comparator.comparing(ExercisesOfSessions::getId));

        assertNotNull(actual);
        assertEquals(exercises.size(), actual.size());
        for (int ind = 0; ind < actual.size(); ind++) {
            assertEquals(actual.get(ind).getId(), exercises.get(ind).getId());
            assertEquals(actual.get(ind).getExercise().getExerciseId(),
                exercises.get(ind).getExercise().getExerciseId());
            assertEquals(actual.get(ind).getSession().getSessionId(),
                exercises.get(ind).getSession().getSessionId());
        }
    }
}

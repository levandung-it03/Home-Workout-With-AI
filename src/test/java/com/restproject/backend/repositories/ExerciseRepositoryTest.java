package com.restproject.backend.repositories;

import com.restproject.backend.annotations.dev.CoreEngines;
import com.restproject.backend.config.RedisConfig;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.enums.Level;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@Import(RedisConfig.class)
@TestPropertySource("/test.yaml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseRepositoryTest {
    @Autowired
    ExerciseRepository exerciseRepository;

    @CoreEngines
    public Exercise getStereo() {
        return Exercise.builder().name("Push-ups").basicReps(14).levelEnum(Level.INTERMEDIATE).build();
    }

    @Test
    public void save_admin_constraintViolation() {
        Exercise stereo = this.getStereo();
        Exercise saveExercise = exerciseRepository.save(stereo);

        assertEquals(stereo.getName(), saveExercise.getName());
        assertEquals(stereo.getLevelEnum(), saveExercise.getLevelEnum());
        assertEquals(stereo.getBasicReps(), saveExercise.getBasicReps());

        Exercise duplicated = this.getStereo();
        assertThrows(DataIntegrityViolationException.class, () -> exerciseRepository.save(duplicated));
    }
}

package com.restproject.backend.repositories;

import com.restproject.backend.config.RedisConfig;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.MusclesOfExercises;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@DataJpaTest
@Import(RedisConfig.class)
@TestPropertySource("/test.yaml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MusclesOfExercisesRepositoryTest {
    @Autowired
    MusclesOfExercisesRepository musclesOfExercisesRepository;
    @Autowired
    ExerciseRepository exerciseRepository;

    @Test
    public void findAllExercisesByLevelAndMuscles_admin_valid() {
        List<Muscle> reqMuscles = List.of(Muscle.CHEST, Muscle.TRICEPS);
        Level reqLevel = Level.IMMEDIATE;

        var saveExercises = exerciseRepository.saveAll(List.of(
            Exercise.builder().name("Push-ups").level(reqLevel).basicReps(14).build(),
            Exercise.builder().name("Diamond push-ups").level(reqLevel).basicReps(10).build()
        ));
        musclesOfExercisesRepository.saveAll(List.of(
            MusclesOfExercises.builder().muscle(reqMuscles.getFirst()).exercise(saveExercises.get(0)).build(),
            MusclesOfExercises.builder().muscle(reqMuscles.getFirst()).exercise(saveExercises.get(1)).build(),
            MusclesOfExercises.builder().muscle(reqMuscles.getLast()).exercise(saveExercises.get(1)).build()
        ));

        var queriedResult = musclesOfExercisesRepository.findAllExercisesByLevelAndMuscles(reqLevel, reqMuscles);

        assertArrayEquals(queriedResult.toArray(), saveExercises.toArray());
    }
}

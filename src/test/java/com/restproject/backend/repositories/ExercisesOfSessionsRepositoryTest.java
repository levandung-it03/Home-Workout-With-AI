package com.restproject.backend.repositories;

import com.restproject.backend.config.RedisConfig;
import com.restproject.backend.dtos.response.ExerciseHasMusclesResponse;
import com.restproject.backend.dtos.response.ExercisesOfSessionResponse;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.ExercisesOfSessions;
import com.restproject.backend.entities.MusclesOfExercises;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.enums.PageEnum;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
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
    MusclesOfExercisesRepository musclesOfExercisesRepository;
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    ExerciseRepository exerciseRepository;

    /**
     * RULES:
     * 1. All Exercises, which are at Beginner Level, belong to Session of request (by req.sessionId) for fast testing.
     */
    @Test
    public void findAllExercisesHasMusclesBySessionId_admin_valid() {
        var sessionRequest = sessionRepository.save(Session.builder().level(Level.BEGINNER).name("Session 2: Chest,...")
            .description("This is Session").build());
        var exercises = new ArrayList<>(exerciseRepository.saveAll(List.of(
            Exercise.builder().name("Push Up").level(Level.BEGINNER).basicReps(15).build(),
            Exercise.builder().name("Bicep Curl").level(Level.INTERMEDIATE).basicReps(12).build(),
            Exercise.builder().name("Triceps Dip").level(Level.INTERMEDIATE).basicReps(10).build(),
            Exercise.builder().name("Squat").level(Level.ADVANCE).basicReps(20).build(),
            Exercise.builder().name("Plank").level(Level.BEGINNER).basicReps(30).build(),
            Exercise.builder().name("Jump Squat").level(Level.ADVANCE).basicReps(20).build(),
            Exercise.builder().name("One Leg Squat").level(Level.ADVANCE).basicReps(20).build()
        )));
        //--All of Beginner Level exercises belong to session (with just testing data)
        var exercisesSessionRelationship = exercises.stream().filter(e -> e.getLevel().equals(Level.BEGINNER)).toList();
        exercisesOfSessionsRepository.saveAll(exercisesSessionRelationship.stream().map(exe ->
            ExercisesOfSessions.builder().exercise(exe).session(sessionRequest).build()).toList());
        var exerciseHasMusclesFromDB = new ArrayList<>(musclesOfExercisesRepository.saveAll(
            List.of(
                MusclesOfExercises.builder().exercise(exercises.get(0)).muscle(Muscle.CHEST).build(),
                MusclesOfExercises.builder().exercise(exercises.get(1)).muscle(Muscle.BICEPS).build(),
                MusclesOfExercises.builder().exercise(exercises.get(2)).muscle(Muscle.LEG).build(),
                MusclesOfExercises.builder().exercise(exercises.get(3)).muscle(Muscle.BACK_LATS).build(),
                MusclesOfExercises.builder().exercise(exercises.get(0)).muscle(Muscle.TRICEPS).build(),
                MusclesOfExercises.builder().exercise(exercises.get(1)).muscle(Muscle.BACK_LATS).build(),
                MusclesOfExercises.builder().exercise(exercises.get(2)).muscle(Muscle.ABS).build(),
                MusclesOfExercises.builder().exercise(exercises.get(3)).muscle(Muscle.BICEPS).build(),
                MusclesOfExercises.builder().exercise(exercises.get(1)).muscle(Muscle.ABS).build(),
                MusclesOfExercises.builder().exercise(exercises.get(4)).muscle(Muscle.ABS).build(),
                MusclesOfExercises.builder().exercise(exercises.get(5)).muscle(Muscle.ABS).build(),
                MusclesOfExercises.builder().exercise(exercises.get(6)).muscle(Muscle.ABS).build()
            )));
        var exercisesHasMusclesRes = new ArrayList<>(exercises.stream().map(exercise ->
            ExercisesOfSessionResponse.builder().exercise(exercise).muscleList(new ArrayList<>())
                //--All of Beginner Level exercises belong to session (with just testing data)
                .withCurrentSession(exercise.getLevel().equals(Level.BEGINNER)).build()).toList());
        for (MusclesOfExercises exeHasMusDB : exerciseHasMusclesFromDB) {
            for (ExercisesOfSessionResponse exeHasMusRes : exercisesHasMusclesRes) {
                if (exeHasMusRes.getExercise().getExerciseId().equals(exeHasMusDB.getExercise().getExerciseId()))
                    exeHasMusRes.getMuscleList().add(exeHasMusDB.getMuscle());
            }
        }

        ArrayList<ExercisesOfSessionResponse> actual = new ArrayList<>(exercisesOfSessionsRepository
            .findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
                sessionRequest.getSessionId(),
                PageRequest.of(0, PageEnum.SIZE.getSize())
            ).toList());

        assertNotNull(actual);
        actual.sort(Comparator.comparing(exe -> exe.getExercise().getExerciseId()));
        exercisesHasMusclesRes.sort(Comparator.comparing(exe -> exe.getExercise().getExerciseId()));
        for (int index = 0; index < actual.size(); index++) {
            var expectExe = exercisesHasMusclesRes.get(index).getExercise();
            var actualExe = actual.get(index).getExercise();
            long totalMuscle = exerciseHasMusclesFromDB.stream().filter(exeHasMusDB ->
                exeHasMusDB.getExercise().getExerciseId().equals(expectExe.getExerciseId())).count();

            assertEquals(expectExe.getName(), actualExe.getName());
            assertEquals(expectExe.getLevel(), actualExe.getLevel());
            assertEquals(expectExe.getBasicReps(), actualExe.getBasicReps());
            assertEquals(totalMuscle, actual.get(index).getMuscleList().size());
        }
        assertEquals(
            actual.stream().filter(ExercisesOfSessionResponse::isWithCurrentSession).count(),
            exercisesHasMusclesRes.stream().filter(ExercisesOfSessionResponse::isWithCurrentSession).count());
    }
}

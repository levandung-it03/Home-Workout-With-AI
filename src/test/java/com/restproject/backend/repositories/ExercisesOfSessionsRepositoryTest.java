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
    public void findAllExercisesHasMusclesPrioritizeRelationshipBySessionId_admin_valid() {
        var sessionRequest = sessionRepository.save(Session.builder().levelEnum(Level.BEGINNER).name("Session 2: Chest,...")
            .description("This is Session").build());
        var exercises = new ArrayList<>(exerciseRepository.saveAll(List.of(
            Exercise.builder().name("Push Up").levelEnum(Level.BEGINNER).basicReps(15).build(),
            Exercise.builder().name("Bicep Curl").levelEnum(Level.INTERMEDIATE).basicReps(12).build(),
            Exercise.builder().name("Triceps Dip").levelEnum(Level.INTERMEDIATE).basicReps(10).build(),
            Exercise.builder().name("Squat").levelEnum(Level.ADVANCE).basicReps(20).build(),
            Exercise.builder().name("Plank").levelEnum(Level.BEGINNER).basicReps(30).build(),
            Exercise.builder().name("Jump Squat").levelEnum(Level.ADVANCE).basicReps(20).build(),
            Exercise.builder().name("One Leg Squat").levelEnum(Level.ADVANCE).basicReps(20).build()
        )));
        //--All of Beginner Level exercises belong to session (with just testing data)
        var exercisesSessionRelationship = exercises.stream().filter(e -> e.getLevelEnum().equals(Level.BEGINNER)).toList();
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
            ExercisesOfSessionResponse.builder()
                .exerciseId(exercise.getExerciseId())
                .name(exercise.getName())
                .basicReps(exercise.getBasicReps())
                .levelEnum(exercise.getLevelEnum()) //--All Beginner exercise belongs to session by default.
                .withCurrentSession(exercise.getLevelEnum().equals(Level.BEGINNER))
                .muscleList(new ArrayList<>())
                //--All of Beginner Level exercises belong to session (with just testing data)
                .withCurrentSession(exercise.getLevelEnum().equals(Level.BEGINNER)).build()).toList());
        for (MusclesOfExercises exeHasMusDB : exerciseHasMusclesFromDB) {
            for (ExercisesOfSessionResponse exeHasMusRes : exercisesHasMusclesRes) {
                if (exeHasMusRes.getExerciseId().equals(exeHasMusDB.getExercise().getExerciseId()))
                    exeHasMusRes.getMuscleList().add(exeHasMusDB.getMuscle().toString());
            }
        }

        List<Object[]> repoRes = exercisesOfSessionsRepository
            .findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
                sessionRequest.getSessionId(),
                PageRequest.of(0, PageEnum.SIZE.getSize())
            ).stream().toList();
        ArrayList<ExercisesOfSessionResponse> actual = new ArrayList<>(repoRes
            .stream().map(ExercisesOfSessionResponse::buildFromNativeQuery)
            .toList());

        assertNotNull(actual);
        actual.sort(Comparator.comparing(ExercisesOfSessionResponse::getExerciseId));
        exercisesHasMusclesRes.sort(Comparator.comparing(ExercisesOfSessionResponse::getExerciseId));
        for (int index = 0; index < actual.size(); index++) {
            var expectExe = exercisesHasMusclesRes.get(index);
            var actualExe = actual.get(index);
            long totalMuscle = exerciseHasMusclesFromDB.stream().filter(exeHasMusDB ->
                exeHasMusDB.getExercise().getExerciseId().equals(expectExe.getExerciseId())).count();

            assertEquals(expectExe.getName(), actualExe.getName());
            assertEquals(expectExe.getLevelEnum(), actualExe.getLevelEnum());
            assertEquals(expectExe.getBasicReps(), actualExe.getBasicReps());
            assertEquals(totalMuscle, actual.get(index).getMuscleList().size());
        }
        assertEquals(
            actual.stream().filter(ExercisesOfSessionResponse::isWithCurrentSession).count(),
            exercisesHasMusclesRes.stream().filter(ExercisesOfSessionResponse::isWithCurrentSession).count());
    }

    /**
     * RULES:
     * 1. All Exercises, which are at Beginner Level, belong to Session of request (by req.sessionId) for fast testing.
     */
    @Test
    public void findAllExercisesHasMusclesPrioritizeRelationshipBySessionId_admin_validWithFiltering() {
        var req = ExercisesOfSessionResponse.builder().name("p").muscleList(new ArrayList<>()).build();
        var sessionRequest = sessionRepository.save(Session.builder().levelEnum(Level.BEGINNER).name("Session 2: Chest,...")
            .description("This is Session").build());
        var exercises = new ArrayList<>(exerciseRepository.saveAll(List.of(
            Exercise.builder().name("Push Up").levelEnum(Level.BEGINNER).basicReps(15).build(),
            Exercise.builder().name("Bicep Curl").levelEnum(Level.INTERMEDIATE).basicReps(12).build(),
            Exercise.builder().name("Triceps Dip").levelEnum(Level.INTERMEDIATE).basicReps(10).build(),
            Exercise.builder().name("Squat").levelEnum(Level.ADVANCE).basicReps(20).build(),
            Exercise.builder().name("Plank").levelEnum(Level.BEGINNER).basicReps(30).build(),
            Exercise.builder().name("Jump Squat").levelEnum(Level.ADVANCE).basicReps(20).build(),
            Exercise.builder().name("One Leg Squat").levelEnum(Level.ADVANCE).basicReps(20).build()
        )));
        //--All of Beginner Level exercises belong to session (with just testing data)
        var exercisesSessionRelationship = exercises.stream().filter(e -> e.getLevelEnum().equals(Level.BEGINNER)).toList();
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
        var exercisesHasMusclesRes = new ArrayList<>(exercises.stream()
            .filter(e -> e.getName().toUpperCase().contains("P"))
            .map(exercise ->
                ExercisesOfSessionResponse.builder()
                    .exerciseId(exercise.getExerciseId())
                    .name(exercise.getName())
                    .basicReps(exercise.getBasicReps())
                    .levelEnum(exercise.getLevelEnum()) //--All Beginner exercise belongs to session by default.
                    .withCurrentSession(exercise.getLevelEnum().equals(Level.BEGINNER))
                    .muscleList(new ArrayList<>())
                //--All of Beginner Level exercises belong to session (with just testing data)
                .withCurrentSession(exercise.getLevelEnum().equals(Level.BEGINNER)).build()).toList());
        for (MusclesOfExercises exeHasMusDB : exerciseHasMusclesFromDB) {
            for (ExercisesOfSessionResponse exeHasMusRes : exercisesHasMusclesRes) {
                if (exeHasMusRes.getExerciseId().equals(exeHasMusDB.getExercise().getExerciseId()))
                    exeHasMusRes.getMuscleList().add(exeHasMusDB.getMuscle().toString());
            }
        }

        List<Object[]> repoRes = exercisesOfSessionsRepository
            .findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
                sessionRequest.getSessionId(), req,
                PageRequest.of(0, PageEnum.SIZE.getSize())
            ).stream().toList();
        ArrayList<ExercisesOfSessionResponse> actual = new ArrayList<>(repoRes
            .stream().map(ExercisesOfSessionResponse::buildFromNativeQuery)
            .toList());

        assertNotNull(actual);
        actual.sort(Comparator.comparing(ExercisesOfSessionResponse::getExerciseId));
        exercisesHasMusclesRes.sort(Comparator.comparing(ExercisesOfSessionResponse::getExerciseId));
        for (int index = 0; index < actual.size(); index++) {
            var actualExe = actual.get(index);
            var expectExe = exercisesHasMusclesRes.get(index);

            assertEquals(expectExe.getName(), actualExe.getName());
            assertEquals(expectExe.getLevelEnum(), actualExe.getLevelEnum());
            assertEquals(expectExe.getBasicReps(), actualExe.getBasicReps());
            assertEquals(expectExe.getMuscleList().size(), actualExe.getMuscleList().size());
            assertTrue(expectExe.getMuscleList().containsAll(actualExe.getMuscleList()));
        }
        assertEquals(
            actual.stream().filter(ExercisesOfSessionResponse::isWithCurrentSession).count(),
            exercisesHasMusclesRes.stream().filter(ExercisesOfSessionResponse::isWithCurrentSession).count());
    }

    /**
     * RULES:
     * 1. All Exercises, which are at Beginner Level, belong to Session of request (by req.sessionId) for fast testing.
     */
    @Test
    public void findAllExercisesHasMusclesPrioritizeRelationshipBySessionId_admin_validWithFilteringAndMuscles() {
        var req = ExercisesOfSessionResponse.builder()
            .muscleList(new ArrayList<>(List.of(Muscle.ABS.toString(), Muscle.BICEPS.toString()))).build();
        var sessionRequest = sessionRepository.save(Session.builder().levelEnum(Level.BEGINNER).name("Session 2: Chest,...")
            .description("This is Session").build());
        var exercises = new ArrayList<>(exerciseRepository.saveAll(List.of(
            Exercise.builder().name("Push Up").levelEnum(Level.BEGINNER).basicReps(15).build(),
            Exercise.builder().name("Bicep Curl").levelEnum(Level.INTERMEDIATE).basicReps(12).build(),
            Exercise.builder().name("Triceps Dip").levelEnum(Level.INTERMEDIATE).basicReps(10).build(),
            Exercise.builder().name("Squat").levelEnum(Level.ADVANCE).basicReps(20).build(),
            Exercise.builder().name("Plank").levelEnum(Level.BEGINNER).basicReps(30).build(),
            Exercise.builder().name("Jump Squat").levelEnum(Level.ADVANCE).basicReps(20).build(),
            Exercise.builder().name("One Leg Squat").levelEnum(Level.ADVANCE).basicReps(20).build()
        )));
        //--All of Beginner Level exercises belong to session (with just testing data)
        var exercisesSessionRelationship = exercises.stream().filter(e -> e.getLevelEnum().equals(Level.BEGINNER)).toList();
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
        var exercisesHasMusclesRes = new LinkedHashMap<Long, ExercisesOfSessionResponse>();
        var removedIds = new ArrayList<Long>();
        for (MusclesOfExercises exeHasMusDB : exerciseHasMusclesFromDB) {
            if (!exercisesHasMusclesRes.containsKey(exeHasMusDB.getExercise().getExerciseId())) {
                exercisesHasMusclesRes.put(
                    exeHasMusDB.getExercise().getExerciseId(),
                    ExercisesOfSessionResponse.builder()
                        .exerciseId(exeHasMusDB.getExercise().getExerciseId())
                        .name(exeHasMusDB.getExercise().getName())
                        .basicReps(exeHasMusDB.getExercise().getBasicReps())
                        .levelEnum(exeHasMusDB.getExercise().getLevelEnum()) //--All Beginner exercise belongs to session by default.
                        .withCurrentSession(exeHasMusDB.getExercise().getLevelEnum().equals(Level.BEGINNER))
                        .muscleList(new ArrayList<>(List.of(exeHasMusDB.getMuscle().toString())))
                        //--All of Beginner Level exercises belong to session (with just testing data)
                        .withCurrentSession(exeHasMusDB.getExercise().getLevelEnum().equals(Level.BEGINNER)).build()
                );
            } else {
                exercisesHasMusclesRes.get(exeHasMusDB.getExercise().getExerciseId())
                    .getMuscleList().add(exeHasMusDB.getMuscle().toString());
            }
        }
        for (Long exerciseId : exercisesHasMusclesRes.keySet()) {
            if (exercisesHasMusclesRes.get(exerciseId).getMuscleList()
                .stream().noneMatch(m -> req.getMuscleList().contains(m)))
                removedIds.add(exercisesHasMusclesRes.get(exerciseId).getExerciseId());
        }
        removedIds.forEach(exercisesHasMusclesRes::remove);

        List<Object[]> repoRes = exercisesOfSessionsRepository
            .findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
                sessionRequest.getSessionId(), req,
                PageRequest.of(0, PageEnum.SIZE.getSize())
            ).stream().toList();
        ArrayList<ExercisesOfSessionResponse> actual = new ArrayList<>(repoRes
            .stream().map(ExercisesOfSessionResponse::buildFromNativeQuery)
            .toList());

        assertNotNull(actual);
        actual.sort(Comparator.comparing(ExercisesOfSessionResponse::getExerciseId));
        for (int index = 0; index < actual.size(); index++) {
            var actualExe = actual.get(index);
            var expectExe = exercisesHasMusclesRes.get(actualExe.getExerciseId());

            assertEquals(expectExe.getName(), actualExe.getName());
            assertEquals(expectExe.getLevelEnum(), actualExe.getLevelEnum());
            assertEquals(expectExe.getBasicReps(), actualExe.getBasicReps());
            assertEquals(expectExe.getMuscleList().size(), actualExe.getMuscleList().size());
            assertTrue(expectExe.getMuscleList().containsAll(actualExe.getMuscleList()));
        }
        assertEquals(
            actual.stream().filter(ExercisesOfSessionResponse::isWithCurrentSession).count(),
            exercisesHasMusclesRes.values().stream().filter(ExercisesOfSessionResponse::isWithCurrentSession).count());
    }
}

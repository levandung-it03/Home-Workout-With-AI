//package com.restproject.backend.repositories;
//
//import com.restproject.backend.config.RedisConfig;
//import com.restproject.backend.dtos.response.ExerciseHasMusclesResponse;
//import com.restproject.backend.entities.Exercise;
//import com.restproject.backend.entities.MusclesOfExercises;
//import com.restproject.backend.enums.Level;
//import com.restproject.backend.enums.Muscle;
//import com.restproject.backend.mappers.ExerciseMappers;
//import lombok.AccessLevel;
//import lombok.experimental.FieldDefaults;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.*;
//
//@DataJpaTest
//@Import(RedisConfig.class)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@ActiveProfiles("test")
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class MusclesOfExercisesRepositoryTest {
//    @Autowired
//    MusclesOfExercisesRepository musclesOfExercisesRepository;
//    @Autowired
//    ExerciseRepository exerciseRepository;
//
//    @Test
//    public void findAllExercisesHasMuscles_admin_valid() {
//        ArrayList<Exercise> exercises = new ArrayList<>(exerciseRepository.saveAll(List.of(
//            Exercise.builder().name("Push Up").levelEnum(Level.BEGINNER).basicReps(15).build(),
//            Exercise.builder().name("Bicep Curl").levelEnum(Level.INTERMEDIATE).basicReps(12).build(),
//            Exercise.builder().name("Tricep Dip").levelEnum(Level.INTERMEDIATE).basicReps(10).build(),
//            Exercise.builder().name("Squat").levelEnum(Level.ADVANCE).basicReps(20).build(),
//            Exercise.builder().name("Plank").levelEnum(Level.BEGINNER).basicReps(30).build()
//        )));
//        var exerciseHasMusclesFromDB = new ArrayList<>(musclesOfExercisesRepository.saveAll(List.of(
//            MusclesOfExercises.builder().exercise(exercises.get(0)).muscle(Muscle.CHEST).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(1)).muscle(Muscle.BICEPS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(2)).muscle(Muscle.LEG).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(3)).muscle(Muscle.BACK_LATS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(0)).muscle(Muscle.TRICEPS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(1)).muscle(Muscle.BACK_LATS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(2)).muscle(Muscle.ABS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(3)).muscle(Muscle.BICEPS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(1)).muscle(Muscle.ABS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(4)).muscle(Muscle.ABS).build()
//        )));
//        var exercisesHasMusclesRes = new ArrayList<>(exercises.stream()
//            .map(e -> ExerciseHasMusclesResponse.builder()
//                .exerciseId(e.getExerciseId()).name(e.getName()).basicReps(e.getBasicReps()).levelEnum(e.getLevelEnum().toString())
//                .muscleList(new ArrayList<>())
//                .build()).toList());
//        for (MusclesOfExercises exeHasMusDB : exerciseHasMusclesFromDB) {
//            for (ExerciseHasMusclesResponse exeHasMusRes : exercisesHasMusclesRes) {
//                if (exeHasMusRes.getExerciseId().equals(exeHasMusDB.getExercise().getExerciseId()))
//                    exeHasMusRes.getMuscleList().add(exeHasMusDB.getMuscle().toString());
//            }
//        }
//
//
//        ArrayList<ExerciseHasMusclesResponse> actual = new ArrayList<>(musclesOfExercisesRepository
//            .findAllExercisesHasMuscles(PageRequest.of(0, 10))
//            .stream().map(ExerciseHasMusclesResponse::buildFromNativeQuery).toList());
//
//        assertNotNull(actual);
//        assertEquals(exercisesHasMusclesRes.size(), actual.size());
//        actual.sort(Comparator.comparing(ExerciseHasMusclesResponse::getExerciseId));
//        exercisesHasMusclesRes.sort(Comparator.comparing(ExerciseHasMusclesResponse::getExerciseId));
//        for (int index = 0; index < 4; index++) {
//            var expectExe = exercisesHasMusclesRes.get(index);
//            var actualExe = actual.get(index);
//            long totalMuscle = exerciseHasMusclesFromDB.stream().filter(exeHasMusDB ->
//                exeHasMusDB.getExercise().getExerciseId().equals(expectExe.getExerciseId())).count();
//
//            assertEquals(expectExe.getName(), actualExe.getName());
//            assertEquals(expectExe.getLevelEnum(), actualExe.getLevelEnum());
//            assertEquals(expectExe.getBasicReps(), actualExe.getBasicReps());
//            assertEquals(totalMuscle, actual.get(index).getMuscleList().size());
//        }
//    }
//
//    @Test
//    public void findAllExercisesHasMuscles_admin_validWithFiltering() {
//        var request = ExerciseHasMusclesResponse.builder()
//            .muscleList(List.of(Muscle.ABS.toString(), Muscle.BACK_LATS.toString()))
//            .build();
//        var exercises = new ArrayList<>(exerciseRepository.saveAll(List.of(
//            Exercise.builder().levelEnum(Level.BEGINNER).name("Test Exercise1").basicReps(14).imageUrl("hehe").build(),
//            Exercise.builder().levelEnum(Level.INTERMEDIATE).name("Test Exercise02").basicReps(13).imageUrl("hehe").build(),
//            Exercise.builder().levelEnum(Level.INTERMEDIATE).name("Test Exercise03").basicReps(12).imageUrl("hehe").build(),
//            Exercise.builder().levelEnum(Level.ADVANCE).name("Test Exercise4").basicReps(10).imageUrl("hehe").build(),
//            Exercise.builder().levelEnum(Level.BEGINNER).name("Test Exercise5").basicReps(14).imageUrl("hehe").build()
//        )));
//        var exerciseHasMusclesFromDB = new ArrayList<>(musclesOfExercisesRepository.saveAll(List.of(
//            MusclesOfExercises.builder().exercise(exercises.get(0)).muscle(Muscle.CHEST).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(1)).muscle(Muscle.BICEPS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(2)).muscle(Muscle.BACK_LATS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(3)).muscle(Muscle.BACK_LATS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(0)).muscle(Muscle.TRICEPS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(1)).muscle(Muscle.BACK_LATS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(2)).muscle(Muscle.ABS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(3)).muscle(Muscle.BICEPS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(1)).muscle(Muscle.ABS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(4)).muscle(Muscle.ABS).build()
//        )));
//        var exercisesHasMusclesRes = new LinkedHashMap<Long, ExerciseHasMusclesResponse>();
//        var removedIds = new LinkedHashSet<Long>();
//        for (MusclesOfExercises sesHasMusDB : exerciseHasMusclesFromDB) {
//            if (exercisesHasMusclesRes.containsKey(sesHasMusDB.getExercise().getExerciseId())) {
//                exercisesHasMusclesRes
//                    .get(sesHasMusDB.getExercise().getExerciseId())
//                    .getMuscleList().add(sesHasMusDB.getMuscle().toString());
//            } else {
//                exercisesHasMusclesRes
//                    .put(sesHasMusDB.getExercise().getExerciseId(),
//                        ExerciseHasMusclesResponse.builder()
//                            .exerciseId(sesHasMusDB.getExercise().getExerciseId())
//                            .name(sesHasMusDB.getExercise().getName())
//                            .basicReps(sesHasMusDB.getExercise().getBasicReps())
//                            .levelEnum(sesHasMusDB.getExercise().getLevelEnum().toString())
//                            .muscleList(new ArrayList<>(List.of(sesHasMusDB.getMuscle().toString()))).build()
//                    );
//            }
//        }
//        for (Long exerciseIdKey : exercisesHasMusclesRes.keySet()) {
//            if (exercisesHasMusclesRes.get(exerciseIdKey).getMuscleList()
//                .stream().noneMatch(muscle -> request.getMuscleList().contains(muscle)))
//                removedIds.add(exerciseIdKey);
//        }
//        removedIds.forEach(exercisesHasMusclesRes::remove);
//
//        ArrayList<ExerciseHasMusclesResponse> actual = new ArrayList<>(musclesOfExercisesRepository
//            .findAllExercisesHasMuscles(request, PageRequest.of(0, 10))
//            .stream().map(ExerciseHasMusclesResponse::buildFromNativeQuery).toList());
//
//        assertNotNull(actual);
//        assertEquals(exercisesHasMusclesRes.size(), actual.size());
//        actual.sort(Comparator.comparing(ExerciseHasMusclesResponse::getExerciseId));
//        for (Long exerciseId : exercisesHasMusclesRes.keySet()) {
//            Collections.sort(exercisesHasMusclesRes.get(exerciseId).getMuscleList());
//        }
//        for (int index = 0; index < exercisesHasMusclesRes.size(); index++) {
//            var eachActual = actual.get(index);
//            var eachExpect = exercisesHasMusclesRes.get(eachActual.getExerciseId());
//
//            assertEquals(eachExpect.getName(), eachActual.getName());
//            assertEquals(eachExpect.getLevelEnum(), eachActual.getLevelEnum());
//            assertEquals(eachExpect.getBasicReps(), eachActual.getBasicReps());
//            assertEquals(eachExpect.getMuscleList(), eachActual.getMuscleList());
//        }
//    }
//
//    @Test
//    public void findAllExercisesHasMuscles_admin_validWithFilteringWithoutMuscleList() {
//        var request = ExerciseHasMusclesResponse.builder()
//            .name("Exercise0")
//            .levelEnum(Level.ADVANCE.toString())
//            .muscleList(new ArrayList<>())
//            .build();
//        var exercises = new ArrayList<>(exerciseRepository.saveAll(List.of(
//            Exercise.builder().levelEnum(Level.BEGINNER).name("Test Exercise1").basicReps(14).imageUrl("hehe").build(),
//            Exercise.builder().levelEnum(Level.INTERMEDIATE).name("Test Exercise02").basicReps(13).imageUrl("hehe").build(),
//            Exercise.builder().levelEnum(Level.ADVANCE).name("Test Exercise03").basicReps(12).imageUrl("hehe").build(),
//            Exercise.builder().levelEnum(Level.ADVANCE).name("Test Exercise4").basicReps(10).imageUrl("hehe").build(),
//            Exercise.builder().levelEnum(Level.BEGINNER).name("Test Exercise5").basicReps(14).imageUrl("hehe").build()
//        )));
//        var exerciseHasMusclesFromDB = new ArrayList<>(musclesOfExercisesRepository.saveAll(List.of(
//            MusclesOfExercises.builder().exercise(exercises.get(0)).muscle(Muscle.CHEST).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(1)).muscle(Muscle.BICEPS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(2)).muscle(Muscle.BACK_LATS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(3)).muscle(Muscle.BACK_LATS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(0)).muscle(Muscle.TRICEPS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(1)).muscle(Muscle.BACK_LATS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(2)).muscle(Muscle.ABS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(3)).muscle(Muscle.BICEPS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(1)).muscle(Muscle.ABS).build(),
//            MusclesOfExercises.builder().exercise(exercises.get(4)).muscle(Muscle.ABS).build()
//        )));
//        var exercisesHasMusclesRes = new LinkedHashMap<Long, ExerciseHasMusclesResponse>();
//        for (MusclesOfExercises sesHasMusDB : exerciseHasMusclesFromDB) {
//            if (sesHasMusDB.getExercise().getName().contains(request.getName())) {
//                if (exercisesHasMusclesRes.containsKey(sesHasMusDB.getExercise().getExerciseId())) {
//                    exercisesHasMusclesRes
//                        .get(sesHasMusDB.getExercise().getExerciseId());
//                } else {
//                    exercisesHasMusclesRes
//                        .put(sesHasMusDB.getExercise().getExerciseId(), ExerciseHasMusclesResponse.builder()
//                            .exerciseId(sesHasMusDB.getExercise().getExerciseId())
//                            .name(sesHasMusDB.getExercise().getName())
//                            .levelEnum(sesHasMusDB.getExercise().getLevelEnum().toString())
//                            .basicReps(sesHasMusDB.getExercise().getBasicReps())
//                            .build());
//                }
//            }
//        }
//
//        ArrayList<ExerciseHasMusclesResponse> actual = new ArrayList<>(musclesOfExercisesRepository
//            .findAllExercisesHasMuscles(request, PageRequest.of(0, 10))
//            .stream().map(ExerciseHasMusclesResponse::buildFromNativeQuery).toList());
//
//        assertNotNull(actual);
//        assertEquals(exercisesHasMusclesRes.size(), actual.size());
//        actual.sort(Comparator.comparing(ExerciseHasMusclesResponse::getExerciseId));
//        for (int index = 0; index < exercisesHasMusclesRes.size(); index++) {
//            var eachActual = actual.get(index);
//            var eachExpect = exercisesHasMusclesRes.get(eachActual.getExerciseId());
//
//            assertEquals(eachExpect.getName(), eachActual.getName());
//            assertEquals(eachExpect.getLevelEnum(), eachActual.getLevelEnum());
//            assertEquals(eachExpect.getBasicReps(), eachActual.getBasicReps());
//        }
//    }
//}

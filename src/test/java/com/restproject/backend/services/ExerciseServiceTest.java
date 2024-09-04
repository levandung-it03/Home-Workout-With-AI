package com.restproject.backend.services;

import com.restproject.backend.dtos.request.ExercisesByLevelAndMusclesRequest;
import com.restproject.backend.dtos.request.NewExerciseRequest;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.MusclesOfExercises;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.mappers.ExerciseMappers;
import com.restproject.backend.repositories.ExerciseRepository;
import com.restproject.backend.repositories.MusclesOfExercisesRepository;
import com.restproject.backend.services.Admin.ExerciseService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseServiceTest {
    @Autowired
    ExerciseService exerciseService;

    @MockBean
    ExerciseRepository exerciseRepository;
    @MockBean
    MusclesOfExercisesRepository musclesOfExercisesRepository;
    @MockBean
    ExerciseMappers exerciseMappers;

    @BeforeEach
    public void init() {
    }

    @Test
    public void createExercise_admin_valid() {
        var muscleList = List.of(Muscle.CHEST, Muscle.TRICEPS);
        NewExerciseRequest newExerciseRequest = NewExerciseRequest.builder()
            .name("Push-ups")
            .muscleIds(muscleList.stream().map(Muscle::getId).toList())
            .level(Level.IMMEDIATE.getLevel())
            .basicReps(14)
            .build();
        Exercise savedExercise = Exercise.builder()
            .basicReps(newExerciseRequest.getBasicReps())
            .name(newExerciseRequest.getName())
            .level(Level.getByLevel(newExerciseRequest.getLevel()))
            .build();
        List<MusclesOfExercises> responseExAndMuscleRelationship = muscleList.stream().map(muscle ->
            MusclesOfExercises.builder().exercise(savedExercise).muscle(muscle).build()).toList();

        //--Declare testing tree.
        Mockito.when(exerciseMappers.insertionToPlain(newExerciseRequest)).thenReturn(savedExercise);
        Mockito.when(exerciseRepository.save(savedExercise)).thenReturn(savedExercise);
        Mockito.when(musclesOfExercisesRepository.saveAll(responseExAndMuscleRelationship))
            .thenReturn(responseExAndMuscleRelationship);

        //--Perform
        Exercise savedMockExercise = exerciseService.createExercise(newExerciseRequest);

        //--Verify
        assertEquals(savedMockExercise.getName(), savedExercise.getName());
        assertEquals(savedMockExercise.getLevel(), savedExercise.getLevel());
        assertEquals(savedMockExercise.getBasicReps(), savedExercise.getBasicReps());

        Mockito.verify(exerciseRepository, Mockito.times(1)).save(savedExercise);
        Mockito.verify(musclesOfExercisesRepository, Mockito.times(1))
            .saveAll(responseExAndMuscleRelationship);
    }

    @Test
    public void getExercisesByLevelAndMuscles_admin_valid() {
        List<Muscle> reqMuscles = List.of(Muscle.CHEST, Muscle.TRICEPS);
        Level reqLevel = Level.IMMEDIATE;
        var request = ExercisesByLevelAndMusclesRequest.builder().level(reqLevel.getLevel())
            .muscleIds(reqMuscles.stream().map(Muscle::getId).toList()).build();
        var response = List.of(
            Exercise.builder().level(reqLevel).exerciseId(0L).build(),
            Exercise.builder().level(reqLevel).exerciseId(3L).build()
        );

        Mockito.when(musclesOfExercisesRepository.findAllExercisesByLevelAndMuscles(reqLevel, reqMuscles))
            .thenReturn(response);

        List<Exercise> queriedExList = exerciseService.getExercisesByLevelAndMuscles(request);

        assertArrayEquals(queriedExList.toArray(), response.toArray());
    }
}

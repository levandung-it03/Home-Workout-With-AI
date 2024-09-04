package com.restproject.backend.services;

import com.restproject.backend.dtos.request.DeleteExerciseRequest;
import com.restproject.backend.dtos.request.ExercisesByLevelAndMusclesRequest;
import com.restproject.backend.dtos.request.NewExerciseRequest;
import com.restproject.backend.dtos.request.UpdateExerciseRequest;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.MusclesOfExercises;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.ExerciseMappers;
import com.restproject.backend.repositories.ExerciseRepository;
import com.restproject.backend.repositories.ExercisesOfSessionsRepository;
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
import java.util.Optional;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseServiceTest {
    @Autowired
    ExerciseService exerciseService;

    @MockBean
    ExerciseRepository exerciseRepository;
    @MockBean
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;
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

    UpdateExerciseRequest updateExerciseRequest() {
        return UpdateExerciseRequest.builder().exerciseId(1L).name("Push-ups").level(2).basicReps(14)
            .muscleIds(List.of(0, 2)).build();
    }

    @Test
    public void updateExercise_admin_validWithoutUpdatingMuscles() throws Exception {
        var exeReq = this.updateExerciseRequest();
        var exeRes = Exercise.builder().exerciseId(exeReq.getExerciseId()).name(exeReq.getName())
            .basicReps(exeReq.getBasicReps()).level(Level.getByLevel(exeReq.getLevel())).build();
        var msByEx = exeReq.getMuscleIds().stream().map(id ->
            MusclesOfExercises.builder().exercise(exeRes).muscle(Muscle.getById(id)).build()
        ).toList();

        Mockito.when(exerciseRepository.findById(exeReq.getExerciseId())).thenReturn(Optional.of(exeRes));
        Mockito.when(exercisesOfSessionsRepository.existsByExerciseExerciseId(exeRes.getExerciseId()))
            .thenReturn(false);
        Mockito.when(musclesOfExercisesRepository.findAllByExercise(exeRes)).thenReturn(msByEx);
        Mockito.doNothing().when(exerciseMappers).updateTarget(exeRes, exeReq);
        Mockito.doNothing().when(exerciseRepository).deleteById(exeRes.getExerciseId());
        Mockito.when(exerciseRepository.save(exeRes)).thenReturn(exeRes);

        Exercise savedEx = exerciseService.updateExercise(exeReq);

        Mockito.verify(exerciseRepository).findById(exeReq.getExerciseId());
        Mockito.verify(exercisesOfSessionsRepository).existsByExerciseExerciseId(exeRes.getExerciseId());
        Mockito.verify(musclesOfExercisesRepository).findAllByExercise(exeRes);
        Mockito.verify(exerciseMappers).updateTarget(exeRes, exeReq);
        Mockito.verify(exerciseRepository).deleteById(exeRes.getExerciseId());
        Mockito.verify(exerciseRepository).save(exeRes);

        assertEquals(exeRes, savedEx);
    }

    @Test
    public void updateExercise_admin_validWithUpdatingMuscles() throws Exception {
        var exeReq = this.updateExerciseRequest();
        var exeRes = Exercise.builder().exerciseId(exeReq.getExerciseId()).name(exeReq.getName())
            .basicReps(exeReq.getBasicReps()).level(Level.getByLevel(exeReq.getLevel())).build();
        var msByEx = exeReq.getMuscleIds().stream().map(id ->
            MusclesOfExercises.builder().exercise(exeRes).muscle(Muscle.getById(id)).build()
        ).toList();
        exeReq.setMuscleIds(List.of(5, 6)); //--Actual new muscle-ids of updated exercise.
        var newMsByEx = exeReq.getMuscleIds().stream().map(id ->
            MusclesOfExercises.builder().exercise(exeRes).muscle(Muscle.getById(id)).build()
        ).toList();

        Mockito.when(exerciseRepository.findById(exeReq.getExerciseId())).thenReturn(Optional.of(exeRes));
        Mockito.when(exercisesOfSessionsRepository.existsByExerciseExerciseId(exeRes.getExerciseId()))
            .thenReturn(false);
        Mockito.when(musclesOfExercisesRepository.findAllByExercise(exeRes)).thenReturn(msByEx);
        Mockito.doNothing().when(exerciseMappers).updateTarget(exeRes, exeReq);
        Mockito.doNothing().when(exerciseRepository).deleteById(exeRes.getExerciseId());
        Mockito.when(exerciseRepository.save(exeRes)).thenReturn(exeRes);
        Mockito.doNothing().when(musclesOfExercisesRepository).deleteAllByExerciseExerciseId(exeRes.getExerciseId());
        Mockito.when(musclesOfExercisesRepository.saveAll(newMsByEx)).thenReturn(Mockito.anyList());

        Exercise savedEx = exerciseService.updateExercise(exeReq);

        Mockito.verify(exerciseRepository).findById(exeReq.getExerciseId());
        Mockito.verify(exercisesOfSessionsRepository).existsByExerciseExerciseId(exeRes.getExerciseId());
        Mockito.verify(musclesOfExercisesRepository).findAllByExercise(exeRes);
        Mockito.verify(exerciseMappers).updateTarget(exeRes, exeReq);
        Mockito.verify(exerciseRepository).deleteById(exeRes.getExerciseId());
        Mockito.verify(exerciseRepository).save(exeRes);
        Mockito.verify(musclesOfExercisesRepository).deleteAllByExerciseExerciseId(exeRes.getExerciseId());
        Mockito.verify(musclesOfExercisesRepository).saveAll(newMsByEx);

        assertEquals(exeRes, savedEx);
    }

    @Test
    public void updateExercise_admin_exerciseIdNotFound() {
        var exeReq = this.updateExerciseRequest();

        Mockito.when(exerciseRepository.findById(exeReq.getExerciseId())).thenReturn(Optional.empty());

        var exception = assertThrows(ApplicationException.class, () -> exerciseService.updateExercise(exeReq));
        assertEquals(exception.getMessage(), ErrorCodes.INVALID_PRIMARY.getMessage());
    }

    @Test
    public void updateExercise_admin_exerciseIdRelatedToSession() {
        var exeReq = this.updateExerciseRequest();
        var exeRes = Exercise.builder().exerciseId(exeReq.getExerciseId()).name(exeReq.getName())
            .basicReps(exeReq.getBasicReps()).level(Level.getByLevel(exeReq.getLevel())).build();

        Mockito.when(exerciseRepository.findById(exeReq.getExerciseId())).thenReturn(Optional.of(exeRes));
        Mockito.when(exercisesOfSessionsRepository.existsByExerciseExerciseId(exeReq.getExerciseId()))
            .thenReturn(true);

        var exception = assertThrows(ApplicationException.class, () -> exerciseService.updateExercise(exeReq));
        assertEquals(exception.getMessage(), ErrorCodes.FORBIDDEN_UPDATING.getMessage());
    }

    @Test
    public void updateExercise_admin_emptyFormerMuscleIds() {
        var exeReq = this.updateExerciseRequest();
        var exeRes = Exercise.builder().exerciseId(exeReq.getExerciseId()).name(exeReq.getName())
            .basicReps(exeReq.getBasicReps()).level(Level.getByLevel(exeReq.getLevel())).build();

        Mockito.when(exerciseRepository.findById(exeReq.getExerciseId())).thenReturn(Optional.of(exeRes));
        Mockito.when(exercisesOfSessionsRepository.existsByExerciseExerciseId(exeReq.getExerciseId()))
                .thenReturn(false);
        Mockito.when(musclesOfExercisesRepository.findAllByExercise(exeRes)).thenReturn(List.of());

        var exception = assertThrows(ApplicationException.class, () -> exerciseService.updateExercise(exeReq));
        assertEquals(exception.getMessage(), ErrorCodes.INVALID_IDS_COLLECTION.getMessage());
    }

    @Test
    public void deleteExercise_admin_valid() {
        var req = DeleteExerciseRequest.builder().exerciseId(2L).build();
        Mockito.when(exerciseRepository.existsById(req.getExerciseId())).thenReturn(true);
        Mockito.when(exercisesOfSessionsRepository.existsByExerciseExerciseId(req.getExerciseId())).thenReturn(false);
        Mockito.doNothing().when(exerciseRepository).deleteById(req.getExerciseId());
        Mockito.doNothing().when(musclesOfExercisesRepository).deleteAllByExerciseExerciseId(req.getExerciseId());

        exerciseService.deleteExercise(req);

        Mockito.verify(exerciseRepository, Mockito.times(1)).existsById(req.getExerciseId());
        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1))
            .existsByExerciseExerciseId(req.getExerciseId());
        Mockito.verify(exerciseRepository, Mockito.times(1)).deleteById(req.getExerciseId());
        Mockito.verify(musclesOfExercisesRepository, Mockito.times(1))
            .deleteAllByExerciseExerciseId(req.getExerciseId());
    }

    @Test
    public void deleteExercise_admin_exerciseIdNotFound() {
        var req = DeleteExerciseRequest.builder().exerciseId(2L).build();
        Mockito.when(exerciseRepository.existsById(req.getExerciseId())).thenReturn(false);

        var exc = assertThrows(ApplicationException.class, () -> exerciseService.deleteExercise(req));
        Mockito.verify(exerciseRepository, Mockito.times(1)).existsById(req.getExerciseId());
        assertEquals(exc.getMessage(), ErrorCodes.INVALID_PRIMARY.getMessage());
    }

    @Test
    public void deleteExercise_admin_exerciseIdRelatedToSession() {
        var req = DeleteExerciseRequest.builder().exerciseId(2L).build();
        Mockito.when(exerciseRepository.existsById(req.getExerciseId())).thenReturn(true);
        Mockito.when(exercisesOfSessionsRepository.existsByExerciseExerciseId(req.getExerciseId()))
            .thenReturn(true);

        var exc = assertThrows(ApplicationException.class, () -> exerciseService.deleteExercise(req));
        Mockito.verify(exerciseRepository, Mockito.times(1)).existsById(req.getExerciseId());
        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1))
            .existsByExerciseExerciseId(req.getExerciseId());
        assertEquals(exc.getMessage(), ErrorCodes.FORBIDDEN_UPDATING.getMessage());
    }
}

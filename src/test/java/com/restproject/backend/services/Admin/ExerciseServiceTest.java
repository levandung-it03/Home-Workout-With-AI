package com.restproject.backend.services.Admin;

import com.restproject.backend.dtos.request.*;
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
import com.restproject.backend.services.ExerciseService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseServiceTest {
    @Autowired
    ExerciseService exerciseServiceOfAdmin;

    @MockBean
    ExerciseRepository exerciseRepository;
    @MockBean
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;
    @MockBean
    MusclesOfExercisesRepository musclesOfExercisesRepository;
    @MockBean
    ExerciseMappers exerciseMappers;
    
    @Test
    public void createExercise_admin_valid() {
        var muscleList = List.of(Muscle.CHEST, Muscle.TRICEPS);
        NewExerciseRequest newExerciseRequest = NewExerciseRequest.builder()
            .name("Push-ups")
            .muscleIds(muscleList.stream().map(Muscle::getId).toList())
            .level(Level.INTERMEDIATE.getLevel())
            .basicReps(14)
            .build();
        Exercise expectedExercise = Exercise.builder()
            .basicReps(newExerciseRequest.getBasicReps())
            .name(newExerciseRequest.getName())
            .level(Level.getByLevel(newExerciseRequest.getLevel()))
            .build();
        List<MusclesOfExercises> responseExAndMuscleRelationship = muscleList.stream().map(muscle ->
            MusclesOfExercises.builder().exercise(expectedExercise).muscle(muscle).build()).toList();

        //--Declare testing tree.
        Mockito.when(exerciseMappers.insertionToPlain(newExerciseRequest)).thenReturn(expectedExercise);
        Mockito.when(exerciseRepository.save(expectedExercise)).thenReturn(expectedExercise);
        Mockito.when(musclesOfExercisesRepository.saveAll(responseExAndMuscleRelationship))
            .thenReturn(responseExAndMuscleRelationship);

        //--Perform
        Exercise actual = exerciseServiceOfAdmin.createExercise(newExerciseRequest);

        //--Verify
        assertEquals(actual.getName(), expectedExercise.getName());
        assertEquals(actual.getLevel(), expectedExercise.getLevel());
        assertEquals(actual.getBasicReps(), expectedExercise.getBasicReps());

        Mockito.verify(exerciseMappers, Mockito.times(1)).insertionToPlain(newExerciseRequest);
        Mockito.verify(exerciseRepository, Mockito.times(1)).save(expectedExercise);
        Mockito.verify(musclesOfExercisesRepository, Mockito.times(1))
            .saveAll(responseExAndMuscleRelationship);
    }

    @Test
    public void createExercise_admin_duplicatedUniqueConstraint() {
        var muscleList = List.of(Muscle.CHEST, Muscle.TRICEPS);
        NewExerciseRequest newExerciseRequest = NewExerciseRequest.builder()
            .name("Push-ups")
            .muscleIds(muscleList.stream().map(Muscle::getId).toList())
            .level(Level.INTERMEDIATE.getLevel())
            .basicReps(14)
            .build();
        Exercise expectedExercise = Exercise.builder()
            .basicReps(newExerciseRequest.getBasicReps())
            .name(newExerciseRequest.getName())
            .level(Level.getByLevel(newExerciseRequest.getLevel()))
            .build();

        //--Declare testing tree.
        Mockito.when(exerciseMappers.insertionToPlain(newExerciseRequest)).thenReturn(expectedExercise);
        Mockito.when(exerciseRepository.save(expectedExercise)).thenThrow(DataIntegrityViolationException.class);

        //--Perform
        var exc = assertThrows(ApplicationException.class, () ->
            exerciseServiceOfAdmin.createExercise(newExerciseRequest));

        //--Verify
        assertEquals(exc.getErrorCodes(), ErrorCodes.DUPLICATED_EXERCISE);
        Mockito.verify(exerciseMappers, Mockito.times(1)).insertionToPlain(newExerciseRequest);
        Mockito.verify(exerciseRepository, Mockito.times(1)).save(expectedExercise);
    }

    UpdateExerciseRequest updateExerciseAndMusclesRequest() {
        return UpdateExerciseRequest.builder().exerciseId(1L).name("Push-ups").level(2).basicReps(14)
            .muscleIds(List.of(0, 2)).build();
    }

    @Test
    public void updateExerciseAndMuscles_admin_validWithoutUpdatingMuscles() {
        var exeReq = this.updateExerciseAndMusclesRequest();
        var exeRes = Exercise.builder().exerciseId(exeReq.getExerciseId()).name(exeReq.getName())
            .basicReps(exeReq.getBasicReps()).level(Level.getByLevel(exeReq.getLevel())).build();
        var msByEx = exeReq.getMuscleIds().stream().map(id ->
            MusclesOfExercises.builder().exercise(exeRes).muscle(Muscle.getById(id)).build()
        ).toList();

        Mockito.when(exerciseRepository.findById(exeReq.getExerciseId())).thenReturn(Optional.of(exeRes));
        Mockito.when(exercisesOfSessionsRepository.existsByExerciseExerciseId(exeRes.getExerciseId()))
            .thenReturn(false);
        Mockito.when(musclesOfExercisesRepository.findAllByExerciseExerciseId(exeRes.getExerciseId())).thenReturn(msByEx);
        Mockito.doNothing().when(exerciseMappers).updateTarget(exeRes, exeReq);
        Mockito.doNothing().when(exerciseRepository).deleteById(exeRes.getExerciseId());
        Mockito.when(exerciseRepository.save(exeRes)).thenReturn(exeRes);

        Exercise actual = exerciseServiceOfAdmin.updateExerciseAndMuscles(exeReq);

        Mockito.verify(exerciseRepository, Mockito.times(1)).findById(exeReq.getExerciseId());
        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1))
            .existsByExerciseExerciseId(exeRes.getExerciseId());
        Mockito.verify(musclesOfExercisesRepository, Mockito.times(1))
            .findAllByExerciseExerciseId(exeRes.getExerciseId());
        Mockito.verify(exerciseMappers, Mockito.times(1)).updateTarget(exeRes, exeReq);
        Mockito.verify(exerciseRepository, Mockito.times(1)).deleteById(exeRes.getExerciseId());
        Mockito.verify(exerciseRepository, Mockito.times(1)).save(exeRes);

        assertEquals(exeRes, actual);
    }

    @Test
    public void updateExerciseAndMuscles_admin_validWithUpdatingMuscles() {
        var exeReq = this.updateExerciseAndMusclesRequest();
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
        Mockito.when(musclesOfExercisesRepository.findAllByExerciseExerciseId(exeRes.getExerciseId())).thenReturn(msByEx);
        Mockito.doNothing().when(exerciseMappers).updateTarget(exeRes, exeReq);
        Mockito.doNothing().when(exerciseRepository).deleteById(exeRes.getExerciseId());
        Mockito.when(exerciseRepository.save(exeRes)).thenReturn(exeRes);
        Mockito.doNothing().when(musclesOfExercisesRepository).deleteAllByExerciseExerciseId(exeRes.getExerciseId());
        Mockito.when(musclesOfExercisesRepository.saveAll(newMsByEx)).thenReturn(Mockito.anyList());

        Exercise actual = exerciseServiceOfAdmin.updateExerciseAndMuscles(exeReq);

        Mockito.verify(exerciseRepository, Mockito.times(1))
            .findById(exeReq.getExerciseId());
        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1))
            .existsByExerciseExerciseId(exeRes.getExerciseId());
        Mockito.verify(musclesOfExercisesRepository, Mockito.times(1))
            .findAllByExerciseExerciseId(exeRes.getExerciseId());
        Mockito.verify(exerciseMappers, Mockito.times(1))
            .updateTarget(exeRes, exeReq);
        Mockito.verify(exerciseRepository, Mockito.times(1))
            .deleteById(exeRes.getExerciseId());
        Mockito.verify(exerciseRepository, Mockito.times(1))
            .save(exeRes);
        Mockito.verify(musclesOfExercisesRepository, Mockito.times(1))
            .deleteAllByExerciseExerciseId(exeRes.getExerciseId());
        Mockito.verify(musclesOfExercisesRepository, Mockito.times(1))
            .saveAll(newMsByEx);

        assertEquals(exeRes, actual);
    }

    @Test
    public void updateExerciseAndMuscles_admin_exerciseIdNotFound() {
        var exeReq = this.updateExerciseAndMusclesRequest();

        Mockito.when(exerciseRepository.findById(exeReq.getExerciseId())).thenReturn(Optional.empty());

        var exception = assertThrows(ApplicationException.class, () -> exerciseServiceOfAdmin.updateExerciseAndMuscles(exeReq));
        Mockito.verify(exerciseRepository, Mockito.times(1)).findById(exeReq.getExerciseId());
        assertEquals(ErrorCodes.INVALID_PRIMARY.getMessage(), exception.getMessage());
    }

    @Test
    public void updateExerciseAndMuscles_admin_exerciseIdRelatedToSession() {
        var exeReq = this.updateExerciseAndMusclesRequest();
        var exeRes = Exercise.builder().exerciseId(exeReq.getExerciseId()).name(exeReq.getName())
            .basicReps(exeReq.getBasicReps()).level(Level.getByLevel(exeReq.getLevel())).build();

        Mockito.when(exerciseRepository.findById(exeReq.getExerciseId())).thenReturn(Optional.of(exeRes));
        Mockito.when(exercisesOfSessionsRepository.existsByExerciseExerciseId(exeReq.getExerciseId()))
            .thenReturn(true);

        var exception = assertThrows(ApplicationException.class, () -> exerciseServiceOfAdmin.updateExerciseAndMuscles(exeReq));
        Mockito.verify(exerciseRepository, Mockito.times(1)).findById(exeReq.getExerciseId());
        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1))
            .existsByExerciseExerciseId(exeReq.getExerciseId());
        assertEquals(ErrorCodes.FORBIDDEN_UPDATING.getMessage(), exception.getMessage());
    }

    @Test
    public void updateExerciseAndMuscles_admin_emptyFormerMuscleIds() {
        var exeReq = this.updateExerciseAndMusclesRequest();
        var exeRes = Exercise.builder().exerciseId(exeReq.getExerciseId()).name(exeReq.getName())
            .basicReps(exeReq.getBasicReps()).level(Level.getByLevel(exeReq.getLevel())).build();

        Mockito.when(exerciseRepository.findById(exeReq.getExerciseId())).thenReturn(Optional.of(exeRes));
        Mockito.when(exercisesOfSessionsRepository.existsByExerciseExerciseId(exeReq.getExerciseId()))
                .thenReturn(false);
        Mockito.when(musclesOfExercisesRepository.findAllByExerciseExerciseId(exeRes.getExerciseId()))
            .thenReturn(List.of());

        var exception = assertThrows(ApplicationException.class, () -> exerciseServiceOfAdmin.updateExerciseAndMuscles(exeReq));
        Mockito.verify(exerciseRepository, Mockito.times(1)).findById(exeReq.getExerciseId());
        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1))
            .existsByExerciseExerciseId(exeReq.getExerciseId());
        Mockito.verify(musclesOfExercisesRepository, Mockito.times(1))
            .findAllByExerciseExerciseId(exeReq.getExerciseId());
        assertEquals(ErrorCodes.INVALID_PRIMARY.getMessage(), exception.getMessage());
    }

    @Test
    public void deleteExercise_admin_valid() {
        var req = DeleteObjectRequest.builder().id(2L).build();
        Mockito.when(exerciseRepository.existsById(req.getId())).thenReturn(true);
        Mockito.when(exercisesOfSessionsRepository.existsByExerciseExerciseId(req.getId())).thenReturn(false);
        Mockito.doNothing().when(exerciseRepository).deleteById(req.getId());
        Mockito.doNothing().when(musclesOfExercisesRepository).deleteAllByExerciseExerciseId(req.getId());

        exerciseServiceOfAdmin.deleteExercise(req);

        Mockito.verify(exerciseRepository, Mockito.times(1)).existsById(req.getId());
        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1))
            .existsByExerciseExerciseId(req.getId());
        Mockito.verify(exerciseRepository, Mockito.times(1)).deleteById(req.getId());
        Mockito.verify(musclesOfExercisesRepository, Mockito.times(1))
            .deleteAllByExerciseExerciseId(req.getId());
    }

    @Test
    public void deleteExercise_admin_exerciseIdNotFound() {
        var req = DeleteObjectRequest.builder().id(2L).build();
        Mockito.when(exerciseRepository.existsById(req.getId())).thenReturn(false);

        var exc = assertThrows(ApplicationException.class, () -> exerciseServiceOfAdmin.deleteExercise(req));
        Mockito.verify(exerciseRepository, Mockito.times(1)).existsById(req.getId());
        assertEquals(ErrorCodes.INVALID_PRIMARY.getMessage(), exc.getMessage());
    }

    @Test
    public void deleteExercise_admin_exerciseIdRelatedToSession() {
        var req = DeleteObjectRequest.builder().id(2L).build();
        Mockito.when(exerciseRepository.existsById(req.getId())).thenReturn(true);
        Mockito.when(exercisesOfSessionsRepository.existsByExerciseExerciseId(req.getId()))
            .thenReturn(true);

        var exc = assertThrows(ApplicationException.class, () -> exerciseServiceOfAdmin.deleteExercise(req));
        Mockito.verify(exerciseRepository, Mockito.times(1)).existsById(req.getId());
        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1))
            .existsByExerciseExerciseId(req.getId());
        assertEquals(ErrorCodes.FORBIDDEN_UPDATING.getMessage(), exc.getMessage());
    }
}

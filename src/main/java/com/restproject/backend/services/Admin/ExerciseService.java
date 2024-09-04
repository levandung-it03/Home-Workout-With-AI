package com.restproject.backend.services.Admin;

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
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseService {
    ExerciseMappers exerciseMappers;
    ExerciseRepository exerciseRepository;
    MusclesOfExercisesRepository musclesOfExercisesRepository;
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;

    public List<Exercise> getExercisesByLevelAndMuscles(ExercisesByLevelAndMusclesRequest request) {
        return musclesOfExercisesRepository.findAllExercisesByLevelAndMuscles(
            Level.getByLevel(request.getLevel()),
            request.getMuscleIds().stream().map(Muscle::getById).toList()
        );
    }

    @Transactional(rollbackOn = {Exception.class})
    public Exercise createExercise(NewExerciseRequest request) throws ApplicationException {
        var savedExercise = exerciseRepository.save(exerciseMappers.insertionToPlain(request));

        musclesOfExercisesRepository.saveAll(request.getMuscleIds().stream().map(id ->
            MusclesOfExercises.builder().exercise(savedExercise).muscle(Muscle.getById(id)).build()).toList());

        return savedExercise;
    }

    @Transactional(rollbackOn = {Exception.class})
    public Exercise updateExercise(UpdateExerciseRequest request) throws Exception {
        var formerEx = exerciseRepository.findById(request.getExerciseId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        //--Check if this Exercise can be updated or not.
        if (exercisesOfSessionsRepository.existsByExerciseExerciseId(formerEx.getExerciseId()))
            throw new ApplicationException(ErrorCodes.FORBIDDEN_UPDATING);
        //--Query all related and updated data is existing in DB.
        var formerRls = musclesOfExercisesRepository.findAllByExercise(formerEx);
        if (formerRls.isEmpty())    //--If data in DB is wrong.
            throw new ApplicationException(ErrorCodes.INVALID_IDS_COLLECTION);

        //--Mapping new values into "formerEx".
        exerciseMappers.updateTarget(formerEx, request);
        //--Start to save updated data.
        exerciseRepository.deleteById(formerEx.getExerciseId());
        var savedExercise = exerciseRepository.save(formerEx);
        //--Check if muscles-exercise relationship has any changes to update or not.
        if (formerRls.stream().map(r -> r.getMuscle().getId()).sorted().toList()
            .equals(request.getMuscleIds().stream().sorted().toList()))
            return savedExercise;   //--Doesn't update relationships because of un-changing muscles.

        //--Delete the former muscles-exercise relationship.
        musclesOfExercisesRepository.deleteAllByExerciseExerciseId(formerEx.getExerciseId());
        var newMusclesOfEx = request.getMuscleIds().stream().map(id ->
            MusclesOfExercises.builder().exercise(savedExercise).muscle(Muscle.getById(id)).build()
        ).toList();
        //--Save all new relationship.
        musclesOfExercisesRepository.saveAll(newMusclesOfEx);
        return savedExercise;
    }

    @Transactional(rollbackOn = {Exception.class})
    public void deleteExercise(DeleteExerciseRequest request) {
        if (!exerciseRepository.existsById(request.getExerciseId()))
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);

        if (exercisesOfSessionsRepository.existsByExerciseExerciseId(request.getExerciseId()))
            throw new ApplicationException(ErrorCodes.FORBIDDEN_UPDATING);

        exerciseRepository.deleteById(request.getExerciseId());
        musclesOfExercisesRepository.deleteAllByExerciseExerciseId(request.getExerciseId());
    }
}

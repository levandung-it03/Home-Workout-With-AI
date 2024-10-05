package com.restproject.backend.services;

import com.restproject.backend.dtos.request.*;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.MusclesOfExercises;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.ExerciseMappers;
import com.restproject.backend.repositories.ExerciseRepository;
import com.restproject.backend.repositories.ExercisesOfSessionsRepository;
import com.restproject.backend.repositories.MusclesOfExercisesRepository;
import com.restproject.backend.services.ThirdParty.ImgCloudUpload;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseService {
    ImgCloudUpload imgCloudUpload;
    ExerciseMappers exerciseMappers;
    ExerciseRepository exerciseRepository;
    MusclesOfExercisesRepository musclesOfExercisesRepository;
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;

    @Transactional(rollbackOn = {RuntimeException.class})
    public Exercise createExercise(NewExerciseRequest request) throws ApplicationException {
        Exercise savedExercise;
        try { savedExercise = exerciseRepository.save(exerciseMappers.insertionToPlain(request)); }
        catch (DataIntegrityViolationException e) { throw new ApplicationException(ErrorCodes.DUPLICATED_EXERCISE); }
        musclesOfExercisesRepository.saveAll(request.getMuscleIds().stream().map(id ->
            MusclesOfExercises.builder().exercise(savedExercise).muscle(Muscle.getById(id)).build()).toList());
        return savedExercise;
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public Exercise updateExerciseAndMuscles(UpdateExerciseRequest request) throws ApplicationException {
        var formerEx = exerciseRepository.findById(request.getExerciseId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        //--Check if this Exercise can be updated or not.
        if (exercisesOfSessionsRepository.existsByExerciseExerciseId(formerEx.getExerciseId()))
            throw new ApplicationException(ErrorCodes.FORBIDDEN_UPDATING);
        //--Query all related and updated data is existing in DB.
        var formerRls = musclesOfExercisesRepository.findAllByExerciseExerciseId(formerEx.getExerciseId());
        if (formerRls.isEmpty())    //--If data in DB is wrong.
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);

        //--Mapping new values into "formerEx".
        exerciseMappers.updateTarget(formerEx, request);
        //--Start to save updated data.
        exerciseRepository.updateExerciseByExercise(formerEx);

        //--Check if there's changes with Muscles of Updated Exercise.
        if (!formerRls.stream().map(r -> r.getMuscle().getId()).collect(Collectors.toSet())
            .equals(new HashSet<>(request.getMuscleIds()))) {
            return formerEx;   //--Nothing updated equal to return immediately.
        }

        //--Delete the former muscles-exercise relationship.
        musclesOfExercisesRepository.deleteAllByExerciseExerciseId(formerEx.getExerciseId());
        var newMusclesOfEx = request.getMuscleIds().stream().map(id ->
            MusclesOfExercises.builder().exercise(formerEx).muscle(Muscle.getById(id)).build()
        ).toList();
        //--Save all new relationship.
        musclesOfExercisesRepository.saveAll(newMusclesOfEx);
        return formerEx;
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public void deleteExercise(DeleteObjectRequest request) {
        if (!exerciseRepository.existsById(request.getId()))
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);

        if (exercisesOfSessionsRepository.existsByExerciseExerciseId(request.getId()))
            throw new ApplicationException(ErrorCodes.FORBIDDEN_UPDATING);

        musclesOfExercisesRepository.deleteAllByExerciseExerciseId(request.getId());
        exerciseRepository.deleteById(request.getId());
    }

    public Map<String, String> uploadExerciseImg(UpsertExerciseImageRequest request) throws IOException {
        var exercise = exerciseRepository.findById(request.getExerciseId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        if (!Objects.isNull(exercise.getImagePublicId()))
            imgCloudUpload.remove(exercise.getImagePublicId());

        var infoMap = imgCloudUpload.uploadAndReturnInfo("exercise", request.getExerciseImage());
        exercise.setImagePublicId(infoMap.get("publicId"));
        exercise.setImageUrl(infoMap.get("url"));
        exerciseRepository.updateImageUrlByExerciseId(exercise);
        return Map.of("imageUrl", infoMap.get("url"));
    }
}

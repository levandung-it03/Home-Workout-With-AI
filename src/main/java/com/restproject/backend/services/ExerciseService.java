package com.restproject.backend.services;

import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.MuscleExercise;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.ExerciseMappers;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.repositories.*;
import com.restproject.backend.services.ThirdParty.ImgCloudUpload;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseService {
    PageMappers pageMappers;
    ImgCloudUpload imgCloudUpload;
    ExerciseMappers exerciseMappers;
    MuscleRepository muscleRepository;
    ExerciseRepository exerciseRepository;
    MuscleExerciseRepository muscleExerciseRepository;
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;

    public TablePagesResponse<Exercise> getExercisesPages(PaginatedTableRequest request) {
        Pageable pageableCfg = pageMappers.tablePageRequestToPageable(request).toPageable(Exercise.class);

        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<Exercise> repoRes = exerciseRepository.findAll(pageableCfg);
            return TablePagesResponse.<Exercise>builder().data(repoRes.stream().toList())
                .currentPage(request.getPage()).totalPages(repoRes.getTotalPages()).build();
        }

        //--Build filtering info.
        ExercisePagesRequest exerciseInfo;
        try {
            exerciseInfo = ExercisePagesRequest.buildFromHashMap(request.getFilterFields());
        } catch (ApplicationException | IllegalArgumentException | NullPointerException | NoSuchFieldException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }

        Page<Exercise> repoRes = exerciseRepository.findAllExercisesCustom(exerciseInfo, pageableCfg);
        return TablePagesResponse.<Exercise>builder().data(repoRes.stream().toList())
            .currentPage(request.getPage()).totalPages(repoRes.getTotalPages()).build();
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public Exercise createExercise(NewExerciseRequest request) throws ApplicationException {
        Exercise savedExercise;
        try { savedExercise = exerciseRepository.save(exerciseMappers.insertionToPlain(request)); }
        catch (DataIntegrityViolationException e) { throw new ApplicationException(ErrorCodes.DUPLICATED_EXERCISE); }
        muscleExerciseRepository.saveAll(
            muscleRepository.findAllById(request.getMuscleIds())
                .stream().map(muscle -> MuscleExercise.builder().exercise(savedExercise).muscle(muscle).build())
                .toList());
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
        var formerRls = muscleExerciseRepository.findAllByExerciseExerciseId(formerEx.getExerciseId());
        if (formerRls.isEmpty())    //--If data in DB is wrong.
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);

        //--Mapping new values into "formerEx".
        exerciseMappers.updateTarget(formerEx, request);
        //--Start to save updated data.
        exerciseRepository.updateExerciseByExercise(formerEx);

        //--Check if there's changes with Muscles of Updated Exercise.
        if (formerRls.stream().map(r -> r.getMuscle().getMuscleId()).collect(Collectors.toSet())
            .equals(new HashSet<>(request.getMuscleIds()))) {
            return formerEx;   //--Nothing updated equal to return immediately.
        }

        //--Delete the former muscles-exercise relationship.
        muscleExerciseRepository.deleteAllByExerciseExerciseId(formerEx.getExerciseId());
        var newMusclesOfEx = muscleRepository.findAllById(request.getMuscleIds()).stream().map(muscle ->
            MuscleExercise.builder().exercise(formerEx).muscle(muscle).build()
        ).toList();
        //--Save all new relationship.
        muscleExerciseRepository.saveAll(newMusclesOfEx);
        return formerEx;
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public void deleteExercise(DeleteObjectRequest request) throws IOException {
        if (!exerciseRepository.existsById(request.getId()))
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);

        if (exercisesOfSessionsRepository.existsByExerciseExerciseId(request.getId()))
            throw new ApplicationException(ErrorCodes.FORBIDDEN_UPDATING);

        var imagePublicId = exerciseRepository.findById(request.getId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY)).getImagePublicId();

        exerciseRepository.deleteById(request.getId());  //--Automatically delete Muscles relationships.
        imgCloudUpload.remove(imagePublicId);
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

package com.restproject.backend.controllers;

import com.restproject.backend.dtos.reponse.ApiResponseObject;
import com.restproject.backend.dtos.request.DeleteExerciseRequest;
import com.restproject.backend.dtos.request.ExercisesByLevelAndMusclesRequest;
import com.restproject.backend.dtos.request.NewExerciseRequest;
import com.restproject.backend.dtos.request.UpdateExerciseRequest;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Admin.ExerciseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseControllers {
    ExerciseService exerciseService;

    @ResponseBody
    @GetMapping("/admin/v1/get-exercises-by-level-and-muscles")
    public ResponseEntity<ApiResponseObject<List<Exercise>>> getExercisesByLevelAndMuscles(
        @Valid @RequestBody ExercisesByLevelAndMusclesRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_EXS_BY_LV_AND_MUSCLE,
            exerciseService.getExercisesByLevelAndMuscles(request));
    }

    @ResponseBody
    @PostMapping("/admin/v1/create-exercise")
    public ResponseEntity<ApiResponseObject<Exercise>> createExercise(@Valid @RequestBody NewExerciseRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_EXERCISE,
            exerciseService.createExercise(request));
    }

    @ResponseBody
    @PutMapping("/admin/v1/update-exercise")
    public ResponseEntity<ApiResponseObject<Exercise>> updateExercise(
        @Valid @RequestBody UpdateExerciseRequest request) throws Exception {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_EXERCISE,
            exerciseService.updateExercise(request));
    }

    @ResponseBody
    @DeleteMapping("/admin/v1/delete-exercise")
    public ResponseEntity<ApiResponseObject<Void>> deleteExercise(@Valid @RequestBody DeleteExerciseRequest request) {
        exerciseService.deleteExercise(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.DELETE_EXERCISE);
    }
}

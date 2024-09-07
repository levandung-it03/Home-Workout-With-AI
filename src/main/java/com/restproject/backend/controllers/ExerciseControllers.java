package com.restproject.backend.controllers;

import com.restproject.backend.dtos.reponse.ApiResponseObject;
import com.restproject.backend.dtos.request.*;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Admin.ExerciseService;
import jakarta.validation.Valid;
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
    ExerciseService exerciseServiceOfAdmin;

    @ResponseBody
    @GetMapping("/admin/v1/get-exercises-by-level-and-muscles")
    public ResponseEntity<ApiResponseObject<List<Exercise>>> getExercisesByLevelAndMuscles(
        @Valid @RequestBody ExercisesByLevelAndMusclesRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_EXS_BY_LV_AND_MUSCLE,
            exerciseServiceOfAdmin.getExercisesByLevelAndMuscles(request));
    }

    @ResponseBody
    @GetMapping("/admin/v1/get-exercises-by-page")
    public ResponseEntity<ApiResponseObject<List<Exercise>>> getPaginatedListOfExercises(
        @Valid @RequestBody PaginatedObjectRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_PAGINATED_EXERCISES,
            exerciseServiceOfAdmin.getPaginatedListOfExercises(request));
    }

    @ResponseBody
    @PostMapping("/admin/v1/create-exercise")
    public ResponseEntity<ApiResponseObject<Exercise>> createExercise(@Valid @RequestBody NewExerciseRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_EXERCISE,
            exerciseServiceOfAdmin.createExercise(request));
    }

    @ResponseBody
    @PutMapping("/admin/v1/update-exercise")
    public ResponseEntity<ApiResponseObject<Exercise>> updateExercise(
        @Valid @RequestBody UpdateExerciseRequest request) throws Exception {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_EXERCISE,
            exerciseServiceOfAdmin.updateExercise(request));
    }

    @ResponseBody
    @DeleteMapping("/admin/v1/delete-exercise")
    public ResponseEntity<ApiResponseObject<Void>> deleteExercise(@Valid @RequestBody DeleteObjectRequest request) {
        exerciseServiceOfAdmin.deleteExercise(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.DELETE_EXERCISE);
    }
}

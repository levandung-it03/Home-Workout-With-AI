package com.restproject.backend.controllers.Private;

import com.restproject.backend.dtos.response.ApiResponseObject;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseControllers {
    ExerciseService exerciseServiceOfAdmin;

    @ResponseBody
    @PostMapping("/admin/v1/create-exercise")
    public ResponseEntity<ApiResponseObject<Exercise>> createExercise(@Valid @RequestBody NewExerciseRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_EXERCISE,
            exerciseServiceOfAdmin.createExercise(request));
    }

    @ResponseBody
    @PutMapping("/admin/v1/update-exercise-and-muscles")
    public ResponseEntity<ApiResponseObject<Exercise>> updateExerciseAndMuscles(
        @Valid @RequestBody UpdateExerciseRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_EXERCISE,
            exerciseServiceOfAdmin.updateExerciseAndMuscles(request));
    }

    @ResponseBody
    @DeleteMapping("/admin/v1/delete-exercise")
    public ResponseEntity<ApiResponseObject<Void>> deleteExercise(@Valid @RequestBody DeleteObjectRequest request) {
        exerciseServiceOfAdmin.deleteExercise(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.DELETE_EXERCISE);
    }
}

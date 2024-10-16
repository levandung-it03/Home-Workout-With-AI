package com.restproject.backend.controllers;

import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.ExerciseService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseControllers {
    ExerciseService exerciseServiceOfAdmin;

    @ResponseBody
    @GetMapping("/admin/v1/get-exercises-has-muscles-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<Exercise>>> getExercisesHasMusclesPages(
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_EXERCISES_HAS_MUSCLES_PAGES,
            exerciseServiceOfAdmin.getExercisesPages(request));
    }

    @ResponseBody
    @PostMapping(value = "/admin/v1/create-exercise")
    public ResponseEntity<ApiResponseObject<Exercise>> createExercise(@Valid @RequestBody NewExerciseRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_EXERCISE,
            exerciseServiceOfAdmin.createExercise(request));
    }

    @ResponseBody
    @PostMapping(value = "/admin/v1/upload-exercise-image",
        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResponseObject<Map<String, String>>> upsertExerciseImage(
        @Valid UpsertExerciseImageRequest request) throws IOException {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_EXERCISE,
            exerciseServiceOfAdmin.uploadExerciseImg(request));
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

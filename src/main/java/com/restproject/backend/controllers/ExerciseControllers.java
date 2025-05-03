package com.restproject.backend.controllers;

import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "Bearer", bearerFormat = "JWT")
@RequestMapping("/api/private")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseControllers {
    ExerciseService exerciseServiceOfAdmin;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all paginated Exercises (have relational Muscles)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=23004; message=Get Exercise has Muscles pages successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10008,10009]; message=Invalid [filtering,sorting] field or value",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @GetMapping("/admin/v1/get-exercises-has-muscles-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<Exercise>>> getExercisesHasMusclesPages(
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_EXERCISES_HAS_MUSCLES_PAGES,
            exerciseServiceOfAdmin.getExercisesPages(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Exercise")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=23001; message=Create new Exercise successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=13001; message=Exercise's Name, Level and Basic Reps set is already existing",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PostMapping(value = "/admin/v1/create-exercise")
    public ResponseEntity<ApiResponseObject<Exercise>> createExercise(@Valid @RequestBody NewExerciseRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_EXERCISE,
            exerciseServiceOfAdmin.createExercise(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Upload Exercise's image")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=23005; message=Upload Exercise image successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10005; message=Can not get object because id or primary fields are invalid",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PostMapping(value = "/admin/v1/upload-exercise-image",
        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResponseObject<Map<String, String>>> upsertExerciseImage(
        @Valid UpsertExerciseImageRequest request) throws IOException {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPLOAD_EXERCISE_IMG,
            exerciseServiceOfAdmin.uploadExerciseImg(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Exercise (and its relationship with Muscles)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=25002; message=Update Session successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10005; message=Can not get object because id or primary fields are invalid",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10006; message=Can not update or delete a depended object",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PutMapping("/admin/v1/update-exercise-and-muscles")
    public ResponseEntity<ApiResponseObject<Exercise>> updateExerciseAndMuscles(
        @Valid @RequestBody UpdateExerciseRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_EXERCISE,
            exerciseServiceOfAdmin.updateExerciseAndMuscles(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Exercise")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=23003; message=Delete Exercise successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10005; message=Can not get object because id or primary fields are invalid",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10006; message=Can not update or delete a depended object",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @DeleteMapping("/admin/v1/delete-exercise")
    public ResponseEntity<ApiResponseObject<Void>> deleteExercise(@Valid @RequestBody DeleteObjectRequest request)
        throws IOException {
        exerciseServiceOfAdmin.deleteExercise(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.DELETE_EXERCISE);
    }
}

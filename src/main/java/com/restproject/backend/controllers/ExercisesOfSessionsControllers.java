package com.restproject.backend.controllers;

import com.restproject.backend.dtos.general.ByIdDto;
import com.restproject.backend.dtos.request.UpdateExercisesOfSessionRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.ExercisesOfSessions;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.ExercisesOfSessionsService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/private")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExercisesOfSessionsControllers {
    ExercisesOfSessionsService exercisesOfSessionsServiceOfAdmin;

    @ResponseBody
    @GetMapping("/admin/v1/get-exercises-of-session-relationship")
    public ResponseEntity<ApiResponseObject<List<ExercisesOfSessions>>> getExercisesOfSessionRelationship(
        @Valid ByIdDto request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_EXERCISES_OF_SESSION_RELATIONSHIP,
            exercisesOfSessionsServiceOfAdmin.getExercisesOfSessionRelationship(request));
    }

    //--Missing Test
    @ResponseBody
    @PutMapping("/admin/v1/update-exercises-of-session")
    public ResponseEntity<ApiResponseObject<List<Exercise>>> updateExercisesOfSession(
        @Valid @RequestBody UpdateExercisesOfSessionRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_SESSION,
            exercisesOfSessionsServiceOfAdmin.updateExercisesOfSession(request));
    }
}
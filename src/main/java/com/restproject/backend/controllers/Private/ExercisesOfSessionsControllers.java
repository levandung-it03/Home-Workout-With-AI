package com.restproject.backend.controllers.Private;

import com.restproject.backend.dtos.request.UpdateExercisesOfSessionRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.request.PaginatedRelationshipRequest;
import com.restproject.backend.dtos.response.ExercisesOfSessionResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Admin.ExercisesOfSessionsService;
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
    @GetMapping("/admin/v1/get-exercises-has-muscles-of-session-pages")   //--For "Show Exercises" of Session.
    public ResponseEntity<ApiResponseObject<TablePagesResponse<ExercisesOfSessionResponse>>>
    getExercisesHasMusclesOfSessionPagesPrioritizeRelationship(
        @Valid @RequestBody PaginatedRelationshipRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_EXERCISES_HAS_MUSCLES_OF_SESSION_PAGES,
            exercisesOfSessionsServiceOfAdmin.getExercisesHasMusclesOfSessionPagesPrioritizeRelationship(request));
    }

    @ResponseBody
    @PutMapping("/admin/v1/update-exercises-of-session")
    public ResponseEntity<ApiResponseObject<List<Exercise>>> updateExercisesOfSession(
        @Valid @RequestBody UpdateExercisesOfSessionRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_SESSION,
            exercisesOfSessionsServiceOfAdmin.updateExercisesOfSession(request));
    }
}
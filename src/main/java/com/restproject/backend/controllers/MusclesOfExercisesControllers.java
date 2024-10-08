package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.response.ExerciseHasMusclesResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.MusclesOfExercisesService;
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
public class MusclesOfExercisesControllers {
    MusclesOfExercisesService musclesOfExercisesService;

    @ResponseBody
    @GetMapping("/admin/v1/get-exercises-has-muscles-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<ExerciseHasMusclesResponse>>> getExercisesHasMusclesPages(
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_EXERCISES_HAS_MUSCLES_PAGES,
            musclesOfExercisesService.getExercisesHasMusclesPages(request));
    }
}

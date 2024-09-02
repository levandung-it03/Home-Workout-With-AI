package com.restproject.backend.controllers;

import com.restproject.backend.dtos.reponse.ApiResponseObject;
import com.restproject.backend.dtos.request.ExercisesByLevelAndMusclesRequest;
import com.restproject.backend.dtos.request.NewExerciseRequest;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Admin.ExerciseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
    public ApiResponseObject<List<Exercise>> getExercisesByLevelAndMuscles(
        @RequestBody ExercisesByLevelAndMusclesRequest request) {
        return new ApiResponseObject<List<Exercise>>().buildSuccessResponse(SucceedCodes.GET_EXS_BY_LV_AND_MUSCLE,
            exerciseService.getExercisesByLevelAndMuscles(request));
    }

    @ResponseBody
    @PostMapping("/admin/v1/create-exercise")
    public ApiResponseObject<Void> createExercise(@RequestBody NewExerciseRequest request) {
        exerciseService.createExercise(request);
        return new ApiResponseObject<Void>().buildSuccessResponse(SucceedCodes.CREATE_EXERCISE);
    }
}

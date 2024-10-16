package com.restproject.backend.controllers;

import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.entities.Muscle;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.MuscleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MuscleControllers {
    MuscleService muscleService;

    @ResponseBody
    @GetMapping({"/admin/v1/get-all-muscles", "/user/v1/get-all-muscles"})
    public ResponseEntity<ApiResponseObject<List<Muscle>>> getAllMuscles() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_ALL_MUSCLE_ENUMS, muscleService.getAllMuscles());
    }
}

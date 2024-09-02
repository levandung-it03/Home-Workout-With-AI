package com.restproject.backend.controllers;

import com.restproject.backend.dtos.reponse.ApiResponseObject;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.enums.SucceedCodes;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnumsControllers {

    @ResponseBody
    @GetMapping({"/admin/v1/get-all-levels", "/user/v1/get-all-levels"})
    public ApiResponseObject<List<Level>> getAllLevels() {
        return new ApiResponseObject<List<Level>>().buildSuccessResponse(SucceedCodes.GET_ALL_LEVEL_ENUMS,
            Level.getAllLevels());
    }

    @ResponseBody
    @GetMapping({"/admin/v1/get-all-muscles", "/user/v1/get-all-muscles"})
    public ApiResponseObject<List<Muscle>> getAllMuscles() {
        return new ApiResponseObject<List<Muscle>>().buildSuccessResponse(SucceedCodes.GET_ALL_MUSCLE_ENUMS,
            Muscle.getAllMuscles());
    }
}

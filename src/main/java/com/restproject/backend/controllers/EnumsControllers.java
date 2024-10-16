package com.restproject.backend.controllers;

import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.EnumsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnumsControllers {
    EnumsService enumsService;

    @ResponseBody
    @GetMapping({"/admin/v1/get-all-levels", "/user/v1/get-all-levels"})
    public ResponseEntity<ApiResponseObject<List<Map<String, String>>>> getAllLevels() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_ALL_LEVEL_ENUMS, enumsService.getAllLevels());
    }

    @ResponseBody
    @GetMapping({"/admin/v1/get-all-genders", "/user/v1/get-all-genders"})
    public ResponseEntity<ApiResponseObject<List<Map<String, String>>>> getAllGenders() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_ALL_GENDER_ENUMS, enumsService.getAllGenders());
    }
}

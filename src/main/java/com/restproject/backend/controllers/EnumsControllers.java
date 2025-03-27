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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnumsControllers {
    EnumsService enumsService;

    @ResponseBody
    @GetMapping({"/api/private/admin/v1/get-all-levels", "/api/private/user/v1/get-all-levels"})
    public ResponseEntity<ApiResponseObject<List<Map<String, String>>>> getAllLevels() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_ALL_LEVEL_ENUMS, enumsService.getAllLevels());
    }

    @ResponseBody
    @GetMapping({"/api/public/admin/v1/get-all-genders", "/api/public/user/v1/get-all-genders",})
    public ResponseEntity<ApiResponseObject<List<Map<String, String>>>> getAllGenders() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_ALL_GENDER_ENUMS, enumsService.getAllGenders());
    }

    @ResponseBody
    @GetMapping({"/api/private/admin/v1/get-all-aims", "/api/private/user/v1/get-all-aims"})
    public ResponseEntity<ApiResponseObject<List<Map<String, String>>>> getAllAims() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_ALL_LEVEL_ENUMS, enumsService.getAllAims());
    }

    @ResponseBody
    @GetMapping({"/api/public/admin/v1/get-all-default-passwords", "/api/public/user/v1/get-all-default-passwords",})
    public ResponseEntity<ApiResponseObject<Map<String, Map<String, String>>>> getAllDefaultPassword() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_ALL_DEFAULT_PASSWORDS_ENUMS,
            enumsService.getDefaultPasswords());
    }
}

package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.NewUserRequest;
import com.restproject.backend.dtos.request.UpdateUserStatusRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.entities.UserInfo;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserControllers {
    UserService userService;

    @ResponseBody
    @PostMapping("/api/public/v1/register-user")
    public ResponseEntity<ApiResponseObject<UserInfo>> registerUser(
        @Valid @RequestBody NewUserRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_USER_INFO,
            userService.registerUser(request));
    }

    @ResponseBody
    @PostMapping("/api/private/admin/v1/update-user-status")
    public ResponseEntity<ApiResponseObject<Void>> updateUserStatus(
        @Valid @RequestBody UpdateUserStatusRequest request) {
        userService.updateUserStatus(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_USER_STATUS);
    }
}

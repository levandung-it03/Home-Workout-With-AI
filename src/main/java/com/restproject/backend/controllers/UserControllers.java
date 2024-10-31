package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.AuthenticationRequest;
import com.restproject.backend.dtos.request.ChangePasswordRequest;
import com.restproject.backend.dtos.request.UpdateUserStatusRequest;
import com.restproject.backend.dtos.request.VerifyOtpRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.entities.Auth.ChangePasswordOtp;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserControllers {
    UserService userService;

    @ResponseBody
    @PutMapping("/api/private/admin/v1/update-user-status")
    public ResponseEntity<ApiResponseObject<HashMap<String, Object>>> updateUserStatus(
        @Valid @RequestBody UpdateUserStatusRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_USER_STATUS,
            userService.updateUserStatus(request));
    }

    @ResponseBody
    @PostMapping("/user/v1/get-otp-to-change-password")
    public ResponseEntity<ApiResponseObject<HashMap<String, Object>>> getOtpToChangePassword(
        @Valid @RequestBody AuthenticationRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_OTP_TO_CHANGE_PASSWORD,
            userService.getOtpToChangePassword(request));
    }

    @ResponseBody
    @PostMapping("/user/v1/verify-change-password-otp")
    public ResponseEntity<ApiResponseObject<ChangePasswordOtp>> verifyOtpToChangePassword(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody VerifyOtpRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.VERIFY_OTP,
            userService.verifyOtpToChangePassword(request, accessToken));
    }

    @ResponseBody
    @PostMapping("/user/v1/change-password")
    public ResponseEntity<ApiResponseObject<Void>> changePassword(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request, accessToken);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CHANGE_PASSWORD);
    }
}

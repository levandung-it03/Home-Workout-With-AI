package com.restproject.backend.controllers.Auth;

import com.restproject.backend.dtos.request.GetOtpRequest;
import com.restproject.backend.dtos.request.VerifyOtpRequest;
import com.restproject.backend.dtos.response.AuthenticationResponse;
import com.restproject.backend.dtos.request.AuthenticationRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.entities.Auth.RegisterOtp;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Auth.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicAuthControllers {
    AuthenticationService authenticationService;

    @ResponseBody
    @PostMapping("/v1/authenticate")
    public ResponseEntity<ApiResponseObject<AuthenticationResponse>> authenticate(
        @Valid @RequestBody AuthenticationRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.AUTHENTICATION,
            authenticationService.authenticate(request));
    }

    @ResponseBody
    @PostMapping("/v1/get-register-otp")
    public ResponseEntity<ApiResponseObject<HashMap<String, Object>>> getRegisterOtp(
        @Valid @RequestBody GetOtpRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_OTP,
            authenticationService.getRegisterOtp(request.getEmail()));
    }

    @ResponseBody
    @PostMapping("/v1/verify-register-otp")
    public ResponseEntity<ApiResponseObject<RegisterOtp>> verifyRegisterOtp(
        @Valid @RequestBody VerifyOtpRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.VERIFY_OTP,
            authenticationService.verifyRegisterOtp(request));
    }

    @ResponseBody
    @PostMapping("/v1/get-forgot-password-otp")
    public ResponseEntity<ApiResponseObject<HashMap<String, Object>>> getForgotPasswordOtp(
        @Valid @RequestBody GetOtpRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_OTP,
            authenticationService.getForgotPasswordOtp(request.getEmail()));
    }
}

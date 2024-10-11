package com.restproject.backend.controllers.Auth;

import com.restproject.backend.dtos.request.GetOtpRequest;
import com.restproject.backend.dtos.request.VerifyOtpRequest;
import com.restproject.backend.dtos.response.AuthenticationResponse;
import com.restproject.backend.dtos.request.AuthenticationRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Auth.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
    @PostMapping("/v1/get-otp")
    public ResponseEntity<ApiResponseObject<HashMap<String, Object>>> getOtp(
        @RequestBody GetOtpRequest request, HttpSession httpSession) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_OTP,
            authenticationService.getOtp(request.getEmail(), httpSession));
    }

    @ResponseBody
    @PostMapping("/v1/verify-otp")
    public ResponseEntity<ApiResponseObject<HashMap<String, Object>>> getOtp(
        @RequestBody VerifyOtpRequest request, HttpSession httpSession) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.VERIFY_OTP,
            authenticationService.verifyOtp(request, httpSession));
    }
}

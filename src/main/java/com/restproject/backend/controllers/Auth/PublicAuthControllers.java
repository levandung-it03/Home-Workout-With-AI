package com.restproject.backend.controllers.Auth;

import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.AuthenticationResponse;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.entities.Auth.RegisterOtp;
import com.restproject.backend.entities.UserInfo;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Auth.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicAuthControllers {
    AuthenticationService authenticationService;

    @GetMapping("/v1/oauth2-authentication-url")
    public ResponseEntity<ApiResponseObject<String>> oauth2Authenticate(
        @RequestParam("loginType") String loginType,
        @RequestParam("redirectUrl") String redirectUrl) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.AUTHENTICATION,
            authenticationService.oauth2GenerateUrl(loginType, redirectUrl));
    }

    @ResponseBody
    @PostMapping("/v1/oauth2-authorization")
    public ResponseEntity<ApiResponseObject<Map<String, Object>>> oauth2GoogleAuthorize(
        @Valid @RequestBody Oauth2AuthorizationRequest req) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.AUTHENTICATION,
            authenticationService.oauth2GoogleAuthorize(req));
    }

    @ResponseBody
    @PostMapping("/v1/oauth2-register-user")
    public ResponseEntity<ApiResponseObject<AuthenticationResponse>> oauth2RegisterUser(
        @Valid @RequestBody NewUserRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_USER_INFO,
            authenticationService.oauth2RegisterUser(request));
    }

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
        @Valid @RequestBody VerifyPublicOtpRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.VERIFY_OTP,
            authenticationService.verifyRegisterOtp(request));
    }

    @ResponseBody
    @PostMapping("/v1/register-user")
    public ResponseEntity<ApiResponseObject<UserInfo>> registerUser(@Valid @RequestBody NewUserRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_USER_INFO,
            authenticationService.registerUser(request));
    }

    @ResponseBody
    @PostMapping("/v1/get-forgot-password-otp")
    public ResponseEntity<ApiResponseObject<HashMap<String, Object>>> getForgotPasswordOtp(
        @Valid @RequestBody GetOtpRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_OTP,
            authenticationService.getForgotPasswordOtp(request.getEmail()));
    }

    @ResponseBody
    @PostMapping("/v1/generate-random-password")
    public ResponseEntity<ApiResponseObject<Void>> generateRandomPassword(
        @Valid @RequestBody VerifyPublicOtpRequest req) {
        authenticationService.generateRandomPassword(req);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.SEND_RANDOM_PASSWORD);
    }
}

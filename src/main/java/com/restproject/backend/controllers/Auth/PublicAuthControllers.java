package com.restproject.backend.controllers.Auth;

import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.AuthenticationResponse;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.entities.Auth.RegisterOtp;
import com.restproject.backend.entities.UserInfo;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get URL to login by Oauth2 Service")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=21009; message=Get Oauth2 URL to authenticate successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=11007; message=Received invalid Redirect URL from Oauth2 Method",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @GetMapping("/v1/oauth2-authentication-url")
    public ResponseEntity<ApiResponseObject<String>> oauth2Authenticate(
        @RequestParam("loginType") String loginType,
        @RequestParam("redirectUrl") String redirectUrl) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_OAUTH2_URL,
            authenticationService.oauth2GenerateUrl(loginType, redirectUrl));
    }

    @Operation(summary = "Authorize User by returned Oauth2 `code`, save register OTP (or not) if this is new a User")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=21010; message=Account has been authorized successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=11002; message=Token or its claims are invalid",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PostMapping("/v1/oauth2-authorization")
    public ResponseEntity<ApiResponseObject<Map<String, Object>>> oauth2GoogleAuthorize(
        @Valid @RequestBody Oauth2AuthorizationRequest req) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.OAUTH2_AUTHORIZATION,
            authenticationService.oauth2GoogleAuthorize(req));
    }

    @Operation(summary = "Register new User by Oauth2 Register Hidden OTP")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=26001; message=Create new User Info successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PostMapping("/v1/oauth2-register-user")
    public ResponseEntity<ApiResponseObject<AuthenticationResponse>> oauth2RegisterUser(
        @Valid @RequestBody NewUserRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_USER_INFO,
            authenticationService.oauth2RegisterUser(request));
    }

    @Operation(summary = "Basic Authentication")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=21001; message=Authenticate successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=11001; message=Email or Password is invalid",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=11004; message=User not found or access denied",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PostMapping("/v1/authenticate")
    public ResponseEntity<ApiResponseObject<AuthenticationResponse>> authenticate(
        @Valid @RequestBody AuthenticationRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.AUTHENTICATION,
            authenticationService.authenticate(request));
    }

    @Operation(summary = "[Register_Step_1] Get OTP to register (confirm email is exists)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=21004; message=Get OTP successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10015; message=User is already existing",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PostMapping("/v1/get-register-otp")
    public ResponseEntity<ApiResponseObject<HashMap<String, Object>>> getRegisterOtp(
        @Valid @RequestBody GetOtpRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_OTP,
            authenticationService.getRegisterOtp(request.getEmail()));
    }

    @Operation(summary = "[Register_Step_2] Verify OTP, and send hidden OTP (prevent CSRF)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=21005; message=Verify OTP successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10013; message=OTP has been expired, please do it again!",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10014; message=OTP is wrong!",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PostMapping("/v1/verify-register-otp")
    public ResponseEntity<ApiResponseObject<RegisterOtp>> verifyRegisterOtp(
        @Valid @RequestBody VerifyPublicOtpRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.VERIFY_OTP,
            authenticationService.verifyRegisterOtp(request));
    }

    @Operation(summary = "[Register_Step_3] Verify hidden OTP and register new User")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=26001; message=Create new User Info successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10011; message=Session was opened too long, please do it again!",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10015; message=User is already existing",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PostMapping("/v1/register-user")
    public ResponseEntity<ApiResponseObject<UserInfo>> registerUser(@Valid @RequestBody NewUserRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_USER_INFO,
            authenticationService.registerUser(request));
    }

    @Operation(summary = "[ForgetPass_Step_1] Get OTP to change password")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=21004; message=Get OTP successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=11004; message=User not found or access denied",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PostMapping("/v1/get-forgot-password-otp")
    public ResponseEntity<ApiResponseObject<HashMap<String, Object>>> getForgotPasswordOtp(
        @Valid @RequestBody GetOtpRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_OTP,
            authenticationService.getForgotPasswordOtp(request.getEmail()));
    }

    @Operation(summary = "[ForgetPass_Step_2] Verify OTP to get new Password")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=21006; message=Send random password into your email successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=11004; message=User not found or access denied",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10013; message=OTP has been expired, please do it again!",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10014; message=OTP is wrong!",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PostMapping("/v1/generate-random-password")
    public ResponseEntity<ApiResponseObject<Void>> generateRandomPassword(
        @Valid @RequestBody VerifyPublicOtpRequest req) {
        authenticationService.generateRandomPassword(req);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.SEND_RANDOM_PASSWORD);
    }
}

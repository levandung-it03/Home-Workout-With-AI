package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.AuthenticationRequest;
import com.restproject.backend.dtos.request.ChangePasswordRequest;
import com.restproject.backend.dtos.request.UpdateUserStatusRequest;
import com.restproject.backend.dtos.request.VerifyAuthOtpRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.entities.Auth.ChangePasswordOtp;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/private")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "Bearer", bearerFormat = "JWT")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserControllers {
    UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user's status")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=27001; message=Update User status",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10005; message=Can not get object because id or primary fields are invalid",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class)))
    })
    @ResponseBody
    @PutMapping("/admin/v1/update-user-status")
    public ResponseEntity<ApiResponseObject<HashMap<String, Object>>> updateUserStatus(
        @Valid @RequestBody UpdateUserStatusRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_USER_STATUS,
            userService.updateUserStatus(request));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get OTP to change User's password")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=21007; message=Authentication successfully and sent OTP",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "401",
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
    @PostMapping("/user/v1/get-otp-to-change-password")
    public ResponseEntity<ApiResponseObject<HashMap<String, Object>>> getOtpToChangePassword(
        @Valid @RequestBody AuthenticationRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_OTP_TO_CHANGE_PASSWORD,
            userService.getOtpToChangePassword(request));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Verify change-password-OTP")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=21005; message=Verify OTP successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
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
    @PostMapping("/user/v1/verify-change-password-otp")
    public ResponseEntity<ApiResponseObject<ChangePasswordOtp>> verifyOtpToChangePassword(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody VerifyAuthOtpRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.VERIFY_OTP,
            userService.verifyOtpToChangePassword(request, accessToken));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Apply new password by hidden OTP (ends of Change-Password progress)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=21008; message=Change password successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=11004; message=User not found or access denied!",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10011; message=Session was opened too long, please do it again! (hidden OTP expired)",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10012; message=Please don't use weird form to submit action! (wrong hidden OTP)",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PostMapping("/user/v1/change-password")
    public ResponseEntity<ApiResponseObject<Void>> changePassword(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request, accessToken);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CHANGE_PASSWORD);
    }
}

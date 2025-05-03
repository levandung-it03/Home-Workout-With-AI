package com.restproject.backend.controllers.Auth;

import com.restproject.backend.dtos.general.TokenDto;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Auth.AuthenticationService;
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

@RestController
@RequestMapping("/api/private/auth")
@SecurityScheme(
    name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "Bearer",
    bearerFormat = "JWT (but require RefreshToken)")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateAuthControllers {
    AuthenticationService authenticationService;

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Refresh new AccessToken for Client's Device")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=21002; message=Refresh Token successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=11002; message=Token or its claims are invalid",
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
    @PostMapping("/v1/refresh-token")  //--RefreshToken as Authorization-Bearer needed.
    public ResponseEntity<ApiResponseObject<TokenDto>> refreshToken(@Valid @RequestBody TokenDto tokenObject) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.REFRESHING_TOKEN,
            authenticationService.refreshToken(tokenObject));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Logout for both Basic Authentication and Oauth2")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=21003; message=Logout successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PostMapping("/v1/logout")
    public ResponseEntity<ApiResponseObject<Void>> logout(@RequestHeader("Authorization") String refreshToken,
                                                          @Valid @RequestBody TokenDto tokenObject) {
        authenticationService.logout(refreshToken, tokenObject.getToken());
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.LOGOUT);
    }
}

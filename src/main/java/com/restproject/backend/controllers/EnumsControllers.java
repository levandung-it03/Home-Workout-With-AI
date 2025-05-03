package com.restproject.backend.controllers;

import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.EnumsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get all Level enums")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=22001; message=Get all Levels successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @GetMapping({"/api/private/admin/v1/get-all-levels", "/api/private/user/v1/get-all-levels"})
    public ResponseEntity<ApiResponseObject<List<Map<String, String>>>> getAllLevels() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_ALL_LEVEL_ENUMS, enumsService.getAllLevels());
    }

    @Operation(summary = "Get all Gender enums")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=22003; message=Get all Genders successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @GetMapping({"/api/public/admin/v1/get-all-genders", "/api/public/user/v1/get-all-genders",})
    public ResponseEntity<ApiResponseObject<List<Map<String, String>>>> getAllGenders() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_ALL_GENDER_ENUMS, enumsService.getAllGenders());
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get all Aim enums")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=22004; message=Get all Aims successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @GetMapping({"/api/private/admin/v1/get-all-aims", "/api/private/user/v1/get-all-aims"})
    public ResponseEntity<ApiResponseObject<List<Map<String, String>>>> getAllAims() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_ALL_AIMS_ENUMS, enumsService.getAllAims());
    }

    @Operation(summary = "Get all default Oauth2 Passwords")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=22005; message=Get all Oauth2 Passwords successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @GetMapping({"/api/public/admin/v1/get-all-default-passwords", "/api/public/user/v1/get-all-default-passwords",})
    public ResponseEntity<ApiResponseObject<Map<String, Map<String, String>>>> getAllDefaultPassword() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_ALL_DEFAULT_PASSWORDS_ENUMS,
            enumsService.getDefaultPasswords());
    }
}

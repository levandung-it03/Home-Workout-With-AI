package com.restproject.backend.controllers;

import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.entities.Muscle;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.MuscleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
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

@RestController
@RequestMapping("/api/private")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "Bearer", bearerFormat = "JWT")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MuscleControllers {
    MuscleService muscleService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get Muscles for another action")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=22002; message=Get all Muscles successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @GetMapping({"/admin/v1/get-all-muscles", "/user/v1/get-all-muscles"})
    public ResponseEntity<ApiResponseObject<List<Muscle>>> getAllMuscles() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_ALL_MUSCLE_ENUMS, muscleService.getAllMuscles());
    }
}

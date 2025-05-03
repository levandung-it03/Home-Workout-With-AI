package com.restproject.backend.controllers;

import com.restproject.backend.dtos.general.ByIdDto;
import com.restproject.backend.dtos.request.UpdateSessionsOfScheduleRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.response.PreviewSubscribedScheduleResponse;
import com.restproject.backend.entities.Session;
import com.restproject.backend.entities.SessionsOfSchedules;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.SessionsOfSchedulesService;
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

import java.util.List;

@RestController
@RequestMapping("/api/private")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "Bearer", bearerFormat = "JWT")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionsOfSchedulesControllers {
    SessionsOfSchedulesService sessionsOfSchedulesService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Show all Sessions of specified Schedule")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=24006; message=Get Sessions in Schedule successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @GetMapping("/admin/v1/get-sessions-of-schedule-relationship")
    public ResponseEntity<ApiResponseObject<List<SessionsOfSchedules>>> getSessionsOfScheduleRelationship(
        @Valid ByIdDto request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SESSIONS_OF_SCHEDULE_RELATIONSHIP,
            sessionsOfSchedulesService.getSessionsOfScheduleRelationship(request));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Preview specified Schedule, which User has been subscribed (to perform exercise)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=24006; message=Get Sessions in Schedule successfully",
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
            description = "code=11004; message=User not found or access denied",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @GetMapping("/user/v1/get-preview-schedule-to-perform")
    public ResponseEntity<ApiResponseObject<PreviewSubscribedScheduleResponse>> getPreviewScheduleToPerform(
        @RequestHeader("Authorization") String accessToken, @Valid ByIdDto request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SESSIONS_OF_SCHEDULE_RELATIONSHIP,
            sessionsOfSchedulesService.getPreviewScheduleToPerform(request, accessToken));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Exercises of specified Session")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=24002; message=Update Schedule successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10004; message=Collection of Ids is invalid",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10005; message=Can not get object because id or primary fields are invalid",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10010; message=Ordinals must be unique",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=16002; message=Schedule was subscribed and can't be changed",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10007; message=Level between relationships don't synchronize to each other",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PutMapping("/admin/v1/update-sessions-of-schedule")
    public ResponseEntity<ApiResponseObject<List<Session>>> updateExercisesOfSession(
        @Valid @RequestBody UpdateSessionsOfScheduleRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_SCHEDULE,
            sessionsOfSchedulesService.updateSessionsOfSchedule(request));
    }
}

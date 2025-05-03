package com.restproject.backend.controllers;

import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.SessionService;
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
@RequestMapping("/api/private")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "Bearer", bearerFormat = "JWT")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionControllers {
    SessionService sessionService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all paginated Sessions (have relational Muscles)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=25004; message=Get Session has Muscles pages successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10008,10009]; message=Invalid [filtering,sorting] field or value",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @GetMapping("/admin/v1/get-sessions-has-muscles-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<Session>>> getSessionsHasMusclesPages(
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SESSIONS_HAS_MUSCLES_PAGES,
            sessionService.getSessionsHasMusclesPages(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Session")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=25001; message=Create new Session successfully",
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
            description = "code=10010; message=Ordinals must be unique",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10007; message=Level between relationships don't synchronize to each other",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=14001; message=Session's Name and Level pair is already existing",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PostMapping("/admin/v1/create-session")
    public ResponseEntity<ApiResponseObject<Session>> createSession(@Valid @RequestBody NewSessionRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_SESSION,
            sessionService.createSession(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Session (and its relationship with Muscles)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=25002; message=Update Session successfully",
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
            description = "code=10006; message=Can not update or delete a depended object",
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
    @PutMapping("/admin/v1/update-session-and-muscles")
    public ResponseEntity<ApiResponseObject<Session>> updateSessionAndMuscles(
        @Valid @RequestBody UpdateSessionRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_SESSION,
            sessionService.updateSessionAndMuscles(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Session")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=25003; message=Delete Session successfully",
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
            description = "code=10006; message=Can not update or delete a depended object",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @DeleteMapping("/admin/v1/delete-session")
    public ResponseEntity<ApiResponseObject<Void>> deleteSession(@Valid @RequestBody DeleteObjectRequest request) {
        sessionService.deleteSession(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.DELETE_SESSION);
    }
}

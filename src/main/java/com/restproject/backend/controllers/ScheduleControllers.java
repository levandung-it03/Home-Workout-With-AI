package com.restproject.backend.controllers;

import com.restproject.backend.dtos.general.ByIdDto;
import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.response.PreviewFullScheduleResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.ScheduleService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/private")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "Bearer", bearerFormat = "JWT")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduleControllers {
    ScheduleService scheduleServiceOfAdmin;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all paginated Schedules (have relational Sessions)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=24004; message=Get Schedule pages successfully",
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
    @GetMapping("/admin/v1/get-schedules-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<Schedule>>> getSchedulesPages(
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SCHEDULES_PAGES,
            scheduleServiceOfAdmin.getSchedulesPages(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all paginated Schedules (have relational Sessions)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=24007; message=Get preview Schedule successfully",
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
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @GetMapping("/admin/v1/get-preview-schedule")
    public ResponseEntity<ApiResponseObject<PreviewFullScheduleResponse>> getPreviewScheduleInfo(
        @Valid ByIdDto request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_PREVIEW_SCHEDULE,
            scheduleServiceOfAdmin.getPreviewSchedule(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Schedule")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=24001; message=Create new Schedule successfully",
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
            description = "code=16001; message=Schedule's Name and Level pair is already existing",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PostMapping("/admin/v1/create-schedule")
    public ResponseEntity<ApiResponseObject<Schedule>> createSchedule(
        @Valid @RequestBody NewScheduleRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_SCHEDULE,
            scheduleServiceOfAdmin.createSchedule(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Schedule (and the relationship with Sessions)")
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
            description = "code=10005; message=Can not get object because id or primary fields are invalid",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10007; message=Level between relationships don't synchronize to each other",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=16003; message=Schedule was subscribed and can't be changed",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PutMapping("/admin/v1/update-schedule")
    public ResponseEntity<ApiResponseObject<Schedule>> updateSchedule(
        @Valid @RequestBody UpdateScheduleRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_SCHEDULE,
            scheduleServiceOfAdmin.updateSchedule(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Schedule")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=24003; message=Delete Schedule successfully",
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
            description = "code=16003; message=Schedule was subscribed and can't be changed",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @DeleteMapping("/admin/v1/delete-schedule")
    public ResponseEntity<ApiResponseObject<Void>> deleteSchedule(@Valid @RequestBody DeleteObjectRequest request) {
        scheduleServiceOfAdmin.deleteSchedule(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.DELETE_SCHEDULE);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get paginated available (not subscribed yet) Schedules for User")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=24004; message=Get Schedule pages successfully",
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
    @GetMapping("/user/v1/get-available-schedules-of-user-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<Schedule>>> getAvailableSchedulesOfUserPages(
        @RequestHeader("Authorization") String accessToken, @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SCHEDULES_PAGES,
            scheduleServiceOfAdmin.getAvailableSchedulesOfUserPages(request, accessToken));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Fastly get Sessions Quantity of specified Schedule to add new data-line (for AI Model)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=24008; message=Get Sessions quantity of Schedule successfully",
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
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @GetMapping("/admin/v1/get-sessions-quantity-of-schedule")
    public ResponseEntity<ApiResponseObject<Map<String, Object>>> getSessionsQuantityOfSchedule(@Valid ByIdDto req) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SESSION_QUANTITY_OF_SCHEDULE,
            scheduleServiceOfAdmin.getSessionsQuantityOfSchedule(req));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "User update Rep-Ratio of a subscribed Schedule")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=24009; message=Update subscribed Schedule rep ratio successfully",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=11004; message=User not found or access denied",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=17001; message=Your account is login in another place and subscribing another schedule, please try again",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=17002; message=Your account coins is not enough!",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
    })
    @ResponseBody
    @PutMapping("/user/v1/update-subscribed-schedule-rep-ratio")
    public ResponseEntity<ApiResponseObject<Void>> updateSubscribedScheduleRepRatio(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody UpdateSubscribedScheduleRepRatioRequest req) {
        scheduleServiceOfAdmin.updateSubscribedScheduleRepRatio(req, accessToken);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_SUBSCRIBED_SCHEDULE_REP_RATIO);
    }
}

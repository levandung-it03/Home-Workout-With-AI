package com.restproject.backend.controllers;

import com.restproject.backend.dtos.general.ByIdDto;
import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.*;
import com.restproject.backend.entities.ExercisesOfSessions;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.SubscriptionService;
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
public class SubscriptionControllers {
    SubscriptionService subscriptionService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get paginated subscriptions by user information")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=28001; message=Get Subscriptions successfully!",
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
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class)))
    })
    @ResponseBody
    @GetMapping("/admin/v1/get-all-subscriptions-by-user-info-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<SubscriptionsInfoResponse>>>
    getSubscriptionsOfUserInfoPages(@Valid PaginatedRelationshipRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SUBSCRIPTIONS_OF_USER_INFO_PAGES,
            subscriptionService.getSubscriptionsOfUserInfoPages(request));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get Schedules-list for User on Home (completed or not)")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=24004; message=Get Schedule pages successfully!",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class)))
    })
    @ResponseBody
    @GetMapping("/user/v1/get-schedules-of-user")
    public ResponseEntity<ApiResponseObject<List<Schedule>>> getSchedulesOfUser(
        @RequestHeader("Authorization") String accessToken,
        @Valid ScheduleByStatusRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SCHEDULES_PAGES,
            subscriptionService.getSchedulesOfUser(request, accessToken));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get Sessions of subscribed-Schedules for User")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=24006; message=Get Sessions in Schedule successfully!",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=14002; message=This Session belongs to an un-subscribed Schedule",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class)))
    })
    @ResponseBody
    @GetMapping("/user/v1/get-sessions-of-subscribed-schedule-of-user")
    public ResponseEntity<ApiResponseObject<SessionToPerformResponse>> getSessionsOfSubscribedScheduleOfUser(
        @RequestHeader("Authorization") String accessToken, @Valid ScheduleInfoToPerformSessionRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SESSIONS_OF_SCHEDULE_RELATIONSHIP,
            subscriptionService.getSessionsOfSubscribedScheduleOfUser(request, accessToken));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get Exercises in Session, which belongs to subscribed-Schedules for User")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=25006; message=Get Exercises in Session successfully!",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=[10001, 10002]; message=Invalid variable type or format of fields",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=14002; message=This Session belongs to an un-subscribed Schedule",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class)))
    })
    @ResponseBody
    @GetMapping("/user/v1/get-exercises-in-session-of-subscribed-schedule-of-user")
    public ResponseEntity<ApiResponseObject<List<ExercisesOfSessions>>> getExercisesInSessionOfSubscribedScheduleOfUser(
        @RequestHeader("Authorization") String accessToken, @Valid ByIdDto request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_EXERCISES_OF_SESSION_RELATIONSHIP,
            subscriptionService.getExercisesInSessionOfSubscribedScheduleOfUser(request, accessToken));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get preview Schedule info (just summary, not all) before User subscribe it")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=24007; message=Get preview Schedule successfully!",
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
    @GetMapping("/user/v1/get-preview-schedule-info-for-user-to-subscribe")
    public ResponseEntity<ApiResponseObject<PreviewScheduleResponse>> getPreviewScheduleInfoForUserToSubscribe(
        @RequestHeader("Authorization") String accessToken, @Valid ByIdDto request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_PREVIEW_SCHEDULE,
            subscriptionService.getPreviewScheduleInfoForUserToSubscribe(request, accessToken));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Subscribing Schedule action from User")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=28002; message=You've been subscribed Schedule successfully!",
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
            description = "code=17001; message=Your account is login in another place and subscribing another schedule, please try again",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=17002; message=Your account coins is not enough!",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=17003; message=You've subscribed this schedule!",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class)))
    })
    @ResponseBody
    @PostMapping("/user/v1/subscribe-schedule")
    public ResponseEntity<ApiResponseObject<Void>> subscribeSchedule(
        @RequestHeader("Authorization") String accessToken, @Valid @RequestBody ScheduleSubscriptionRequest request) {
        subscriptionService.subscribeSchedule(request, accessToken);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.SUBSCRIBE_SCHEDULE);
    }


    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Subscribing recommended Schedule (by AI) action from User")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "code=28002; message=You've been subscribed Schedule successfully!",
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
            description = "code=17001; message=Your account is login in another place and subscribing another schedule, please try again",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=17002; message=Your account coins is not enough!",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=17003; message=You've subscribed this schedule!",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class))),
        @ApiResponse(
            responseCode = "400",
            description = "code=10000; message=Unaware exception's thrown from resource server",
            content = @Content(schema = @Schema(implementation = ApiResponseObject.class)))
    })
    @ResponseBody
    @PostMapping("/user/v1/subscribe-schedule-with-AI")
    public ResponseEntity<ApiResponseObject<Void>> subscribeScheduleWithAI(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody ScheduleSubscriptionWithAIRequest request) {
        subscriptionService.subscribeScheduleWithAI(request, accessToken);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.SUBSCRIBE_SCHEDULE);
    }
}

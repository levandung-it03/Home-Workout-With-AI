package com.restproject.backend.controllers;

import com.restproject.backend.dtos.general.ByIdDto;
import com.restproject.backend.dtos.request.PaginatedRelationshipRequest;
import com.restproject.backend.dtos.request.ScheduleByStatusRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.response.SubscriptionsInfoResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.SubscriptionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionControllers {
    SubscriptionService subscriptionService;

    @ResponseBody
    @GetMapping("/user/v1/get-schedules-of-user")
    public ResponseEntity<ApiResponseObject<List<Schedule>>> getSchedulesOfUser(
        @RequestHeader("Authorization") String accessToken,
        @Valid ScheduleByStatusRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SCHEDULES_PAGES,
            subscriptionService.getSchedulesOfUser(request, accessToken));
    }

    @ResponseBody
    @GetMapping("/user/v1/get-sessions-of-subscribed-schedule-of-user")
    public ResponseEntity<ApiResponseObject<Session>> getSessionsOfSubscribedScheduleOfUser(
        @RequestHeader("Authorization") String accessToken, @Valid ByIdDto request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SESSIONS_OF_SCHEDULE_RELATIONSHIP,
            subscriptionService.getSessionsOfSubscribedScheduleOfUser(request, accessToken));
    }

    @ResponseBody
    @GetMapping("/user/v1/get-exercises-in-session-of-subscribed-schedule-of-user")
    public ResponseEntity<ApiResponseObject<List<Exercise>>> getExercisesInSessionOfSubscribedScheduleOfUser(
        @RequestHeader("Authorization") String accessToken, @Valid ByIdDto request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_EXERCISES_OF_SESSION_RELATIONSHIP,
            subscriptionService.getExercisesInSessionOfSubscribedScheduleOfUser(request, accessToken));
    }

    @ResponseBody
    @GetMapping("/admin/v1/get-all-subscriptions-by-user-info-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<SubscriptionsInfoResponse>>> getSubscriptionsOfUserInfoPages(
        @Valid PaginatedRelationshipRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SUBSCRIPTIONS_OF_USER_INFO_PAGES,
            subscriptionService.getSubscriptionsOfUserInfoPages(request));
    }
}

package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.ScheduleByStatusRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.entities.Schedule;
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
    @GetMapping("/user/v1/get-schedules-of-user-pages")
    public ResponseEntity<ApiResponseObject<List<Schedule>>> getSchedulesPages(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody ScheduleByStatusRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SCHEDULES_PAGES,
            subscriptionService.getSchedulesByStatusPages(request, accessToken));
    }
}

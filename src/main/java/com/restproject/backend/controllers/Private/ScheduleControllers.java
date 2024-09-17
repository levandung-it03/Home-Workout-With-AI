package com.restproject.backend.controllers.Private;

import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.request.NewScheduleRequest;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Admin.ScheduleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduleControllers {
    ScheduleService scheduleServiceOfAdmin;

    @ResponseBody
    @GetMapping("/admin/v1/get-schedules-pages")
    public ResponseEntity<ApiResponseObject<List<Schedule>>> getSchedulesPages(
        @Valid @RequestBody PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SCHEDULES_PAGES,
            scheduleServiceOfAdmin.getSchedulesPages(request));
    }

    @ResponseBody
    @PostMapping("/admin/v1/create-schedule")
    public ResponseEntity<ApiResponseObject<Schedule>> createSchedule(@RequestBody NewScheduleRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_SCHEDULE,
            scheduleServiceOfAdmin.createSchedule(request));
    }
}

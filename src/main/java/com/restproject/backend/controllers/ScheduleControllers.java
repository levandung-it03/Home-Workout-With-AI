package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.DeleteObjectRequest;
import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.dtos.request.UpdateScheduleRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.request.NewScheduleRequest;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.ScheduleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduleControllers {
    ScheduleService scheduleServiceOfAdmin;

    @ResponseBody
    @GetMapping("/admin/v1/get-schedules-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<Schedule>>> getSchedulesPages(
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SCHEDULES_PAGES,
            scheduleServiceOfAdmin.getSchedulesPages(request));
    }

    //--Missing Test
    @ResponseBody
    @PostMapping("/admin/v1/create-schedule")
    public ResponseEntity<ApiResponseObject<Schedule>> createSchedule(
        @Valid @RequestBody NewScheduleRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_SCHEDULE,
            scheduleServiceOfAdmin.createSchedule(request));
    }

    @ResponseBody
    @PutMapping("/admin/v1/update-schedule")
    public ResponseEntity<ApiResponseObject<Schedule>> updateSchedule(
        @Valid @RequestBody UpdateScheduleRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_SCHEDULE,
            scheduleServiceOfAdmin.updateSchedule(request));
    }

    @ResponseBody
    @DeleteMapping("/admin/v1/delete-schedule")
    public ResponseEntity<ApiResponseObject<Void>> deleteSchedule(@Valid @RequestBody DeleteObjectRequest request) {
        scheduleServiceOfAdmin.deleteSchedule(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.DELETE_SCHEDULE);
    }
}

package com.restproject.backend.controllers;

import com.restproject.backend.dtos.reponse.ApiResponseObject;
import com.restproject.backend.dtos.request.NewScheduleRequest;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Admin.ScheduleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduleControllers {
    ScheduleService scheduleServiceOfAdmin;

    @ResponseBody
    @PostMapping("/admin/v1/create-schedule")
    public ApiResponseObject<Void> createSchedule(@RequestBody NewScheduleRequest request) {
        scheduleServiceOfAdmin.createSchedule(request);
        return new ApiResponseObject<Void>().buildSuccessResponse(SucceedCodes.CREATE_SCHEDULE);
    }
}

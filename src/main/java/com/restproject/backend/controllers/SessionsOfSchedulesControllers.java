package com.restproject.backend.controllers;

import com.restproject.backend.dtos.general.ByIdDto;
import com.restproject.backend.dtos.request.UpdateSessionsOfScheduleRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.response.PreviewSubscribedScheduleResponse;
import com.restproject.backend.entities.Session;
import com.restproject.backend.entities.SessionsOfSchedules;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.SessionsOfSchedulesService;
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
public class SessionsOfSchedulesControllers {
    SessionsOfSchedulesService sessionsOfSchedulesService;

    @ResponseBody
    @GetMapping("/admin/v1/get-sessions-of-schedule-relationship")
    public ResponseEntity<ApiResponseObject<List<SessionsOfSchedules>>> getSessionsOfScheduleRelationship(
        @Valid ByIdDto request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SESSIONS_OF_SCHEDULE_RELATIONSHIP,
            sessionsOfSchedulesService.getSessionsOfScheduleRelationship(request));
    }

    @ResponseBody
    @GetMapping("/user/v1/get-preview-schedule-to-perform")
    public ResponseEntity<ApiResponseObject<PreviewSubscribedScheduleResponse>> getPreviewScheduleToPerform(
        @RequestHeader("Authorization") String accessToken, @Valid ByIdDto request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SESSIONS_OF_SCHEDULE_RELATIONSHIP,
            sessionsOfSchedulesService.getPreviewScheduleToPerform(request, accessToken));
    }

    //--Missing Test
    @ResponseBody
    @PutMapping("/admin/v1/update-sessions-of-schedule")
    public ResponseEntity<ApiResponseObject<List<Session>>> updateExercisesOfSession(
        @Valid @RequestBody UpdateSessionsOfScheduleRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_SCHEDULE,
            sessionsOfSchedulesService.updateSessionsOfSchedule(request));
    }
}

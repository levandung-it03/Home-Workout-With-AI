package com.restproject.backend.controllers.Private;

import com.restproject.backend.dtos.request.PaginatedRelationshipRequest;
import com.restproject.backend.dtos.request.UpdateSessionsOfScheduleRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.response.SessionsOfScheduleResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Admin.SessionsOfSchedulesService;
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
    SessionsOfSchedulesService sessionsOfSchedulesServiceOfAdmin;

    @ResponseBody
    @GetMapping("/admin/v1/get-sessions-has-muscles-of-schedule-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<SessionsOfScheduleResponse>>>
    getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship(
        @Valid @RequestBody PaginatedRelationshipRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SESSIONS_HAS_MUSCLES_OF_SCHEDULE_PAGES,
            sessionsOfSchedulesServiceOfAdmin.getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship(request));
    }

    @ResponseBody
    @PutMapping("/admin/v1/update-sessions-of-schedule")
    public ResponseEntity<ApiResponseObject<List<Session>>> updateExercisesOfSession(
        @Valid @RequestBody UpdateSessionsOfScheduleRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_SCHEDULE,
            sessionsOfSchedulesServiceOfAdmin.updateSessionsOfSchedule(request));
    }
}

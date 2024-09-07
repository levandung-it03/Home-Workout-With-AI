package com.restproject.backend.controllers;

import com.restproject.backend.dtos.reponse.ApiResponseObject;
import com.restproject.backend.dtos.request.*;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Admin.SessionService;
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
public class SessionControllers {
    SessionService sessionServiceOfAdmin;

//    @ResponseBody
//    @GetMapping("/admin/v1/get-sessions-by-level")
//    public ResponseEntity<ApiResponseObject<List<Session>>> getSessionsByLevel(
//        @RequestBody SessionsByLevelRequest request) {
//        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SESSIONS_BY_LV,
//            sessionServiceOfAdmin.getSessionsByLevel(request));
//    }

    @ResponseBody
    @GetMapping("/admin/v1/get-sessions-by-page")
    public ResponseEntity<ApiResponseObject<List<Session>>> getPaginatedSessions(
        @Valid @RequestBody PaginatedObjectRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_PAGINATED_SESSIONS,
            sessionServiceOfAdmin.getPaginatedSessions(request));
    }

    @ResponseBody
    @GetMapping("/admin/v1/get-sessions-by-page-and-filtering-fields")
    public ResponseEntity<ApiResponseObject<List<Session>>> getPaginatedFilteringListOfSessions(
        @Valid @RequestBody FilteringPageRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_PAGINATED_FILTERING_SESSIONS,
            sessionServiceOfAdmin.getPaginatedFilteringListOfSessions(request));
    }

    @ResponseBody
    @GetMapping("/admin/v1/get-exercises-of-session-by-page")   //--For "Show Exercises" of Session.
    public ResponseEntity<ApiResponseObject<List<Exercise>>> getPaginatedExercisesOfSession(
        @Valid @RequestBody PaginatedExercisesOfSessionRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_PAGINATED_EXERCISES_OF_SESSION,
            sessionServiceOfAdmin.getPaginatedExercisesOfSession(request));
    }

    @ResponseBody
    @PostMapping("/admin/v1/create-session")
    public ResponseEntity<ApiResponseObject<Session>> createSession(@Valid @RequestBody NewSessionRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_SESSION,
            sessionServiceOfAdmin.createSession(request));
    }

//    @ResponseBody
//    @PostMapping("/admin/v1/update-session")
//    public ResponseEntity<ApiResponseObject<Session>> createSession(@Valid @RequestBody UpdateSessionRequest request) {
//        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_SESSION,
//            sessionServiceOfAdmin.updateSession(request));
//    }
}

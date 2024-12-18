package com.restproject.backend.controllers;

import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.SessionService;
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
public class SessionControllers {
    SessionService sessionServiceOfAdmin;

    @ResponseBody
    @GetMapping("/admin/v1/get-sessions-has-muscles-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<Session>>> getSessionsHasMusclesPages(
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SESSIONS_HAS_MUSCLES_PAGES,
            sessionServiceOfAdmin.getSessionsHasMusclesPages(request));
    }

    //--Missing Test
    @ResponseBody
    @PostMapping("/admin/v1/create-session")
    public ResponseEntity<ApiResponseObject<Session>> createSession(@Valid @RequestBody NewSessionRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_SESSION,
            sessionServiceOfAdmin.createSession(request));
    }


    //--Missing Test
    @ResponseBody
    @PutMapping("/admin/v1/update-session-and-muscles")
    public ResponseEntity<ApiResponseObject<Session>> updateSessionAndMuscles(
        @Valid @RequestBody UpdateSessionRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_SESSION,
            sessionServiceOfAdmin.updateSessionAndMuscles(request));
    }

    @ResponseBody
    @DeleteMapping("/admin/v1/delete-session")
    public ResponseEntity<ApiResponseObject<Void>> deleteSession(@Valid @RequestBody DeleteObjectRequest request) {
        sessionServiceOfAdmin.deleteSession(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.DELETE_SESSION);
    }
}

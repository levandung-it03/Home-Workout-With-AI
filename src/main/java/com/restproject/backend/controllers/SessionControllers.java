package com.restproject.backend.controllers;

import com.restproject.backend.dtos.reponse.ApiResponseObject;
import com.restproject.backend.dtos.request.NewSessionRequest;
import com.restproject.backend.dtos.request.SessionsByLevelRequest;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Admin.SessionService;
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

    @ResponseBody
    @GetMapping("/admin/v1/get-sessions-by-level")
    public ResponseEntity<ApiResponseObject<List<Session>>> getSessionsByLevel(
        @RequestBody SessionsByLevelRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SESSIONS_BY_LV,
            sessionServiceOfAdmin.getSessionsByLevel(request));
    }

    @ResponseBody
    @PostMapping("/admin/v1/create-session")
    public ResponseEntity<ApiResponseObject<Session>> createSession(@RequestBody NewSessionRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_SESSION,
            sessionServiceOfAdmin.createSession(request));
    }
}

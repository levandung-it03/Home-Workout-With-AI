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
    public ApiResponseObject<List<Session>> getSessionsByLevel(@RequestBody SessionsByLevelRequest request) {
        return new ApiResponseObject<List<Session>>().buildSuccessResponse(SucceedCodes.GET_SESSIONS_BY_LV,
            sessionServiceOfAdmin.getSessionsByLevel(request));
    }

    @ResponseBody
    @PostMapping("/admin/v1/create-session")
    public ApiResponseObject<Void> createSession(@RequestBody NewSessionRequest request) {
        sessionServiceOfAdmin.createSession(request);
        return new ApiResponseObject<Void>().buildSuccessResponse(SucceedCodes.CREATE_SESSION);
    }
}

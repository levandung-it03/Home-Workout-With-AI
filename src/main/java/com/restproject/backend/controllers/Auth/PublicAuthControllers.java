package com.restproject.backend.controllers.Auth;

import com.restproject.backend.dtos.response.AuthenticationResponse;
import com.restproject.backend.dtos.request.AuthenticationRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Auth.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicAuthControllers {
    AuthenticationService authenticationService;

    @ResponseBody
    @PostMapping("/v1/authenticate")
    public ResponseEntity<ApiResponseObject<AuthenticationResponse>> authenticate(
        @Valid @RequestBody AuthenticationRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.AUTHENTICATION,
            authenticationService.authenticate(request));
    }
}

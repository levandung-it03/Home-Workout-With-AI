package com.restproject.backend.controllers.Public;

import com.restproject.backend.dtos.general.TokenDto;
import com.restproject.backend.dtos.reponse.AuthenticationResponse;
import com.restproject.backend.dtos.request.AuthenticationRequest;
import com.restproject.backend.dtos.reponse.ApiResponseObject;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.services.Auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthControllers {
    private final AuthenticationService authenticationService;

    @ResponseBody
    @PostMapping("/api/public/auth/v1/authenticate")
    public ApiResponseObject<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return new ApiResponseObject<AuthenticationResponse>().buildSuccessResponse(SucceedCodes.AUTHENTICATION,
            authenticationService.authenticate(request));
    }

    @ResponseBody
    @PostMapping("/api/private/auth/v1/refresh-token")  //--RefreshToken as Authorization-Bearer needed.
    public ApiResponseObject<TokenDto> refreshToken(@RequestBody TokenDto tokenObject) {
        return new ApiResponseObject<TokenDto>().buildSuccessResponse(SucceedCodes.REFRESHING_TOKEN,
            authenticationService.refreshToken(tokenObject));
    }

    @ResponseBody
    @PostMapping("/api/private/auth/v1/logout")
    public ApiResponseObject<Void> logout(@RequestHeader("Authorization") String refreshToken,
                                          @RequestBody TokenDto tokenObject) {
        authenticationService.logout(refreshToken, tokenObject.getToken());
        return new ApiResponseObject<Void>().buildSuccessResponse(SucceedCodes.LOGOUT);
    }
}

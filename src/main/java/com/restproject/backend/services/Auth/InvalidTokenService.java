package com.restproject.backend.services.Auth;

import com.restproject.backend.entities.Auth.InvalidToken;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.InvalidTokenCrud;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class InvalidTokenService {
    private final InvalidTokenCrud invalidTokenCrud;
    private final JwtService jwtService;

    public boolean existByJwtId(String id) {
        return invalidTokenCrud.existsById(id);
    }

    public void saveInvalidToken(String token) throws ApplicationException {
        var parsedJwt = jwtService.verifyTokenOrElseThrow(token, true);
        //--Proactively check expiry time to save into blacklist or not, not throw exception.
        if (new Date().before(parsedJwt.getExpirationTime()))
            invalidTokenCrud.save(InvalidToken.builder().id(parsedJwt.getJWTID())
                .expiryDate(parsedJwt.getExpirationTime().toInstant()).build());
    }
}

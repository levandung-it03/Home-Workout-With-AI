package com.restproject.backend.services.Auth;

import com.nimbusds.jwt.JWTClaimsSet;
import com.restproject.backend.entities.Auth.InvalidToken;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.InvalidTokenCrud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class InvalidTokenService {
    private final InvalidTokenCrud invalidTokenCrud;
    private final JwtService jwtService;

    public boolean existByJwtId(String id) {
        return invalidTokenCrud.existsById(id);
    }

    public void saveInvalidToken(String token) throws ApplicationException {
        JWTClaimsSet parsedJwt = jwtService.verifyTokenOrElseThrow(token, true);
        //--Proactively check expiry time to save into blacklist or not, not throw exception.
        if (new Date().before(parsedJwt.getExpirationTime()))
            invalidTokenCrud.save(InvalidToken.builder().id(parsedJwt.getJWTID())
                .expiryDate(parsedJwt.getExpirationTime().toInstant()).build());
    }
}

package com.restproject.backend.services.Auth;

import com.restproject.backend.dtos.general.TokenDto;
import com.restproject.backend.dtos.response.AuthenticationResponse;
import com.restproject.backend.dtos.request.AuthenticationRequest;
import com.restproject.backend.entities.Auth.RefreshToken;
import com.restproject.backend.entities.Auth.User;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final InvalidTokenService invalidTokenService;
    private final JwtService jwtService;

    /**
     * Steps
     * <br> 1. AuthenticationManager authenticates Credentials (by configured AuthenticationProvider).
     * <br> &nbsp; Failed: throw {@link AuthenticationException}.
     * <br> 2. Check if User is not in-activated.
     * <br> &nbsp; Failed: throw {@link ApplicationException}.
     * <br> 3. Generate a pair of token: AccessToken and RefreshToken to respond to client.
     * <br> 4. Save RefreshToken into Redis (marking that User's login session is starting).
     *
     * @param authObject as Credentials(username, password).
     * @return Response(AccessToken, RefreshToken).
     * @throws AuthenticationException by AuthenticationManager.
     */
    public AuthenticationResponse authenticate(AuthenticationRequest authObject)
        throws AuthenticationException, ApplicationException {
        //--Authenticate User's credentials.
        var authToken = new UsernamePasswordAuthenticationToken(authObject.getUsername(), authObject.getPassword());
        var authUser = (User) authenticationManager.authenticate(authToken).getPrincipal();
        //--Check if this User is in blacklist or not.
        if (!authUser.isActive())
            throw new ApplicationException(ErrorCodes.FORBIDDEN_USER);
        //--Generate Token Pair: Access Token and Refresh Token for response.
        var accessTok = jwtService.generateAccessToken(authUser).get("token");
        var refreshTokMap = jwtService.generateRefreshToken(authUser);
        var refreshTokObj = RefreshToken.builder().id(refreshTokMap.get("id")).build();
        //--Save Refresh Token into Redis for security (logout or immediately denying accessibility).
        refreshTokenService.saveRefreshToken(refreshTokObj);
        return AuthenticationResponse.builder().refreshToken(refreshTokMap.get("token")).accessToken(accessTok).build();
    }

    /**
     * Steps
     * <br> 0. Verify RefreshToken by Oauth2ResourceServer (in JwtDecoder() Bean).
     * <br> 1. Check if AccessToken is expired or not.
     * <br> &nbsp;Failed: throw {@link ApplicationException} as INVALID_TOKEN
     * <br> 2. Retrieve User to check in-activated status.
     * <br> &nbsp;Failed: throw {@link ApplicationException} as FORBIDDEN_USER
     * <br> 3. Generate new AccessToken and pay it back to client.
     *
     * @param tokenObject as expired AccessToken.
     * @return Response(AccessToken).
     * @throws ApplicationException by AccessToken.
     */
    //--Refresh Token has already been verified by Oauth2RrcServer - JwtDecoder.
    public TokenDto refreshToken(TokenDto tokenObject) throws ApplicationException {
        //--Verify expired Access Token to symbolize for a valid refreshing token request.
        var jwtClaimsSet = jwtService.verifyTokenOrElseThrow(tokenObject.getToken(), true);
        //--Get User to check if he/she's in in-activated or not.
        var user = userRepository.findByEmail(jwtClaimsSet.getSubject()).orElseThrow(() ->
            new ApplicationException(ErrorCodes.INVALID_TOKEN));
        if (!user.isActive())
            throw new ApplicationException(ErrorCodes.FORBIDDEN_USER);
        //--Refresh new Access Token.
        var accessTok = jwtService.generateAccessToken(user).get("token");
        return TokenDto.builder().token(accessTok).build();
    }

    /**
     * Cases:
     * <br> 1. Both AccessToken & RefreshToken valid: save AccessToken into blacklist, remove RefreshToken from Redis.
     * <br> 2. AccessToken invalids: immediately respond to client successful result (because something is wrong).
     * <br> 3. AccessToken is expired:
     * <br> &nbsp; 3.1. RefreshToken invalids: respond successful logging-out result to client.
     * <br> &nbsp; 3.2. RefreshToken is expired: respond successful logging-out result to client
     * <br> &nbsp; 3.2. RefreshToken valid: remove RefreshToken and respond successful logging-out result to client at the end of service.
     * <br> 4. AccessToken valid, but RefreshToken doesn't: still save AccessToken into blacklist (because it valid and is still stolen).
     *
     * @param refreshToken from headers (Authorization Bearer).
     * @param accessToken  from body.
     * @throws ApplicationException by RefreshToken.
     */
    public void logout(String refreshToken, String accessToken) throws ApplicationException {
        //--Check if Access Token is valid or expired. If it's not, then save into blacklist.
        invalidTokenService.saveInvalidToken(accessToken);  //--May throw ApplicationExc(INVALID_TOKEN).
        //--If Access Token is invalid, the code can't be touched at here to remove Refresh Token.
        var refreshJwt = jwtService.verifyTokenOrElseThrow(refreshToken, false);
        refreshTokenService.removeRefreshTokenByJwtId(refreshJwt.getJWTID());
    }
}

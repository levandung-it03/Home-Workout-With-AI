package com.restproject.backend.services.Auth;

import com.restproject.backend.dtos.general.TokenDto;
import com.restproject.backend.dtos.request.NewUserRequest;
import com.restproject.backend.dtos.request.VerifyOtpRequest;
import com.restproject.backend.dtos.response.AuthenticationResponse;
import com.restproject.backend.dtos.request.AuthenticationRequest;
import com.restproject.backend.entities.Auth.ForgotPasswordOtp;
import com.restproject.backend.entities.Auth.RefreshToken;
import com.restproject.backend.entities.Auth.RegisterOtp;
import com.restproject.backend.entities.Auth.User;
import com.restproject.backend.entities.UserInfo;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.UserInfoMappers;
import com.restproject.backend.repositories.AuthorityRepository;
import com.restproject.backend.repositories.UserInfoRepository;
import com.restproject.backend.repositories.UserRepository;
import com.restproject.backend.services.ThirdParty.EmailService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final InvalidTokenService invalidTokenService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final RegisterOtpService registerOtpService;
    private final ForgotPasswordOtpService forgotPasswordOtpService;
    private final UserInfoRepository userInfoRepository;
    private final UserInfoMappers userInfoMappers;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder userPasswordEncoder;

    @Value("${services.back-end.user-info.default-coins}")
    private int defaultCoins;

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
        var authToken = new UsernamePasswordAuthenticationToken(authObject.getEmail(), authObject.getPassword());
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

    public HashMap<String, Object> getRegisterOtp(String email) {
        String otp = generateRandomOtp(4);
        //--Remove the previous OTP code in session if it's existing.
        if (registerOtpService.findByEmail(email).isPresent())
            registerOtpService.deleteByEmail(email);

        String otpMailMessage = String.format("""
            <div>
                <p style="font-size: 18px">Do not share this information to anyone. Please secure this characters!</p>
                <h2>User Email: <b>%s</b></h2>
                <h2>OTP: <b>%s</b></h2>
            </div>
        """, email, otp);
        emailService.sendSimpleEmail(email, "OTP Code by Home Workout With AI", otpMailMessage);

        //--Save into session for the next actions.
        registerOtpService.save(RegisterOtp.builder().id(email).otpCode(otp).build());

        // Schedule a task to remove the OTP after 5 minutes (or your preferred timeout).
        scheduler.schedule(() -> {
            registerOtpService.deleteByEmail(email);
            log.info("OTP for " + email + " has expired and been removed.");
        }, 5, TimeUnit.MINUTES);

        return new HashMap<>(Map.of("ageInSeconds", 5*60));
    }

    public RegisterOtp verifyRegisterOtp(VerifyOtpRequest request) {
        //--Remove the previous OTP code in session if it's existing.
        Optional<RegisterOtp> existsOtp = registerOtpService.findByEmail(request.getEmail());
        if (existsOtp.isPresent() && existsOtp.get().getOtpCode().equals(request.getOtpCode())) {
            var result = RegisterOtp.builder().id(request.getEmail()).otpCode(generateRandomOtp(4)).build();
            registerOtpService.deleteByEmail(request.getEmail());
            registerOtpService.save(result);

            // Schedule a task to remove the OTP after 15 minutes (similar to accessToken lifetime).
            scheduler.schedule(() -> {
                registerOtpService.deleteByEmail(request.getEmail());
                log.info("OTP for " + request.getEmail() + " has expired and been removed.");
            }, 15, TimeUnit.MINUTES);

            return result;
        } else throw new ApplicationException(ErrorCodes.VERIFY_OTP);
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public UserInfo registerUser(NewUserRequest request) throws ApplicationException {
        var removedOtp = registerOtpService.findByEmail(request.getEmail())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.VERIFY_OTP));
        registerOtpService.deleteByEmail(removedOtp.getId());

        UserInfo newUserInfo = userInfoMappers.insertionToPlain(request);
        User newUser = User.builder()
            .email(request.getEmail())
            .password(userPasswordEncoder.encode(request.getPassword()))
            .createdTime(LocalDateTime.now())
            .authorities(List.of(
                authorityRepository.findByAuthorityName("ROLE_USER").orElseThrow(RuntimeException::new)
            ))
            .active(true)
            .build();
        User savedUser = userRepository.save(newUser);

        newUserInfo.setUser(savedUser);
        newUserInfo.setCoins((long) defaultCoins);  //--Default coins for new User.
        return userInfoRepository.save(newUserInfo);    //--FetchType.LAZY will ignore User
    }

    public HashMap<String, Object> getForgotPasswordOtp(String email) {
        if (!userRepository.existsByEmail(email))
            throw new ApplicationException(ErrorCodes.FORBIDDEN_USER);

        String otp = generateRandomOtp(4);
        //--Remove the previous OTP code in session if it's existing.
        if (forgotPasswordOtpService.findByEmail(email).isPresent())
            forgotPasswordOtpService.deleteByEmail(email);

        String otpMailMessage = String.format("""
            <div>
                <p style="font-size: 18px">Do not share this information to anyone. Please secure these characters!</p>
                <h2>User Email: <b>%s</b></h2>
                <h2>OTP: <b>%s</b></h2>
            </div>
        """, email, otp);
        emailService.sendSimpleEmail(email, "OTP Code for new password by Home Workout With AI", otpMailMessage);

        //--Save into session for the next actions.
        forgotPasswordOtpService.save(ForgotPasswordOtp.builder().id(email).otpCode(otp).build());

        // Schedule a task to remove the OTP after 5 minutes (or your preferred timeout).
        scheduler.schedule(() -> {
            forgotPasswordOtpService.deleteByEmail(email);
            log.info("OTP for " + email + " has expired and been removed.");
        }, 5, TimeUnit.MINUTES);

        return new HashMap<>(Map.of("ageInSeconds", 5*60));
    }

    public static String generateRandomOtp(int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            otp.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        return otp.toString();
    }

    public void generateRandomPassword(VerifyOtpRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.FORBIDDEN_USER));
        var removedOtp = forgotPasswordOtpService.findByEmail(request.getEmail())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.VERIFY_OTP));
        forgotPasswordOtpService.deleteByEmail(removedOtp.getId());

        String newPassword = AuthenticationService.generateRandomOtp(6);
        String newPassMessage = String.format("""
            <div>
                <p style="font-size: 18px">Do not share this information to anyone. Please secure these characters!</p>
                <h2>User Email: <b>%s</b></h2>
                <h2>New Password: <b>%s</b></h2>
            </div>
        """, request.getEmail(), newPassword);
        emailService.sendSimpleEmail(request.getEmail(), "New password by Home Workout With AI", newPassMessage);

        user.setPassword(userPasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}

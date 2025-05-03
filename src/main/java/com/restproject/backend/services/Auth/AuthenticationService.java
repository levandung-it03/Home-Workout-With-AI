package com.restproject.backend.services.Auth;

import com.restproject.backend.dtos.general.TokenDto;
import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.AuthenticationResponse;
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
import com.restproject.backend.services.ThirdParty.CryptoService;
import com.restproject.backend.services.ThirdParty.EmailService;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.restproject.backend.enums.DefaultOauth2Password.*;
import static com.restproject.backend.enums.DefaultOauth2Password.isDefaultOauth2Password;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private final String SEPARATOR = "_SePaRaToR_";

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
    private final WebClient webClient;
    private final PasswordEncoder userPasswordEncoder;
    private final CryptoService cryptoService;

    @Value("${services.back-end.user-info.default-coins}")
    private int defaultCoins;
    @Value("${services.security.max-hidden-otp-age-min}")
    private int maxHiddenOtpAgeMin;
    @Value("${services.security.max-otp-age-min}")
    private int maxOtpAgeMin;
    @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
    private String authUri;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${services.front-end.domain-name}")
    private String clientDomain;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;
    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String getTokenUri;
    @Value("${spring.security.oauth2.client.provider.google.revoke-token-uri}")
    private String revokeTokenUrl;
    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String getUserInfoUri;
    @Value("${spring.security.oauth2.client.registration.google.scope}")
    private Set<String> scopes;

    public static String generateRandomOtp(int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            otp.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        return otp.toString();
    }

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
        var decodedPass = cryptoService.decrypt(authObject.getPassword());
        var authToken = new UsernamePasswordAuthenticationToken(authObject.getEmail(), decodedPass);
        var authUser = (User) authenticationManager.authenticate(authToken).getPrincipal();
        //--Check if this User is in blacklist or not.
        if (!authUser.isActive() || isDefaultOauth2Password(authObject.getPassword()))
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
        //--Revoke JWT from oauth2 server.
        if (Objects.nonNull(refreshJwt.getClaim("oauth2Type"))
            && refreshJwt.getClaim("oauth2Type").toString().equals(GOOGLE.getVirtualPassword()))
            webClient.post()
                .uri(revokeTokenUrl + refreshJwt.getClaim("oauth2RefreshToken").toString())
                .retrieve().bodyToMono(Void.class).block();
    }

    public HashMap<String, Object> getRegisterOtp(String email) {
        if (userRepository.existsByEmail(email))
            throw new ApplicationException(ErrorCodes.USER_EXISTING);

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
        emailService.sendSimpleEmail(email, "Verify Email OTP Code for registering by Home Workout With AI",
            otpMailMessage);

        //--Save into session for the next actions.
        registerOtpService.save(RegisterOtp.builder().id(email).otpCode(otp).build());

        // Schedule a task to remove the OTP after 5 minutes (or your preferred timeout).
        scheduler.schedule(() -> {
            registerOtpService.deleteByEmail(email);
            log.info("Register OTP for {} has expired and been removed.", email);
        }, maxOtpAgeMin, TimeUnit.MINUTES);

        return new HashMap<>(Map.of("ageInSeconds", maxOtpAgeMin * 60));
    }

    public RegisterOtp verifyRegisterOtp(VerifyPublicOtpRequest request) {
        var existsOtp = registerOtpService.findByEmail(request.getEmail())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.OTP_IS_KILLED));
        if (!existsOtp.getOtpCode().equals(request.getOtpCode()))
            throw new ApplicationException(ErrorCodes.OTP_NOT_FOUND);

        var result = RegisterOtp.builder().id(request.getEmail()).otpCode(generateRandomOtp(4)).build();
        registerOtpService.deleteByEmail(request.getEmail());
        registerOtpService.save(result);    //--Creating a Hidden OTP to prevent weird submitting form.

        scheduler.schedule(() -> {
            registerOtpService.deleteByEmail(request.getEmail());
            log.info("Hidden Register OTP for {} has expired and been removed.", request.getEmail());
        }, maxHiddenOtpAgeMin, TimeUnit.MINUTES);

        return result;
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public UserInfo registerUser(NewUserRequest request) throws ApplicationException {
        var removedOtp = registerOtpService.findByEmail(request.getEmail())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.HIDDEN_OTP_IS_KILLED));
        registerOtpService.deleteByEmail(removedOtp.getId());

        if (userRepository.existsByEmail(request.getEmail()))
            throw new ApplicationException(ErrorCodes.USER_EXISTING);

        UserInfo newUserInfo = userInfoMappers.insertionToPlain(request);
        User newUser = User.builder()
            .email(request.getEmail())
            .password(isDefaultOauth2Password(request.getPassword())
                ? request.getPassword() : userPasswordEncoder.encode(request.getPassword()))
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
        var userQueryResult = userRepository.findByEmail(email);
        if (userQueryResult.isEmpty() || !userQueryResult.get().isActive()
            || isDefaultOauth2Password(userQueryResult.get().getPassword()))
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
        emailService.sendSimpleEmail(email, "OTP Code for new password by Home Workout With AI",
            otpMailMessage);

        //--Save into session for the next actions.
        forgotPasswordOtpService.save(ForgotPasswordOtp.builder().id(email).otpCode(otp).build());

        // Schedule a task to remove the OTP after 5 minutes (or your preferred timeout).
        scheduler.schedule(() -> {
            forgotPasswordOtpService.deleteByEmail(email);
            log.info("Forgot Password OTP for {} has expired and been removed.", email);
        }, maxOtpAgeMin, TimeUnit.MINUTES);

        return new HashMap<>(Map.of("ageInSeconds", maxOtpAgeMin * 60));
    }

    public void generateRandomPassword(VerifyPublicOtpRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.FORBIDDEN_USER));
        if (!user.isActive()) throw new ApplicationException(ErrorCodes.FORBIDDEN_USER);

        var removedOtp = forgotPasswordOtpService.findByEmail(request.getEmail())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.OTP_IS_KILLED));
        if (!removedOtp.getOtpCode().equals(request.getOtpCode()))
            throw new ApplicationException(ErrorCodes.OTP_NOT_FOUND);
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

    public String oauth2GenerateUrl(String loginType, String redirectUrl) {
        String oauth2Request = clientDomain;
        try {
            String finalRedUrl = (redirectUrl == null || redirectUrl.isEmpty())
                ? (clientDomain + redirectUri)
                : URLDecoder.decode(redirectUrl, "UTF-8");
            if (loginType.equals(GOOGLE.getVirtualPassword()))
                oauth2Request = authUri +
                    "?client_id=" + clientId +
                    "&redirect_uri=" + finalRedUrl +
                    "&response_type=code" +
                    "&scope=" + String.join("%20", scopes) +
                    "&access_type=offline" +
                    "&prompt=consent";
            else if (loginType.equals(FACEBOOK.getVirtualPassword()))
                oauth2Request = clientDomain;
        } catch (Exception e) {
            throw new ApplicationException(ErrorCodes.WEIRD_REDIRECT_URL);
        }
        return cryptoService.encrypt(oauth2Request);
    }

    @Transactional(rollbackOn = RuntimeException.class)
    public Map<String, Object> oauth2GoogleAuthorize(Oauth2AuthorizationRequest request) {
        Map<String, Object> authResponse = new HashMap<>();
        if (request.getLoginType().equals(GOOGLE.getVirtualPassword())) {
            //--Publisher: Getting Access_Token from Oauth2 Server.
            //--Using .block() to make result returns synchronously.
            Map<String, Object> getTokenResponse = webClient.post().uri(getTokenUri)
                .body(BodyInserters.fromFormData("client_id", clientId)
                    .with("client_secret", clientSecret)
                    .with("redirect_uri", clientDomain + redirectUri)
                    .with("grant_type", "authorization_code")
                    .with("code", URLDecoder.decode(request.getCode(), StandardCharsets.UTF_8))
                ).retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}).block();
            if (Objects.isNull(getTokenResponse)
                || Objects.isNull(getTokenResponse.get("access_token"))
                || getTokenResponse.get("access_token").toString().isBlank())
                throw new ApplicationException(ErrorCodes.INVALID_TOKEN);

            //--Publisher: Getting User_Info from Oauth2 Server by Access_Token.
            //--Using .block() to make result returns synchronously and auto-return .map(res -> res) as Map.
            authResponse = webClient.get().uri(getUserInfoUri)
                .header("Authorization", "Bearer " + getTokenResponse.get("access_token"))
                .retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}).block();
            if (Objects.isNull(authResponse) || authResponse.isEmpty())
                throw new ApplicationException(ErrorCodes.INVALID_TOKEN);

            String email = authResponse.get("email").toString();
            Optional<User> user = userRepository.findByEmail(email);
            authResponse.put("isExistingUserInfo", user.isPresent());
            //--If (User is NOT existing && found account isn't the right Oauth2 type)
            if (user.isEmpty() || !user.get().getPassword().equals(GOOGLE.getVirtualPassword())) {
                //--Save Oauth2-Refresh-Token as a virtual code.
                var registerCode = GOOGLE.getVirtualPassword() + SEPARATOR + getTokenResponse.get("refresh_token").toString();
                registerOtpService.save(RegisterOtp.builder().id(email).otpCode(registerCode).build());
                //--Schedule a task to remove the OTP after 5 minutes (or your preferred timeout).
                scheduler.schedule(() -> {
                    registerOtpService.deleteByEmail(email);
                    log.info("Register OTP for {} has expired and been removed.", email);
                }, maxOtpAgeMin, TimeUnit.MINUTES);
                //--Virtual OTP Code to avoid XSS attack.
                authResponse.put("otpRegisterCode", registerCode);
            } else {
                var refreshTokMap = jwtService.generateOauth2RefreshToken(user.get(),
                    getTokenResponse.get("refresh_token").toString(), GOOGLE.getVirtualPassword());
                var refreshTokObj = RefreshToken.builder().id(refreshTokMap.get("id")).build();
                //--Save Refresh Token into Redis for security (logout or immediately denying accessibility).
                refreshTokenService.saveRefreshToken(refreshTokObj);
                return Map.of(
                    "isExistingUserInfo", true,
                    "accessToken", jwtService.generateAccessToken(user.get()).get("token"),
                    "refreshToken", refreshTokMap.get("token")
                );
            }
        }
        return authResponse;
    }

    @Transactional(rollbackOn = RuntimeException.class)
    public AuthenticationResponse oauth2RegisterUser(NewUserRequest request) {
        UserInfo userInfo = this.registerUser(request);
        String[] oauth2TokenInfo = request.getOtpCode().split(SEPARATOR);

        var accessTok = jwtService.generateAccessToken(userInfo.getUser()).get("token");
        var refreshTokMap = jwtService.generateOauth2RefreshToken(userInfo.getUser(), oauth2TokenInfo[1], oauth2TokenInfo[0]);
        //--Save Refresh Token into Redis for security (logout or immediately denying accessibility).
        refreshTokenService.saveRefreshToken(RefreshToken.builder().id(refreshTokMap.get("id")).build());
        return AuthenticationResponse.builder().accessToken(accessTok).refreshToken(refreshTokMap.get("token")).build();
    }
}

package com.restproject.backend.services;

import com.restproject.backend.dtos.request.AuthenticationRequest;
import com.restproject.backend.dtos.request.ChangePasswordRequest;
import com.restproject.backend.dtos.request.UpdateUserStatusRequest;
import com.restproject.backend.dtos.request.VerifyOtpRequest;
import com.restproject.backend.entities.Auth.ChangePasswordOtp;
import com.restproject.backend.entities.Auth.User;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.UserRepository;
import com.restproject.backend.services.Auth.ChangePasswordOtpService;
import com.restproject.backend.services.Auth.JwtService;
import com.restproject.backend.services.ThirdParty.EmailService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.restproject.backend.services.Auth.AuthenticationService.generateRandomOtp;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    UserRepository userRepository;
    AuthenticationManager authenticationManager;
    EmailService emailService;
    ChangePasswordOtpService changePasswordOtpService;
    PasswordEncoder userPasswordEncoder;
    JwtService jwtService;

    public HashMap<String, Object> updateUserStatus(UpdateUserStatusRequest request) {
        if (!userRepository.existsById(request.getUserId()))
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);

        userRepository.updateStatusByUserId(request.getUserId(), request.getNewStatus());
        return new HashMap<>(Map.ofEntries(
            Map.entry("userId", request.getUserId()),
            Map.entry("newStatus", request.getNewStatus())
        ));
    }


    public HashMap<String, Object> getOtpToChangePassword(AuthenticationRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        var authUser = (User) authenticationManager.authenticate(authToken).getPrincipal();
        //--Check if this User is in blacklist or not.
        if (!authUser.isActive())
            throw new ApplicationException(ErrorCodes.FORBIDDEN_USER);

        String otp = generateRandomOtp(4);
        //--Remove the previous OTP code in session if it's existing.
        if (changePasswordOtpService.findByEmail(request.getEmail()).isPresent())
            changePasswordOtpService.deleteByEmail(request.getEmail());

        String otpMailMessage = String.format("""
            <div>
                <p style="font-size: 18px">Do not share this information to anyone. Please secure these characters!</p>
                <h2>User Email: <b>%s</b></h2>
                <h2>OTP: <b>%s</b></h2>
            </div>
        """, request.getEmail(), otp);
        emailService.sendSimpleEmail(request.getEmail(), "OTP Code for new password by Home Workout With AI",
            otpMailMessage);

        //--Save into session for the next actions.
        changePasswordOtpService.save(ChangePasswordOtp.builder().id(request.getEmail()).otpCode(otp).build());

        // Schedule a task to remove the OTP after 5 minutes (or your preferred timeout).
        scheduler.schedule(() -> {
            changePasswordOtpService.deleteByEmail(request.getEmail());
            log.info("Change Password OTP for {} has expired and been removed.", request.getEmail());
        }, 5, TimeUnit.MINUTES);

        return new HashMap<>(Map.of("ageInSeconds", 5*60));
    }

    public ChangePasswordOtp verifyOtpToChangePassword(VerifyOtpRequest request, String accessToken) {
        //--Remove the previous OTP code in session if it's existing.
        Optional<ChangePasswordOtp> existsOtp = changePasswordOtpService.findByEmail(request.getEmail());
        if (existsOtp.isPresent() && existsOtp.get().getOtpCode().equals(request.getOtpCode())) {
            var result = ChangePasswordOtp.builder().id(request.getEmail()).otpCode(generateRandomOtp(4)).build();
            changePasswordOtpService.deleteByEmail(request.getEmail());
            changePasswordOtpService.save(result);

            // Schedule a task to remove the OTP after 15 minutes (similar to accessToken lifetime).
            scheduler.schedule(() -> {
                changePasswordOtpService.deleteByEmail(request.getEmail());
                log.info("Change Password Hidden OTP for {} has expired and been removed.", request.getEmail());
            }, 15, TimeUnit.MINUTES);

            return result;
        } else throw new ApplicationException(ErrorCodes.VERIFY_OTP);
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public void changePassword(ChangePasswordRequest request, String accessToken) {
        var user = userRepository.findByEmail(jwtService.readPayload(accessToken).get("sub"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.FORBIDDEN_USER));

        Optional<ChangePasswordOtp> existsOtp = changePasswordOtpService.findByEmail(user.getEmail());
        if (existsOtp.isEmpty() || !existsOtp.get().getOtpCode().equals(request.getOtpCode()))
            throw new ApplicationException(ErrorCodes.VERIFY_OTP);

        changePasswordOtpService.deleteByEmail(user.getEmail());
        user.setPassword(userPasswordEncoder.encode(request.getPassword()));
        userRepository.updateUserPassword(user);
    }
}

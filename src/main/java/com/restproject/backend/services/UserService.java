package com.restproject.backend.services;

import com.restproject.backend.dtos.request.NewUserRequest;
import com.restproject.backend.dtos.request.UpdateUserStatusRequest;
import com.restproject.backend.dtos.request.VerifyOtpRequest;
import com.restproject.backend.entities.Auth.User;
import com.restproject.backend.entities.UserInfo;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.UserInfoMappers;
import com.restproject.backend.repositories.AuthorityRepository;
import com.restproject.backend.repositories.UserInfoRepository;
import com.restproject.backend.repositories.UserRepository;
import com.restproject.backend.services.Auth.AuthenticationService;
import com.restproject.backend.services.Auth.ForgotPasswordOtpService;
import com.restproject.backend.services.Auth.RegisterOtpService;
import com.restproject.backend.services.ThirdParty.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final RegisterOtpService registerOtpService;
    private final ForgotPasswordOtpService forgotPasswordOtpService;
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserInfoMappers userInfoMappers;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder userPasswordEncoder;
    private final EmailService emailService;

    @Value("${services.back-end.user-info.default-coins}")
    private int defaultCoins;

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

    public HashMap<String, Object> updateUserStatus(UpdateUserStatusRequest request) {
        if (!userRepository.existsById(request.getUserId()))
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);

        userRepository.updateStatusByUserId(request.getUserId(), request.getNewStatus());
        return new HashMap<>(Map.ofEntries(
            Map.entry("userId", request.getUserId()),
            Map.entry("newStatus", request.getNewStatus())
        ));
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

        user.setPassword(newPassword);
        userRepository.save(user);
    }
}

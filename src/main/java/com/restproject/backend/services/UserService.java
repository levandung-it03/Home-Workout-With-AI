package com.restproject.backend.services;

import com.restproject.backend.dtos.request.NewUserRequest;
import com.restproject.backend.dtos.request.UpdateUserStatusRequest;
import com.restproject.backend.entities.Auth.User;
import com.restproject.backend.entities.UserInfo;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.UserInfoMappers;
import com.restproject.backend.repositories.AuthorityRepository;
import com.restproject.backend.repositories.UserInfoRepository;
import com.restproject.backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserInfoMappers userInfoMappers;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder userPasswordEncoder;

    @Value("${services.back-end.user-info.default-coins}")
    private int defaultCoins;

    @Transactional(rollbackOn = {RuntimeException.class})
    public UserInfo registerUser(NewUserRequest request) throws ApplicationException {
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
        newUserInfo.setCoins((long) defaultCoins);    //--Default coins for new User.
        return userInfoRepository.save(newUserInfo);    //--FetchType.LAZY will ignore User
    }

    public void updateUserStatus(UpdateUserStatusRequest request) {
        if (!userRepository.existsById(request.getUserId()))
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);

        userRepository.updateStatusByUserId(request.getUserId(), request.getNewStatus());
    }
}

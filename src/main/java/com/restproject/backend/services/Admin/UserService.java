package com.restproject.backend.services.Admin;

import com.restproject.backend.dtos.request.UpdateUserStatusRequest;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;

    public void updateUserStatus(UpdateUserStatusRequest request) {
        if (!userRepository.existsById(request.getUserId()))
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);

        userRepository.updateStatusByUserId(request.getUserId(), request.getNewStatus());
    }
}

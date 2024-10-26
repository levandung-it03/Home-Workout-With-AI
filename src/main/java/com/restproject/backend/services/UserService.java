package com.restproject.backend.services;

import com.restproject.backend.dtos.request.UpdateUserStatusRequest;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public HashMap<String, Object> updateUserStatus(UpdateUserStatusRequest request) {
        if (!userRepository.existsById(request.getUserId()))
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);

        userRepository.updateStatusByUserId(request.getUserId(), request.getNewStatus());
        return new HashMap<>(Map.ofEntries(
            Map.entry("userId", request.getUserId()),
            Map.entry("newStatus", request.getNewStatus())
        ));
    }
}

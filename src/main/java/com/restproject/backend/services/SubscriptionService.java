package com.restproject.backend.services;

import com.restproject.backend.dtos.request.ScheduleByStatusRequest;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.repositories.SubscriptionRepository;
import com.restproject.backend.services.Auth.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionService {
    SubscriptionRepository subscriptionRepository;
    JwtService jwtService;

    public List<Schedule> getSchedulesByStatusPages(ScheduleByStatusRequest request, String accessToken) {
        return subscriptionRepository.getAllScheduleByUsernameAndStatus(
            jwtService.readPayload(accessToken).get("subject"),
            request.getIsCompleted()
        );
    }
}

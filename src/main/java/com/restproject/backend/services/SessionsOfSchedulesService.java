package com.restproject.backend.services;

import com.restproject.backend.dtos.general.ByIdDto;
import com.restproject.backend.dtos.general.SessionInfoDto;
import com.restproject.backend.dtos.request.UpdateSessionsOfScheduleRequest;
import com.restproject.backend.entities.Session;
import com.restproject.backend.entities.SessionsOfSchedules;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.ScheduleRepository;
import com.restproject.backend.repositories.SessionRepository;
import com.restproject.backend.repositories.SessionsOfSchedulesRepository;
import com.restproject.backend.repositories.SubscriptionRepository;
import com.restproject.backend.services.Auth.JwtService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionsOfSchedulesService {
    SessionsOfSchedulesRepository sessionsOfSchedulesRepository;
    ScheduleRepository scheduleRepository;
    SessionRepository sessionRepository;
    SubscriptionRepository subscriptionRepository;

    //--Missing Test
    @Transactional(rollbackOn = {RuntimeException.class})
    public List<Session> updateSessionsOfSchedule(UpdateSessionsOfScheduleRequest request) {
        var updatedSchedule = scheduleRepository.findById(request.getScheduleId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        var sessionsFromDB = sessionRepository.findAllByIdIn(request.getSessionsInfo()
            .stream().map(SessionInfoDto::getSessionId).toList());
        if (sessionsFromDB.size() != request.getSessionsInfo().size())
            throw new ApplicationException(ErrorCodes.INVALID_IDS_COLLECTION);
        if (subscriptionRepository.existsByScheduleScheduleId(request.getScheduleId()))
            throw new ApplicationException(ErrorCodes.SCHEDULE_SUBSCRIPTIONS_VIOLATION);

        List<SessionInfoDto> sessionsInfo = request.getSessionsInfo()
            .stream().sorted(Comparator.comparing(SessionInfoDto::getSessionId)).toList();
        ArrayList<SessionsOfSchedules> savedRelationships = new ArrayList<>();
        for (var index = 0; index < request.getSessionsInfo().size(); index++) {
            savedRelationships.add(SessionsOfSchedules.builder()
                .schedule(updatedSchedule)
                .session(sessionsFromDB.get(index))
                .ordinal(sessionsInfo.get(index).getOrdinal())
                .build());
        }

        sessionsOfSchedulesRepository.deleteAllByScheduleScheduleId(updatedSchedule.getScheduleId());
        sessionsOfSchedulesRepository.flush();
        sessionsOfSchedulesRepository.saveAll(savedRelationships);
        return sessionsFromDB;
    }

    public List<SessionsOfSchedules> getSessionsOfScheduleRelationship(ByIdDto request) {
        return sessionsOfSchedulesRepository.findAllById(request.getId());
    }
}

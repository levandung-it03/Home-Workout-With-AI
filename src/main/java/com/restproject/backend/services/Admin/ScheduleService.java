package com.restproject.backend.services.Admin;

import com.restproject.backend.dtos.request.NewScheduleRequest;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.entities.SessionsOfSchedules;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Level;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.ScheduleRepository;
import com.restproject.backend.repositories.SessionRepository;
import com.restproject.backend.repositories.SessionsOfSchedulesRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduleService {
    ScheduleRepository scheduleRepository;
    SessionRepository sessionRepository;
    SessionsOfSchedulesRepository sessionsOfSchedulesRepository;

    @Transactional(rollbackOn = {Exception.class})
    public void createSchedule(NewScheduleRequest request) throws ApplicationException {
        var savedSchedule = scheduleRepository.save(Schedule.builder()
            .name(request.getName())
            .description(request.getDescription())
            .level(Level.getByLevel(request.getLevel()))
            .build());

        sessionsOfSchedulesRepository.saveAll(request.getSessionIds().stream().map(id ->
            SessionsOfSchedules.builder().schedule(savedSchedule).session(sessionRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_IDS_COLLECTION))).build()
        ).toList());
    }
}
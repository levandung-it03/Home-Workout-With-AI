package com.restproject.backend.services.Admin;

import com.restproject.backend.dtos.request.NewScheduleRequest;
import com.restproject.backend.dtos.request.NewSessionRequest;
import com.restproject.backend.dtos.request.SessionsByLevelRequest;
import com.restproject.backend.entities.ExercisesOfSessions;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.entities.Session;
import com.restproject.backend.entities.SessionsOfSchedules;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Level;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.ExerciseRepository;
import com.restproject.backend.repositories.ExercisesOfSessionsRepository;
import com.restproject.backend.repositories.SessionRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionService {
    SessionRepository sessionRepository;
    ExerciseRepository exerciseRepository;
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;

    @Transactional(rollbackOn = {Exception.class})
    public void createSession(NewSessionRequest request) throws ApplicationException {
        var savedSession = sessionRepository.save(Session.builder()
            .name(request.getName())
            .muscleList(request.getMuscleList())
            .description(request.getDescription())
            .level(Level.getByLevel(request.getLevel()))
            .build());

        exercisesOfSessionsRepository.saveAll(request.getExerciseIds().stream().map(id ->
            ExercisesOfSessions.builder().session(savedSession).exercise(exerciseRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_IDS_COLLECTION))).build()
        ).toList());
    }

    public List<Session> getSessionsByLevel(SessionsByLevelRequest request) throws ApplicationException {
        return sessionRepository.findAllByLevel(Level.getByLevel(request.getLevel()));
    }
}

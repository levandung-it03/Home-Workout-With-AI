package com.restproject.backend.services.Admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restproject.backend.dtos.request.*;
import com.restproject.backend.entities.*;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.FilteringPageMappers;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.mappers.SessionMappers;
import com.restproject.backend.repositories.ExerciseRepository;
import com.restproject.backend.repositories.ExercisesOfSessionsRepository;
import com.restproject.backend.repositories.MusclesOfSessionsRepository;
import com.restproject.backend.repositories.SessionRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionService {
    SessionMappers sessionMappers;
    PageMappers pageMappers;
    ObjectMapper objectMapper;
    FilteringPageMappers filteringPageMappers;
    SessionRepository sessionRepository;
    ExerciseRepository exerciseRepository;
    MusclesOfSessionsRepository musclesOfSessionsRepository;
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;

    public List<Session> getSessionsByLevel(SessionsByLevelRequest request) throws ApplicationException {
        return sessionRepository.findAllByLevel(Level.getByLevel(request.getLevel()));
    }

    public List<Session> getPaginatedSessions(PaginatedObjectRequest request) {
        Pageable pageableConfig = pageMappers.pageRequestToPageable(request).toPageable();
        Page<Session> repoResponse = sessionRepository.findAll(pageableConfig);
        return repoResponse.stream().toList();
    }

    @Transactional
    public List<Exercise> getPaginatedExercisesOfSession(PaginatedExercisesOfSessionRequest request) {
        Pageable pageableConfig = pageMappers.pageRequestToPageable(
            PaginatedObjectRequest.builder().page(request.getPage()).build()).toPageable();
        Page<ExercisesOfSessions> repoResponse = exercisesOfSessionsRepository
            .findAllBySessionSessionId(request.getSessionId(), pageableConfig);
        return repoResponse.stream().map(ExercisesOfSessions::getExercise).toList();
    }

    @Transactional(rollbackOn = {Exception.class})
    public Session createSession(NewSessionRequest request) throws ApplicationException {
        var savedSession = sessionRepository.save(sessionMappers.insertionToPlain(request));
        musclesOfSessionsRepository.saveAll(request.getMuscleIds().stream().map(id ->
            MusclesOfSessions.builder().muscle(Muscle.getById(id)).session(savedSession).build()).toList());
        exercisesOfSessionsRepository.saveAll(request.getExerciseIds().stream().map(id -> {
            var foundExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
            if (foundExercise.getLevel() != savedSession.getLevel())
                throw new ApplicationException(ErrorCodes.NOT_SYNC_LEVEL);

            return ExercisesOfSessions.builder().session(savedSession).exercise(foundExercise).build();
        }).toList());
        return savedSession;
    }

    public List<Session> getPaginatedFilteringListOfSessions(FilteringPageRequest request) {
        Pageable pageableConfig = filteringPageMappers.pageRequestToPageable(request).toPageable();
        Session sessionInfo;
        try {
            sessionInfo = objectMapper.convertValue(request.getFilterFields(), Session.class);
        } catch (Exception e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }
        Page<Session> repoResponse = sessionRepository.findAllByFilteringSession(sessionInfo, pageableConfig);
        return repoResponse.stream().toList();
    }
}

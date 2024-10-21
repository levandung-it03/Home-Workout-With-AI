package com.restproject.backend.services;

import com.restproject.backend.dtos.general.ExerciseInfoDto;
import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.*;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.mappers.SessionMappers;
import com.restproject.backend.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionService {
    PageMappers pageMappers;
    SessionMappers sessionMappers;
    SessionRepository sessionRepository;
    ExerciseRepository exerciseRepository;
    MuscleRepository muscleRepository;
    MuscleSessionRepository muscleSessionRepository;
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;
    SessionsOfSchedulesRepository sessionsOfSchedulesRepository;

    public TablePagesResponse<Session> getSessionsHasMusclesPages(PaginatedTableRequest request) {
        Pageable pageableCfg = pageMappers.tablePageRequestToPageable(request).toPageable(Session.class);

        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<Session> repoRes = sessionRepository.findAll(pageableCfg);
            return TablePagesResponse.<Session>builder().data(repoRes.stream().toList())
                .currentPage(request.getPage()).totalPages(repoRes.getTotalPages()).build();
        }

        //--Build filtering info.
        SessionPagesRequest sessionPagesRequest;
        try {
            sessionPagesRequest = SessionPagesRequest.buildFromHashMap(request.getFilterFields());
        } catch (ApplicationException | IllegalArgumentException | NullPointerException | NoSuchFieldException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }

        Page<Session> repoRes = sessionRepository.findAllSessionsCustom(sessionPagesRequest, pageableCfg);
        return TablePagesResponse.<Session>builder().data(repoRes.stream().toList())
            .currentPage(request.getPage()).totalPages(repoRes.getTotalPages()).build();
    }

    //--Missing Test
    @Transactional(rollbackOn = {RuntimeException.class})
    public Session createSession(NewSessionRequest request) throws ApplicationException {
        Set<Integer> uniqueOrdinals = request.getExercisesInfo().stream().map(ExerciseInfoDto::getOrdinal)
            .collect(Collectors.toSet());
        if (request.getExercisesInfo().size() != uniqueOrdinals.size())
            throw new ApplicationException(ErrorCodes.NOT_UNIQUE_ORDINALS);

        Session savedSession;
        try { savedSession = sessionRepository.save(sessionMappers.insertionToPlain(request)); }
        catch (DataIntegrityViolationException e) { throw new ApplicationException(ErrorCodes.DUPLICATED_SESSION); }
        muscleSessionRepository.saveAll(
            muscleRepository.findAllById(request.getMuscleIds())
                .stream().map(muscle -> MuscleSession.builder().session(savedSession).muscle(muscle).build())
                .toList());
        exercisesOfSessionsRepository.saveAll(request.getExercisesInfo().stream().map(exerciseInfo -> {
            var foundExercise = exerciseRepository.findById(exerciseInfo.getExerciseId())
                .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
            if (foundExercise.getLevelEnum() != savedSession.getLevelEnum())
                throw new ApplicationException(ErrorCodes.NOT_SYNC_LEVEL);

            return ExercisesOfSessions.builder()
                .session(savedSession)
                .exercise(foundExercise)
                .ordinal(exerciseInfo.getOrdinal())
                .iteration(exerciseInfo.getIteration())
                .slackInSecond(exerciseInfo.getSlackInSecond())
                .raiseSlackInSecond(exerciseInfo.getRaiseSlackInSecond())
                .downRepsRatio(exerciseInfo.getDownRepsRatio())
                .build();
        }).toList());
        return savedSession;
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public Session updateSessionAndMuscles(UpdateSessionRequest request) throws ApplicationException {
        var formerSes = sessionRepository.findById(request.getSessionId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        //--Check if this Session can be updated or not.
        if (sessionsOfSchedulesRepository.existsBySessionSessionId(formerSes.getSessionId()))
            throw new ApplicationException(ErrorCodes.FORBIDDEN_UPDATING);
        //--Query all related and updated data is existing in DB.
        var formerRls = muscleSessionRepository.findAllBySessionSessionId(formerSes.getSessionId());
        if (formerRls.isEmpty())    //--If data in DB is wrong.
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);

        //--Mapping new values into "formerSes".
        sessionMappers.updateTarget(formerSes, request);
        //--Start to save updated data.
        sessionRepository.updateSessionBySession(formerSes);

        //--Check if there's changes with Muscles of Updated Session.
        if (formerRls.stream().map(r -> r.getMuscle().getMuscleId()).sorted().toList()
            .equals(request.getMuscleIds().stream().sorted().toList())) {
            return formerSes;   //--Nothing updated equal to return immediately.
        }

        //--Delete the former muscles-session relationship.
        muscleSessionRepository.deleteAllBySessionSessionId(formerSes.getSessionId());
        var newMusclesOfEx = muscleRepository.findAllById(request.getMuscleIds()).stream().map(muscle ->
            MuscleSession.builder().session(formerSes).muscle(muscle).build()
        ).toList();
        //--Save all new relationship.
        muscleSessionRepository.saveAll(newMusclesOfEx);
        return formerSes;
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public void deleteSession(DeleteObjectRequest request) throws ApplicationException {
        if (!sessionRepository.existsById(request.getId()))
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);

        if (sessionsOfSchedulesRepository.existsBySessionSessionId(request.getId()))
            throw new ApplicationException(ErrorCodes.FORBIDDEN_UPDATING);

        muscleSessionRepository.deleteAllBySessionSessionId(request.getId());
        exercisesOfSessionsRepository.deleteAllBySessionSessionId(request.getId());
        sessionRepository.deleteById(request.getId());
    }
}

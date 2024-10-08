package com.restproject.backend.services;

import com.restproject.backend.dtos.request.*;
import com.restproject.backend.entities.*;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.SessionMappers;
import com.restproject.backend.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionService {
    SessionMappers sessionMappers;
    SessionRepository sessionRepository;
    ExerciseRepository exerciseRepository;
    MusclesOfSessionsRepository musclesOfSessionsRepository;
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;
    SessionsOfSchedulesRepository sessionsOfSchedulesRepository;

    //--Missing Test
    @Transactional(rollbackOn = {RuntimeException.class})
    public Session createSession(NewSessionRequest request) throws ApplicationException {
        Session savedSession;
        try { savedSession = sessionRepository.save(sessionMappers.insertionToPlain(request)); }
        catch (DataIntegrityViolationException e) { throw new ApplicationException(ErrorCodes.DUPLICATED_SESSION); }
        musclesOfSessionsRepository.saveAll(request.getMuscleIds().stream().map(id ->
            MusclesOfSessions.builder().muscle(Muscle.getById(id)).session(savedSession).build()).toList());
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
        var formerRls = musclesOfSessionsRepository.findAllBySessionSessionId(formerSes.getSessionId());
        if (formerRls.isEmpty())    //--If data in DB is wrong.
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);

        //--Mapping new values into "formerSes".
        sessionMappers.updateTarget(formerSes, request);
        //--Start to save updated data.
        sessionRepository.updateSessionBySession(formerSes);

        //--Check if there's changes with Muscles of Updated Session.
        if (formerRls.stream().map(relationship -> relationship.getMuscle().getId()).sorted().toList()
            .equals(request.getMuscleIds().stream().sorted().toList())) {
            return formerSes;   //--Nothing updated equal to return immediately.
        }

        //--Delete the former muscles-session relationship.
        musclesOfSessionsRepository.deleteAllBySessionSessionId(formerSes.getSessionId());
        var newMusclesOfEx = request.getMuscleIds().stream().map(id ->
            MusclesOfSessions.builder().session(formerSes).muscle(Muscle.getById(id)).build()
        ).toList();
        //--Save all new relationship.
        musclesOfSessionsRepository.saveAll(newMusclesOfEx);
        return formerSes;
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public void deleteSession(DeleteObjectRequest request) throws ApplicationException {
        if (!sessionRepository.existsById(request.getId()))
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);

        if (sessionsOfSchedulesRepository.existsBySessionSessionId(request.getId()))
            throw new ApplicationException(ErrorCodes.FORBIDDEN_UPDATING);

        musclesOfSessionsRepository.deleteAllBySessionSessionId(request.getId());
        exercisesOfSessionsRepository.deleteAllBySessionSessionId(request.getId());
        sessionRepository.deleteById(request.getId());
    }
}

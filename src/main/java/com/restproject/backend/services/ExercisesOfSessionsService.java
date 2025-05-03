package com.restproject.backend.services;

import com.restproject.backend.dtos.general.ByIdDto;
import com.restproject.backend.dtos.general.ExerciseInfoDto;
import com.restproject.backend.dtos.request.UpdateExercisesOfSessionRequest;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.ExercisesOfSessions;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.ExerciseRepository;
import com.restproject.backend.repositories.ExercisesOfSessionsRepository;
import com.restproject.backend.repositories.SessionRepository;
import com.restproject.backend.repositories.SessionsOfSchedulesRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExercisesOfSessionsService {
    SessionRepository sessionRepository;
    ExerciseRepository exerciseRepository;
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;
    SessionsOfSchedulesRepository sessionsOfSchedulesRepository;

    //--Missing Test
    @Transactional(rollbackOn = {RuntimeException.class})
    public List<Exercise> updateExercisesOfSession(UpdateExercisesOfSessionRequest request) throws ApplicationException {
        Set<Integer> uniqueOrdinals = request.getExercisesInfo().stream().map(ExerciseInfoDto::getOrdinal)
            .collect(Collectors.toSet());
        if (request.getExercisesInfo().size() != uniqueOrdinals.size())
            throw new ApplicationException(ErrorCodes.NOT_UNIQUE_ORDINALS);
        var updatedSession = sessionRepository.findById(request.getSessionId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        var exercisesFromDB = exerciseRepository
            .findAllByIdIn(request.getExercisesInfo().stream().map(ExerciseInfoDto::getExerciseId).toList());
        if (new HashSet<>(exercisesFromDB.stream().map(Exercise::getExerciseId).toList()).size()
            != new HashSet<>(request.getExercisesInfo().stream().map(ExerciseInfoDto::getExerciseId).toList()).size())
            throw new ApplicationException(ErrorCodes.INVALID_IDS_COLLECTION);
        if (sessionsOfSchedulesRepository.existsBySessionSessionId(request.getSessionId()))
            throw new ApplicationException(ErrorCodes.FORBIDDEN_UPDATING);

        var exerciseInfo = request.getExercisesInfo()
            .stream().sorted(Comparator.comparing(ExerciseInfoDto::getExerciseId))
            .toList();
        ArrayList<ExercisesOfSessions> savedRelationships = new ArrayList<>();
        for (var index = 0; index < exercisesFromDB.size(); index++) {
            if (!exercisesFromDB.get(index).getLevelEnum().equals(updatedSession.getLevelEnum()))
                throw new ApplicationException(ErrorCodes.NOT_SYNC_LEVEL);
            savedRelationships.add(ExercisesOfSessions.builder()
                .session(updatedSession)
                .exercise(exercisesFromDB.get(index))
                .ordinal(exerciseInfo.get(index).getOrdinal())
                .iteration(exerciseInfo.get(index).getIteration())
                .slackInSecond(exerciseInfo.get(index).getSlackInSecond())
                .raiseSlackInSecond(exerciseInfo.get(index).getRaiseSlackInSecond())
                .downRepsRatio(exerciseInfo.get(index).getDownRepsRatio())
                .build());
        }

        exercisesOfSessionsRepository.deleteAllBySessionSessionId(updatedSession.getSessionId());
        exercisesOfSessionsRepository.flush();
        var repoResponse = exercisesOfSessionsRepository.saveAll(savedRelationships);

        return repoResponse.stream().map(ExercisesOfSessions::getExercise).toList();
    }

    public List<ExercisesOfSessions> getExercisesOfSessionRelationship(ByIdDto request) {
        return exercisesOfSessionsRepository.findAllById(request.getId());
    }
}

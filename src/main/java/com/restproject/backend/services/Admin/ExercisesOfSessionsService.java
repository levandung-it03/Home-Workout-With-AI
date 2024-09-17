package com.restproject.backend.services.Admin;

import com.restproject.backend.dtos.request.UpdateExercisesOfSessionRequest;
import com.restproject.backend.dtos.request.PaginatedRelationshipRequest;
import com.restproject.backend.dtos.response.ExercisesOfSessionResponse;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.ExercisesOfSessions;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.repositories.ExerciseRepository;
import com.restproject.backend.repositories.ExercisesOfSessionsRepository;
import com.restproject.backend.repositories.SessionRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExercisesOfSessionsService {
    PageMappers pageMappers;
    SessionRepository sessionRepository;
    ExerciseRepository exerciseRepository;
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;

    public List<ExercisesOfSessionResponse> getExercisesHasMusclesOfSessionPagesPrioritizeRelationship(
        PaginatedRelationshipRequest request) {
        Pageable pageableConfig = pageMappers.relationshipPageRequestToPageable(request).toPageable();
        Page<ExercisesOfSessionResponse> repoResponse = exercisesOfSessionsRepository
            .findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(request.getId(), pageableConfig);
        return repoResponse.stream().toList();
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public List<Exercise> updateExercisesOfSession(UpdateExercisesOfSessionRequest request)
        throws ApplicationException {
        var updatedSession = sessionRepository.findById(request.getSessionId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        var exercisesFromDB = exerciseRepository.findAllById(request.getExerciseIds());
        if (exercisesFromDB.size() != request.getExerciseIds().size())
            throw new ApplicationException(ErrorCodes.INVALID_IDS_COLLECTION);

        exercisesOfSessionsRepository.deleteAllBySessionSessionId(updatedSession.getSessionId());
        var repoResponse = exercisesOfSessionsRepository.saveAll(exercisesFromDB.stream().map(exercise ->
            ExercisesOfSessions.builder().exercise(exercise).session(updatedSession).build()
        ).toList());
        return repoResponse.stream().map(ExercisesOfSessions::getExercise).toList();
    }
}

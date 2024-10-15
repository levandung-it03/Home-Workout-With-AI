package com.restproject.backend.services;

import com.restproject.backend.dtos.general.ExerciseInfoDto;
import com.restproject.backend.dtos.request.UpdateExercisesOfSessionRequest;
import com.restproject.backend.dtos.request.PaginatedRelationshipRequest;
import com.restproject.backend.dtos.response.ExercisesOfSessionResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.ExercisesOfSessions;
import com.restproject.backend.entities.MusclesOfExercises;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExercisesOfSessionsService {
    PageMappers pageMappers;
    SessionRepository sessionRepository;
    ExerciseRepository exerciseRepository;
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;

    public TablePagesResponse<ExercisesOfSessionResponse> getExercisesHasMusclesOfSessionPagesPrioritizeRelationship(
        PaginatedRelationshipRequest request) {
        Pageable pageableCfg = pageMappers.relationshipPageRequestToPageable(request)
            .toPageable(ExercisesOfSessions.class);

        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<Object[]> repoRes = exercisesOfSessionsRepository
                .findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(request.getId(), pageableCfg);
            return TablePagesResponse.<ExercisesOfSessionResponse>builder()
                .data(repoRes.stream().map(ExercisesOfSessionResponse::buildFromNativeQuery).toList())
                .currentPage(request.getPage()).totalPages(repoRes.getTotalPages()).build();
        }

        //--Build filtering info.
        ExercisesOfSessionResponse exerciseInfo;
        try {
            exerciseInfo = ExercisesOfSessionResponse.buildFromHashMap(request.getFilterFields());
        } catch (ApplicationException | IllegalArgumentException | NullPointerException | NoSuchFieldException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }

        Page<Object[]> repoRes = exercisesOfSessionsRepository
            .findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(request.getId(), exerciseInfo, pageableCfg);
        return TablePagesResponse.<ExercisesOfSessionResponse>builder()
            .data(repoRes.stream().map(ExercisesOfSessionResponse::buildFromNativeQuery).toList())
            .currentPage(request.getPage()).totalPages(repoRes.getTotalPages()).build();
    }

    //--Missing Test
    @Transactional(rollbackOn = {RuntimeException.class})
    public List<Exercise> updateExercisesOfSession(UpdateExercisesOfSessionRequest request)
        throws ApplicationException {
        var updatedSession = sessionRepository.findById(request.getSessionId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        var exercisesFromDB = exerciseRepository
            .findAllByIdIn(request.getExercisesInfo().stream().map(ExerciseInfoDto::getExerciseId).toList());
        if (exercisesFromDB.size() != request.getExercisesInfo().size())
            throw new ApplicationException(ErrorCodes.INVALID_IDS_COLLECTION);
        exercisesOfSessionsRepository.deleteAllBySessionSessionId(updatedSession.getSessionId());

        var exerciseInfo = request.getExercisesInfo()
            .stream().sorted(Comparator.comparing(ExerciseInfoDto::getExerciseId))
            .toList();
        ArrayList<ExercisesOfSessions> savedRelationships = new ArrayList<>();
        for (var index = 0; index < exercisesFromDB.size(); index++)
            savedRelationships.add(ExercisesOfSessions.builder()
                .session(updatedSession)
                .exercise(exercisesFromDB.get(index))
                .ordinal(exerciseInfo.get(index).getOrdinal())
                .iteration(exerciseInfo.get(index).getIteration())
                .slackInSecond(exerciseInfo.get(index).getSlackInSecond())
                .raiseSlackInSecond(exerciseInfo.get(index).getRaiseSlackInSecond())
                .downRepsRatio(exerciseInfo.get(index).getDownRepsRatio())
                .build());
        var repoResponse = exercisesOfSessionsRepository.saveAll(savedRelationships);
        return repoResponse.stream().map(ExercisesOfSessions::getExercise).toList();
    }
}

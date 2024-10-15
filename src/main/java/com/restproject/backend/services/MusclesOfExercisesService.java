package com.restproject.backend.services;

import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.dtos.response.ExerciseHasMusclesResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.MusclesOfExercises;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.repositories.MusclesOfExercisesRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MusclesOfExercisesService {
    MusclesOfExercisesRepository musclesOfExercisesRepository;
    PageMappers pageMappers;

    public TablePagesResponse<ExerciseHasMusclesResponse> getExercisesHasMusclesPages(PaginatedTableRequest request) {
        Pageable pageableCfg = pageMappers.tablePageRequestToPageable(request).toPageable(MusclesOfExercises.class);

        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<Object[]> repoRes = musclesOfExercisesRepository.findAllExercisesHasMuscles(pageableCfg);
            return TablePagesResponse.<ExerciseHasMusclesResponse>builder()
                .data(repoRes.stream().map(ExerciseHasMusclesResponse::buildFromNativeQuery).toList())
                .currentPage(request.getPage()).totalPages(repoRes.getTotalPages()).build();
        }

        //--Build filtering info.
        ExerciseHasMusclesResponse exerciseInfo;
        try {
            exerciseInfo = ExerciseHasMusclesResponse.buildFromHashMap(request.getFilterFields());
        } catch (ApplicationException | IllegalArgumentException | NullPointerException | NoSuchFieldException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }

        Page<Object[]> repoRes = musclesOfExercisesRepository.findAllExercisesHasMuscles(exerciseInfo, pageableCfg);
        return TablePagesResponse.<ExerciseHasMusclesResponse>builder()
            .data(repoRes.stream().map(ExerciseHasMusclesResponse::buildFromNativeQuery).toList())
            .currentPage(request.getPage()).totalPages(repoRes.getTotalPages()).build();
    }
}

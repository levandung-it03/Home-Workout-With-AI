package com.restproject.backend.services;

import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.dtos.response.SessionHasMusclesResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.MusclesOfSessions;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.repositories.MusclesOfSessionsRepository;
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
public class MusclesOfSessionsService {
    MusclesOfSessionsRepository musclesOfSessionsRepository;
    PageMappers pageMappers;

    public TablePagesResponse<SessionHasMusclesResponse> getSessionsHasMusclesPages(PaginatedTableRequest request) {
        if (!Objects.isNull(request.getSortedField())   //--If it's null, it means client doesn't want to sort.
        &&  !MusclesOfSessions.INSTANCE_FIELDS.contains(request.getSortedField()))
            throw new ApplicationException(ErrorCodes.INVALID_SORTING_FIELD_OR_VALUE);
        Pageable pageableCfg = pageMappers.tablePageRequestToPageable(request).toPageable();

        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<Object[]> repoRes = musclesOfSessionsRepository.findAllSessionsHasMuscles(pageableCfg);
            return TablePagesResponse.<SessionHasMusclesResponse>builder()
                .data(repoRes.stream().map(SessionHasMusclesResponse::buildFromNativeQuery).toList())
                .currentPage(request.getPage())
                .totalPages(repoRes.getTotalPages()).build();
        }

        //--Build filtering info.
        SessionHasMusclesResponse sessionInfo;
        try {
            sessionInfo = SessionHasMusclesResponse.buildFromHashMap(request.getFilterFields());
        } catch (ApplicationException | IllegalArgumentException | NullPointerException | NoSuchFieldException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }

        Page<Object[]> repoRes = musclesOfSessionsRepository.findAllSessionsHasMuscles(sessionInfo, pageableCfg);
        return TablePagesResponse.<SessionHasMusclesResponse>builder()
            .data(repoRes.stream().map(SessionHasMusclesResponse::buildFromNativeQuery).toList())
            .currentPage(request.getPage())
            .totalPages(repoRes.getTotalPages())
            .build();
    }
}

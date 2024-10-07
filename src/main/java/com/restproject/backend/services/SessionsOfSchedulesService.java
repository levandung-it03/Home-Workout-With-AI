package com.restproject.backend.services;

import com.restproject.backend.dtos.request.PaginatedRelationshipRequest;
import com.restproject.backend.dtos.request.UpdateSessionsOfScheduleRequest;
import com.restproject.backend.dtos.response.SessionsOfScheduleResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.MusclesOfSessions;
import com.restproject.backend.entities.Session;
import com.restproject.backend.entities.SessionsOfSchedules;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.repositories.ScheduleRepository;
import com.restproject.backend.repositories.SessionRepository;
import com.restproject.backend.repositories.SessionsOfSchedulesRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionsOfSchedulesService {
    PageMappers pageMappers;
    SessionsOfSchedulesRepository sessionsOfSchedulesRepository;
    ScheduleRepository scheduleRepository;
    SessionRepository sessionRepository;

    public TablePagesResponse<SessionsOfScheduleResponse> getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship(
        PaginatedRelationshipRequest request) {
        if (!Objects.isNull(request.getSortedField())
        && !MusclesOfSessions.INSTANCE_FIELDS.contains(request.getSortedField()))
            throw new ApplicationException(ErrorCodes.INVALID_SORTING_FIELD_OR_VALUE);
        Pageable pageableCf = pageMappers.relationshipPageRequestToPageable(request).toPageable();

        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<Object[]> repoRes = sessionsOfSchedulesRepository
                .findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(request.getId(), pageableCf);
            return TablePagesResponse.<SessionsOfScheduleResponse>builder()
                .data(repoRes.stream().map(SessionsOfScheduleResponse::buildFromNativeQuery).toList())
                .currentPage(request.getPage()).totalPages(repoRes.getTotalPages()).build();
        }

        SessionsOfScheduleResponse sessionInfo;
        try {
            sessionInfo = SessionsOfScheduleResponse.buildFromHashMap(request.getFilterFields());
        } catch (ApplicationException | NullPointerException | IllegalArgumentException | NoSuchFieldException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }
        Page<Object[]> repoRes = sessionsOfSchedulesRepository
            .findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(request.getId(), sessionInfo, pageableCf);

        return TablePagesResponse.<SessionsOfScheduleResponse>builder()
            .data(repoRes.stream().map(SessionsOfScheduleResponse::buildFromNativeQuery).toList())
            .currentPage(request.getPage()).totalPages(repoRes.getTotalPages()).build();
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public List<Session> updateSessionsOfSchedule(UpdateSessionsOfScheduleRequest request) {
        var updatedSchedule = scheduleRepository.findById(request.getScheduleId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        var sessionsFromDB = sessionRepository.findAllById(request.getSessionIds());
        if (sessionsFromDB.size() != request.getSessionIds().size())
            throw new ApplicationException(ErrorCodes.INVALID_IDS_COLLECTION);

        sessionsOfSchedulesRepository.deleteAllByScheduleScheduleId(updatedSchedule.getScheduleId());
        var repoResponse = sessionsOfSchedulesRepository.saveAll(sessionsFromDB.stream().map(session ->
            SessionsOfSchedules.builder().session(session).schedule(updatedSchedule).build()
        ).toList());
        return repoResponse.stream().map(SessionsOfSchedules::getSession).toList();
    }
}

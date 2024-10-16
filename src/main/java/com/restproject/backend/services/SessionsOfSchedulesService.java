package com.restproject.backend.services;

import com.restproject.backend.dtos.general.SessionInfoDto;
import com.restproject.backend.dtos.request.PaginatedRelationshipRequest;
import com.restproject.backend.dtos.request.SessionsOfScheduleRequest;
import com.restproject.backend.dtos.request.UpdateSessionsOfScheduleRequest;
import com.restproject.backend.dtos.response.SessionsOfScheduleResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
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

import java.util.ArrayList;
import java.util.Comparator;
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
        Pageable pageableCf = pageMappers.relationshipPageRequestToPageable(request)
            .toPageable(SessionsOfSchedules.class);

        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<Object[]> repoRes = sessionsOfSchedulesRepository
                .findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(request.getId(), pageableCf);
            return TablePagesResponse.<SessionsOfScheduleResponse>builder()
                .data(repoRes.stream().map(SessionsOfScheduleResponse::buildFromQuery).toList())
                .currentPage(request.getPage()).totalPages(repoRes.getTotalPages()).build();
        }

        SessionsOfScheduleRequest sessionInfo;
        try {
            sessionInfo = SessionsOfScheduleRequest.buildFromHashMap(request.getFilterFields());
        } catch (ApplicationException | NullPointerException | IllegalArgumentException | NoSuchFieldException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }
        Page<Object[]> repoRes = sessionsOfSchedulesRepository
            .findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(request.getId(), sessionInfo, pageableCf);

        return TablePagesResponse.<SessionsOfScheduleResponse>builder()
            .data(repoRes.stream().map(SessionsOfScheduleResponse::buildFromQuery).toList())
            .currentPage(request.getPage()).totalPages(repoRes.getTotalPages()).build();
    }

    //--Missing Test
    @Transactional(rollbackOn = {RuntimeException.class})
    public List<Session> updateSessionsOfSchedule(UpdateSessionsOfScheduleRequest request) {
        var updatedSchedule = scheduleRepository.findById(request.getScheduleId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        var sessionsFromDB = sessionRepository.findAllByIdIn(request.getSessionsInfo()
            .stream().map(SessionInfoDto::getSessionId).toList());
        if (sessionsFromDB.size() != request.getSessionsInfo().size())
            throw new ApplicationException(ErrorCodes.INVALID_IDS_COLLECTION);

        sessionsOfSchedulesRepository.deleteAllByScheduleScheduleId(updatedSchedule.getScheduleId());
        List<SessionInfoDto> sessionsInfo = request.getSessionsInfo()
            .stream().sorted(Comparator.comparing(SessionInfoDto::getSessionId)).toList();
        ArrayList<SessionsOfSchedules> savedRelationships = new ArrayList<>();
        for (var index = 0; index < request.getSessionsInfo().size(); index++) {
            savedRelationships.add(SessionsOfSchedules.builder()
                .schedule(updatedSchedule)
                .session(sessionsFromDB.get(index))
                .ordinal(sessionsInfo.get(index).getOrdinal())
                .build());
        }
        sessionsOfSchedulesRepository.saveAll(savedRelationships);
        return sessionsFromDB;
    }
}

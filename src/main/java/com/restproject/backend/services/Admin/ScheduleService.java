package com.restproject.backend.services.Admin;

import com.restproject.backend.dtos.request.NewScheduleRequest;
import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.entities.SessionsOfSchedules;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Level;
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

import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduleService {
    PageMappers pageMappers;
    ScheduleRepository scheduleRepository;
    SessionRepository sessionRepository;
    SessionsOfSchedulesRepository sessionsOfSchedulesRepository;

    public TablePagesResponse<Schedule> getSchedulesPages(PaginatedTableRequest request) {
        if (!Objects.isNull(request.getSortedField())   //--If sortedField is null, it means client doesn't want to sort
            &&  !Schedule.INSTANCE_FIELDS.contains(request.getSortedField()))
            throw new ApplicationException(ErrorCodes.INVALID_SORTING_FIELD_OR_VALUE);
        Pageable pageableCf = pageMappers.tablePageRequestToPageable(request).toPageable();

        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<Schedule> repoRes = scheduleRepository.findAll(pageableCf);
            return TablePagesResponse.<Schedule>builder().data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages()).currentPage(request.getPage()).build();
        }

        Schedule scheduleInfo;
        try {
            scheduleInfo = Schedule.buildFromHashMap(request.getFilterFields());
        } catch (ApplicationException | NullPointerException | IllegalArgumentException | NoSuchFieldException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }
        Page<Schedule> repoRes = scheduleRepository.findAllBySchedule(scheduleInfo, pageableCf);
        return TablePagesResponse.<Schedule>builder().data(repoRes.stream().toList())
            .totalPages(repoRes.getTotalPages()).currentPage(request.getPage()).build();
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public Schedule createSchedule(NewScheduleRequest request) throws ApplicationException {
        var savedSchedule = scheduleRepository.save(Schedule.builder().name(request.getName())
            .description(request.getDescription()).level(Level.getByLevel(request.getLevel())).build());

        sessionsOfSchedulesRepository.saveAll(request.getSessionIds().stream().map(id ->
            SessionsOfSchedules.builder().schedule(savedSchedule).session(sessionRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_IDS_COLLECTION))).build()).toList());

        return savedSchedule;
    }
}
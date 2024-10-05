package com.restproject.backend.services;

import com.restproject.backend.dtos.request.DeleteObjectRequest;
import com.restproject.backend.dtos.request.NewScheduleRequest;
import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.dtos.request.UpdateScheduleRequest;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.entities.SessionsOfSchedules;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.mappers.ScheduleMappers;
import com.restproject.backend.repositories.ScheduleRepository;
import com.restproject.backend.repositories.SessionRepository;
import com.restproject.backend.repositories.SessionsOfSchedulesRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
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
    ScheduleMappers scheduleMappers;

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
        Schedule savedSchedule;
        try { savedSchedule = scheduleRepository.save(scheduleMappers.insertionToPlain(request)); }
        catch (DataIntegrityViolationException e) { throw new ApplicationException(ErrorCodes.DUPLICATED_SCHEDULE); }

        sessionsOfSchedulesRepository.saveAll(request.getSessionIds().stream().map(id -> {
            var foundSession = sessionRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_IDS_COLLECTION));
            if (!foundSession.getLevel().equals(savedSchedule.getLevel()))
                throw new ApplicationException(ErrorCodes.NOT_SYNC_LEVEL);

            return SessionsOfSchedules.builder().schedule(savedSchedule).session(foundSession).build();
        }).toList());

        return savedSchedule;
    }


    @Transactional(rollbackOn = {RuntimeException.class})
    public Schedule updateSchedule(UpdateScheduleRequest request) throws ApplicationException {
        var formerSch = scheduleRepository.findById(request.getScheduleId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        //--Check if this Schedule can be updated or not.
        if (sessionsOfSchedulesRepository.existsByScheduleScheduleId(formerSch.getScheduleId()))
            throw new ApplicationException(ErrorCodes.FORBIDDEN_UPDATING);

        //--Mapping new values into "formerSch".
        scheduleMappers.updateTarget(formerSch, request);
        //--Start to save updated data.
        scheduleRepository.updateScheduleBySchedule(formerSch);
        return formerSch;
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public void deleteSchedule(DeleteObjectRequest request) throws ApplicationException {
        if (!scheduleRepository.existsById(request.getId()))
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);

        if (sessionsOfSchedulesRepository.existsByScheduleScheduleId(request.getId()))
            throw new ApplicationException(ErrorCodes.FORBIDDEN_UPDATING);

        sessionsOfSchedulesRepository.deleteAllByScheduleScheduleId(request.getId());
        scheduleRepository.deleteById(request.getId());
    }
}
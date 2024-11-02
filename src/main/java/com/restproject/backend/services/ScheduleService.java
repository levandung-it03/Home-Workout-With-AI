package com.restproject.backend.services;

import com.restproject.backend.dtos.general.ByIdDto;
import com.restproject.backend.dtos.general.SessionInfoDto;
import com.restproject.backend.dtos.request.*;
import com.restproject.backend.dtos.response.PreviewFullScheduleResponse;
import com.restproject.backend.dtos.response.PreviewScheduleResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.entities.SessionsOfSchedules;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.mappers.ScheduleMappers;
import com.restproject.backend.repositories.*;
import com.restproject.backend.services.Auth.JwtService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduleService {
    PageMappers pageMappers;
    ScheduleRepository scheduleRepository;
    SessionRepository sessionRepository;
    SessionsOfSchedulesRepository sessionsOfSchedulesRepository;
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;
    ScheduleMappers scheduleMappers;
    JwtService jwtService;
    SubscriptionRepository subscriptionRepository;

    public TablePagesResponse<Schedule> getSchedulesPages(PaginatedTableRequest request) {
        Pageable pageableCf = pageMappers.tablePageRequestToPageable(request).toPageable(Schedule.class);

        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<Schedule> repoRes = scheduleRepository.findAll(pageableCf);
            return TablePagesResponse.<Schedule>builder().data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages()).currentPage(request.getPage()).build();
        }

        ScheduleRequest scheduleInfo;
        try {
            scheduleInfo = ScheduleRequest.buildFromHashMap(request.getFilterFields());
        } catch (ApplicationException | NullPointerException | IllegalArgumentException | NoSuchFieldException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }
        Page<Schedule> repoRes = scheduleRepository.findAllBySchedule(scheduleInfo, pageableCf);
        return TablePagesResponse.<Schedule>builder().data(repoRes.stream().toList())
            .totalPages(repoRes.getTotalPages()).currentPage(request.getPage()).build();
    }

    public PreviewFullScheduleResponse getPreviewSchedule(ByIdDto request) {
        var schedule = scheduleRepository.findById(request.getId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        var sessionsOfSchedules = sessionsOfSchedulesRepository.findAllById(request.getId());
        var previewSessions = sessionsOfSchedules.stream().map(sessionOfSchedule ->
            PreviewFullScheduleResponse.PreviewSession.builder()
                .ordinal(sessionOfSchedule.getOrdinal())
                .session(sessionOfSchedule.getSession())
                .exercisesOfSessions(
                    exercisesOfSessionsRepository.findAllById(sessionOfSchedule.getSession().getSessionId()))
                .build()
        ).toList();
        return PreviewFullScheduleResponse.builder()
            .schedule(schedule)
            .totalSessions(schedule.getSessionsOfSchedule().size())
            .sessionsOfSchedules(previewSessions).build();
    }

    //--Missing Test
    @Transactional(rollbackOn = {RuntimeException.class})
    public Schedule createSchedule(NewScheduleRequest request) throws ApplicationException {
        Set<Integer> uniqueOrdinals = request.getSessionsInfo().stream().map(SessionInfoDto::getOrdinal)
            .collect(Collectors.toSet());
        if (request.getSessionsInfo().size() != uniqueOrdinals.size())
            throw new ApplicationException(ErrorCodes.NOT_UNIQUE_ORDINALS);

        Schedule savedSchedule;
        try { savedSchedule = scheduleRepository.save(scheduleMappers.insertionToPlain(request)); }
        catch (DataIntegrityViolationException e) { throw new ApplicationException(ErrorCodes.DUPLICATED_SCHEDULE); }

        sessionsOfSchedulesRepository.saveAll(request.getSessionsInfo().stream().map(sessionInfo -> {
            var foundSession = sessionRepository.findById(sessionInfo.getSessionId())
                .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_IDS_COLLECTION));
            if (!foundSession.getLevelEnum().equals(savedSchedule.getLevelEnum()))
                throw new ApplicationException(ErrorCodes.NOT_SYNC_LEVEL);

            return SessionsOfSchedules.builder()
                .schedule(savedSchedule)
                .session(foundSession)
                .ordinal(sessionInfo.getOrdinal())
                .build();
        }).toList());

        return savedSchedule;
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public Schedule updateSchedule(UpdateScheduleRequest request) throws ApplicationException {
        var formerSch = scheduleRepository.findById(request.getScheduleId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        if (subscriptionRepository.existsByScheduleScheduleId(request.getScheduleId()))
            throw new ApplicationException(ErrorCodes.SCHEDULE_SUBSCRIPTIONS_VIOLATION);

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

        if (subscriptionRepository.existsByScheduleScheduleId(request.getId()))
            throw new ApplicationException(ErrorCodes.SCHEDULE_SUBSCRIPTIONS_VIOLATION);

        sessionsOfSchedulesRepository.deleteAllByScheduleScheduleId(request.getId());
        scheduleRepository.deleteById(request.getId());
    }

    public TablePagesResponse<Schedule> getAvailableSchedulesOfUserPages(PaginatedTableRequest request,
                                                                         String accessToken) {
        Pageable pageableCf = pageMappers.tablePageRequestToPageable(request).toPageable(Schedule.class);
        String email = jwtService.readPayload(accessToken).get("sub");

        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<Schedule> repoRes = scheduleRepository.findAllAvailableScheduleOfUser(email, pageableCf);
            return TablePagesResponse.<Schedule>builder().data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages()).currentPage(request.getPage()).build();
        }

        ScheduleRequest scheduleInfo;
        try {
            scheduleInfo = ScheduleRequest.buildFromHashMap(request.getFilterFields());
        } catch (ApplicationException | NullPointerException | IllegalArgumentException | NoSuchFieldException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }

        Page<Schedule> repoRes = scheduleRepository.findAllAvailableScheduleOfUser(email, scheduleInfo, pageableCf);
        return TablePagesResponse.<Schedule>builder().data(repoRes.stream().toList())
            .totalPages(repoRes.getTotalPages()).currentPage(request.getPage()).build();
    }

    public Map<String, Object> getSessionsQuantityOfSchedule(ByIdDto request) {
        return Map.of("sessionsQuantity", scheduleRepository
            .findById(request.getId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY))
            .getSessionsOfSchedule().size());
    }
}
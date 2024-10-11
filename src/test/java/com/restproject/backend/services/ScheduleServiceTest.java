//package com.restproject.backend.services;
//
//import com.restproject.backend.dtos.request.DeleteObjectRequest;
//import com.restproject.backend.dtos.request.NewScheduleRequest;
//import com.restproject.backend.dtos.request.UpdateScheduleRequest;
//import com.restproject.backend.entities.Schedule;
//import com.restproject.backend.entities.Session;
//import com.restproject.backend.entities.SessionsOfSchedules;
//import com.restproject.backend.enums.ErrorCodes;
//import com.restproject.backend.enums.Level;
//import com.restproject.backend.exceptions.ApplicationException;
//import com.restproject.backend.mappers.ScheduleMappers;
//import com.restproject.backend.repositories.ScheduleRepository;
//import com.restproject.backend.repositories.SessionRepository;
//import com.restproject.backend.repositories.SessionsOfSchedulesRepository;
//import com.restproject.backend.services.ScheduleService;
//import lombok.AccessLevel;
//import lombok.experimental.FieldDefaults;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.dao.DataIntegrityViolationException;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class ScheduleServiceTest {
//    @Autowired
//    ScheduleService scheduleServiceOfAdmin;
//
//    @MockBean
//    ScheduleMappers scheduleMappers;
//    @MockBean
//    ScheduleRepository scheduleRepository;
//    @MockBean
//    SessionRepository sessionRepository;
//    @MockBean
//    SessionsOfSchedulesRepository sessionsOfSchedulesRepository;
//
//
//    @Test
//    public void createSchedule_admin_valid() {
//        var foundSessions = List.of(
//            Session.builder().sessionId(1L).levelEnum(Level.INTERMEDIATE).build(),
//            Session.builder().sessionId(2L).levelEnum(Level.INTERMEDIATE).build()
//        );
//        var req = NewScheduleRequest.builder().name("Shoulders - immediate")
//            .description("Just shoulder's sessions in total under 1 hour")
//            .sessionIds(foundSessions.stream().map(Session::getSessionId).toList())
//            .coins(2000L).level(Level.INTERMEDIATE.getLevel()).build();
//        var savedSchedule = Schedule.builder().name(req.getName()).description(req.getDescription())
//            .levelEnum(Level.getByLevel(req.getLevel())).sessionsOfSchedule(foundSessions).build();
//        var sessionsSchedules = foundSessions.stream().map(e ->
//            SessionsOfSchedules.builder().session(e).schedule(savedSchedule).build()).toList();
//
//        Mockito.when(scheduleMappers.insertionToPlain(req)).thenReturn(savedSchedule);
//        Mockito.when(scheduleRepository.save(savedSchedule)).thenReturn(savedSchedule);
//        foundSessions.forEach(e -> {
//            Mockito.when(sessionRepository.findById(e.getSessionId())).thenReturn(Optional.of(e));
//        });
//        Mockito.when(sessionsOfSchedulesRepository.saveAll(sessionsSchedules)).thenReturn(sessionsSchedules);
//
//        Schedule actual = scheduleServiceOfAdmin.createSchedule(req);
//
//        assertNotNull(actual);
//        assertEquals(actual, savedSchedule);
//        Mockito.verify(scheduleMappers, Mockito.times(1)).insertionToPlain(req);
//        Mockito.verify(scheduleRepository, Mockito.times(1)).save(savedSchedule);
//        foundSessions.forEach(e -> {
//            Mockito.verify(sessionRepository, Mockito.times(1)).findById(e.getSessionId());
//            assertEquals(e.getLevelEnum(), actual.getLevelEnum());
//        });
//        Mockito.verify(sessionsOfSchedulesRepository, Mockito.times(1)).saveAll(sessionsSchedules);
//    }
//
//    @Test
//    public void createSchedule_admin_duplicatedScheduleWithLevel() {
//        var foundSessions = List.of(
//            Session.builder().sessionId(1L).levelEnum(Level.INTERMEDIATE).build()
//        );
//        var req = NewScheduleRequest.builder().name("Shoulders - immediate")
//            .description("Just shoulder's sessions in total under 1 hour")
//            .sessionIds(List.of(foundSessions.getFirst().getSessionId(), 9_999L))
//            .coins(2000L).level(foundSessions.getFirst().getLevelEnum().getLevel()).build();
//        var savedSchedule = Schedule.builder().name(req.getName()).description(req.getDescription())
//            .levelEnum(foundSessions.getFirst().getLevelEnum()).build();
//
//        Mockito.when(scheduleMappers.insertionToPlain(req)).thenReturn(savedSchedule);
//        Mockito.when(scheduleRepository.save(savedSchedule)).thenThrow(DataIntegrityViolationException.class);
//
//        var exc = assertThrows(ApplicationException.class , () -> scheduleServiceOfAdmin.createSchedule(req));
//
//        assertEquals(ErrorCodes.DUPLICATED_SCHEDULE, exc.getErrorCodes());
//        Mockito.verify(scheduleMappers, Mockito.times(1)).insertionToPlain(req);
//        Mockito.verify(scheduleRepository, Mockito.times(1)).save(savedSchedule);
//    }
//
//    @Test
//    public void createSchedule_admin_invalidSessionIds() {
//        var foundSessions = List.of(
//            Session.builder().sessionId(1L).levelEnum(Level.INTERMEDIATE).build()
//        );
//        var invalidId = 9_9999L;
//        var req = NewScheduleRequest.builder().name("Shoulders - immediate")
//            .description("Just shoulder's sessions in total under 1 hour")
//            .sessionIds(List.of(foundSessions.getFirst().getSessionId(), invalidId))
//            .coins(2000L).level(foundSessions.getFirst().getLevelEnum().getLevel()).build();
//        var savedSchedule = Schedule.builder().name(req.getName()).description(req.getDescription())
//            .levelEnum(foundSessions.getFirst().getLevelEnum()).build();
//
//        Mockito.when(scheduleMappers.insertionToPlain(req)).thenReturn(savedSchedule);
//        Mockito.when(scheduleRepository.save(savedSchedule)).thenReturn(savedSchedule);
//        Mockito.when(sessionRepository.findById(req.getSessionIds().stream().toList().getFirst()))
//            .thenReturn(Optional.of(foundSessions.getFirst()));
//        Mockito.when(sessionRepository.findById(invalidId)).thenReturn(Optional.empty());
//
//        var exc = assertThrows(ApplicationException.class , () -> scheduleServiceOfAdmin.createSchedule(req));
//
//        assertEquals(ErrorCodes.INVALID_IDS_COLLECTION, exc.getErrorCodes());
//        Mockito.verify(scheduleMappers, Mockito.times(1)).insertionToPlain(req);
//        Mockito.verify(scheduleRepository, Mockito.times(1)).save(savedSchedule);
//        Mockito.verify(sessionRepository, Mockito.times(1))
//            .findById(req.getSessionIds().stream().toList().getFirst());
//        assertEquals(savedSchedule.getLevelEnum(), foundSessions.getFirst().getLevelEnum());
//        Mockito.verify(sessionRepository, Mockito.times(1)).findById(invalidId);
//    }
//
//    @Test
//    public void createSchedule_admin_invalidLevel() {
//        var foundSessions = List.of(
//            Session.builder().sessionId(1L).levelEnum(Level.INTERMEDIATE).build(),
//            Session.builder().sessionId(2L).levelEnum(Level.BEGINNER).build()
//        );
//        var req = NewScheduleRequest.builder().name("Shoulders - immediate")
//            .description("Just shoulder's sessions in total under 1 hour")
//            .sessionIds(foundSessions.stream().map(Session::getSessionId).toList())
//            .level(Level.INTERMEDIATE.getLevel()).build();
//        var savedSchedule = Schedule.builder().name(req.getName()).description(req.getDescription())
//            .levelEnum(Level.INTERMEDIATE).build();
//
//        Mockito.when(scheduleMappers.insertionToPlain(req)).thenReturn(savedSchedule);
//        Mockito.when(scheduleRepository.save(savedSchedule)).thenReturn(savedSchedule);
//        foundSessions.forEach(e ->
//            Mockito.when(sessionRepository.findById(e.getSessionId())).thenReturn(Optional.of(e)));
//
//        var exc = assertThrows(ApplicationException.class , () -> scheduleServiceOfAdmin.createSchedule(req));
//
//        assertEquals(ErrorCodes.NOT_SYNC_LEVEL, exc.getErrorCodes());
//        Mockito.verify(scheduleMappers, Mockito.times(1)).insertionToPlain(req);
//        Mockito.verify(scheduleRepository, Mockito.times(1)).save(savedSchedule);
//        Mockito.verify(sessionRepository, Mockito.times(1))
//            .findById(foundSessions.getFirst().getSessionId());
//        assertEquals(foundSessions.getFirst().getLevelEnum(), savedSchedule.getLevelEnum());
//        Mockito.verify(sessionRepository, Mockito.times(1))
//            .findById(foundSessions.getLast().getSessionId());
//        assertNotEquals(foundSessions.getLast().getLevelEnum(), savedSchedule.getLevelEnum());
//    }
//
//    UpdateScheduleRequest updateMuscle() {
//        return UpdateScheduleRequest.builder().scheduleId(1L).name("Push-ups").level(2).description("Hello")
//            .coins(2000L).build();
//    }
//
//    @Test
//    public void updateScheduleAndMuscles_admin_validWithoutUpdatingMuscles() {
//        var sesReq = this.updateMuscle();
//        var sesRes = Schedule.builder().scheduleId(sesReq.getScheduleId()).name(sesReq.getName())
//            .description("Hello").levelEnum(Level.getByLevel(sesReq.getLevel())).coins(sesReq.getCoins()).build();
//
//        Mockito.when(scheduleRepository.findById(sesReq.getScheduleId())).thenReturn(Optional.of(sesRes));
//        Mockito.when(sessionsOfSchedulesRepository.existsByScheduleScheduleId(sesRes.getScheduleId()))
//            .thenReturn(false);
//        Mockito.doNothing().when(scheduleMappers).updateTarget(sesRes, sesReq);
//        Mockito.doNothing().when(scheduleRepository).deleteById(sesRes.getScheduleId());
//        Mockito.when(scheduleRepository.save(sesRes)).thenReturn(sesRes);
//
//        Schedule actual = scheduleServiceOfAdmin.updateSchedule(sesReq);
//
//        Mockito.verify(scheduleRepository, Mockito.times(1)).findById(sesReq.getScheduleId());
//        Mockito.verify(sessionsOfSchedulesRepository, Mockito.times(1))
//            .existsByScheduleScheduleId(sesRes.getScheduleId());
//        Mockito.verify(scheduleMappers, Mockito.times(1)).updateTarget(sesRes, sesReq);
//        Mockito.verify(scheduleRepository, Mockito.times(1)).deleteById(sesRes.getScheduleId());
//        Mockito.verify(scheduleRepository, Mockito.times(1)).save(sesRes);
//
//        assertEquals(sesRes, actual);
//    }
//
//    @Test
//    public void updateScheduleAndMuscles_admin_validWithUpdatingMuscles() {
//        var sesReq = this.updateMuscle();
//        var sesRes = Schedule.builder().scheduleId(sesReq.getScheduleId()).name(sesReq.getName())
//            .description("Hello").levelEnum(Level.getByLevel(sesReq.getLevel())).coins(sesReq.getCoins()).build();
//
//        Mockito.when(scheduleRepository.findById(sesReq.getScheduleId())).thenReturn(Optional.of(sesRes));
//        Mockito.when(sessionsOfSchedulesRepository.existsByScheduleScheduleId(sesRes.getScheduleId()))
//            .thenReturn(false);
//        Mockito.doNothing().when(scheduleMappers).updateTarget(sesRes, sesReq);
//        Mockito.doNothing().when(scheduleRepository).deleteById(sesRes.getScheduleId());
//        Mockito.when(scheduleRepository.save(sesRes)).thenReturn(sesRes);
//
//        Schedule actual = scheduleServiceOfAdmin.updateSchedule(sesReq);
//
//        Mockito.verify(scheduleRepository, Mockito.times(1))
//            .findById(sesReq.getScheduleId());
//        Mockito.verify(sessionsOfSchedulesRepository, Mockito.times(1))
//            .existsByScheduleScheduleId(sesRes.getScheduleId());
//        Mockito.verify(scheduleMappers, Mockito.times(1))
//            .updateTarget(sesRes, sesReq);
//        Mockito.verify(scheduleRepository, Mockito.times(1))
//            .deleteById(sesRes.getScheduleId());
//        Mockito.verify(scheduleRepository, Mockito.times(1))
//            .save(sesRes);
//
//        assertEquals(sesRes, actual);
//    }
//
//    @Test
//    public void updateScheduleAndMuscles_admin_scheduleIdNotFound() {
//        var sesReq = this.updateMuscle();
//
//        Mockito.when(scheduleRepository.findById(sesReq.getScheduleId())).thenReturn(Optional.empty());
//
//        var exception = assertThrows(ApplicationException.class, () -> scheduleServiceOfAdmin.updateSchedule(sesReq));
//        Mockito.verify(scheduleRepository, Mockito.times(1)).findById(sesReq.getScheduleId());
//        assertEquals(ErrorCodes.INVALID_PRIMARY.getMessage(), exception.getMessage());
//    }
//
//    @Test
//    public void deleteSchedule_admin_valid() {
//        var req = DeleteObjectRequest.builder().id(2L).build();
//        Mockito.when(scheduleRepository.existsById(req.getId())).thenReturn(true);
//        Mockito.when(sessionsOfSchedulesRepository.existsByScheduleScheduleId(req.getId())).thenReturn(false);
//        Mockito.doNothing().when(scheduleRepository).deleteById(req.getId());
//
//        scheduleServiceOfAdmin.deleteSchedule(req);
//
//        Mockito.verify(scheduleRepository, Mockito.times(1)).existsById(req.getId());
//        Mockito.verify(sessionsOfSchedulesRepository, Mockito.times(1))
//            .existsByScheduleScheduleId(req.getId());
//        Mockito.verify(scheduleRepository, Mockito.times(1)).deleteById(req.getId());
//    }
//
//    @Test
//    public void deleteSchedule_admin_scheduleIdNotFound() {
//        var req = DeleteObjectRequest.builder().id(2L).build();
//        Mockito.when(scheduleRepository.existsById(req.getId())).thenReturn(false);
//
//        var exc = assertThrows(ApplicationException.class, () -> scheduleServiceOfAdmin.deleteSchedule(req));
//        Mockito.verify(scheduleRepository, Mockito.times(1)).existsById(req.getId());
//        assertEquals(ErrorCodes.INVALID_PRIMARY.getMessage(), exc.getMessage());
//    }
//
//    @Test
//    public void deleteSchedule_admin_scheduleIdRelatedToSchedule() {
//        var req = DeleteObjectRequest.builder().id(2L).build();
//        Mockito.when(scheduleRepository.existsById(req.getId())).thenReturn(true);
//        Mockito.when(sessionsOfSchedulesRepository.existsByScheduleScheduleId(req.getId()))
//            .thenReturn(true);
//
//        var exc = assertThrows(ApplicationException.class, () -> scheduleServiceOfAdmin.deleteSchedule(req));
//        Mockito.verify(scheduleRepository, Mockito.times(1)).existsById(req.getId());
//        Mockito.verify(sessionsOfSchedulesRepository, Mockito.times(1))
//            .existsByScheduleScheduleId(req.getId());
//        assertEquals(ErrorCodes.FORBIDDEN_UPDATING.getMessage(), exc.getMessage());
//    }
//}

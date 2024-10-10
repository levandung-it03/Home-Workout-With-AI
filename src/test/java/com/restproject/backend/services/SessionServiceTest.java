//package com.restproject.backend.services;
//
//import com.restproject.backend.dtos.request.DeleteObjectRequest;
//import com.restproject.backend.dtos.request.NewSessionRequest;
//import com.restproject.backend.dtos.request.UpdateSessionRequest;
//import com.restproject.backend.entities.*;
//import com.restproject.backend.enums.ErrorCodes;
//import com.restproject.backend.enums.Level;
//import com.restproject.backend.enums.Muscle;
//import com.restproject.backend.exceptions.ApplicationException;
//import com.restproject.backend.mappers.SessionMappers;
//import com.restproject.backend.repositories.*;
//import com.restproject.backend.services.SessionService;
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
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class SessionServiceTest {
//    @Autowired
//    SessionService sessionServiceOfAdmin;
//
//    @MockBean
//    SessionRepository sessionRepository;
//    @MockBean
//    ExercisesOfSessionsRepository exercisesOfSessionsRepository;
//    @MockBean
//    MusclesOfSessionsRepository musclesOfSessionsRepository;
//    @MockBean
//    SessionsOfSchedulesRepository sessionsOfSchedulesRepository;
//    @MockBean
//    ExerciseRepository exerciseRepository;
//    @MockBean
//    SessionMappers sessionMappers;
//
//    @Test
//    public void createSession_admin_valid() {
//        var foundExercises = List.of(
//            Exercise.builder().exerciseId(1L).levelEnum(Level.INTERMEDIATE).build(),
//            Exercise.builder().exerciseId(2L).levelEnum(Level.INTERMEDIATE).build()
//        );
//        var req = NewSessionRequest.builder().name("Shoulders - immediate")
//            .description("Just shoulder's exercises in total under 1 hour")
//            .muscleIds(List.of(Muscle.SHOULDERS.getId()))
//            .exerciseIds(foundExercises.stream().map(Exercise::getExerciseId).toList())
//            .level(Level.INTERMEDIATE.getLevel()).build();
//        var savedSession = Session.builder().name(req.getName()).description(req.getDescription())
//            .levelEnum(Level.getByLevel(req.getLevel())).exercisesOfSession(foundExercises).build();
//        var musclesOfSessions = req.getMuscleIds().stream().map(id ->
//            MusclesOfSessions.builder().muscle(Muscle.getById(id)).session(savedSession).build()).toList();
//        var exercisesSessions = foundExercises.stream().map(e ->
//            ExercisesOfSessions.builder().exercise(e).session(savedSession).build()).toList();
//
//        Mockito.when(sessionMappers.insertionToPlain(req)).thenReturn(savedSession);
//        Mockito.when(sessionRepository.save(savedSession)).thenReturn(savedSession);
//        Mockito.when(musclesOfSessionsRepository.saveAll(musclesOfSessions)).thenReturn(musclesOfSessions);
//        foundExercises.forEach(e -> {
//            Mockito.when(exerciseRepository.findById(e.getExerciseId())).thenReturn(Optional.of(e));
//        });
//        Mockito.when(exercisesOfSessionsRepository.saveAll(exercisesSessions)).thenReturn(exercisesSessions);
//
//        Session actual = sessionServiceOfAdmin.createSession(req);
//
//        assertNotNull(actual);
//        assertEquals(actual, savedSession);
//        Mockito.verify(sessionMappers, Mockito.times(1)).insertionToPlain(req);
//        Mockito.verify(sessionRepository, Mockito.times(1)).save(savedSession);
//        Mockito.verify(musclesOfSessionsRepository, Mockito.times(1)).saveAll(musclesOfSessions);
//        foundExercises.forEach(e -> {
//            Mockito.verify(exerciseRepository, Mockito.times(1)).findById(e.getExerciseId());
//            assertEquals(e.getLevelEnum(), actual.getLevelEnum());
//        });
//        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1)).saveAll(exercisesSessions);
//    }
//
//    @Test
//    public void createSession_admin_duplicatedSessionWithLevel() {
//        var foundExercises = List.of(
//            Exercise.builder().exerciseId(1L).levelEnum(Level.INTERMEDIATE).build()
//        );
//        var req = NewSessionRequest.builder().name("Shoulders - immediate")
//            .description("Just shoulder's exercises in total under 1 hour")
//            .muscleIds(List.of(Muscle.SHOULDERS.getId()))
//            .exerciseIds(List.of(foundExercises.getFirst().getExerciseId(), 9_999L))
//            .level(foundExercises.getFirst().getLevelEnum().getLevel()).build();
//        var savedSession = Session.builder().name(req.getName()).description(req.getDescription())
//            .levelEnum(foundExercises.getFirst().getLevelEnum()).build();
//
//        Mockito.when(sessionMappers.insertionToPlain(req)).thenReturn(savedSession);
//        Mockito.when(sessionRepository.save(savedSession)).thenThrow(DataIntegrityViolationException.class);
//
//        var exc = assertThrows(ApplicationException.class , () -> sessionServiceOfAdmin.createSession(req));
//
//        assertEquals(ErrorCodes.DUPLICATED_SESSION, exc.getErrorCodes());
//        Mockito.verify(sessionMappers, Mockito.times(1)).insertionToPlain(req);
//        Mockito.verify(sessionRepository, Mockito.times(1)).save(savedSession);
//    }
//
//    @Test
//    public void createSession_admin_invalidExerciseIds() {
//        var foundExercises = List.of(
//            Exercise.builder().exerciseId(1L).levelEnum(Level.INTERMEDIATE).build()
//        );
//        var invalidId = 9_9999L;
//        var req = NewSessionRequest.builder().name("Shoulders - immediate")
//            .description("Just shoulder's exercises in total under 1 hour")
//            .muscleIds(List.of(Muscle.SHOULDERS.getId()))
//            .exerciseIds(List.of(foundExercises.getFirst().getExerciseId(), invalidId))
//            .level(foundExercises.getFirst().getLevelEnum().getLevel()).build();
//        var savedSession = Session.builder().name(req.getName()).description(req.getDescription())
//            .levelEnum(foundExercises.getFirst().getLevelEnum()).build();
//        var musclesOfSessions = req.getMuscleIds().stream().map(id ->
//            MusclesOfSessions.builder().muscle(Muscle.getById(id)).session(savedSession).build()).toList();
//
//        Mockito.when(sessionMappers.insertionToPlain(req)).thenReturn(savedSession);
//        Mockito.when(sessionRepository.save(savedSession)).thenReturn(savedSession);
//        Mockito.when(musclesOfSessionsRepository.saveAll(musclesOfSessions)).thenReturn(musclesOfSessions);
//        Mockito.when(exerciseRepository.findById(req.getExerciseIds().stream().toList().getFirst()))
//            .thenReturn(Optional.of(foundExercises.getFirst()));
//        Mockito.when(exerciseRepository.findById(invalidId)).thenReturn(Optional.empty());
//
//        var exc = assertThrows(ApplicationException.class , () -> sessionServiceOfAdmin.createSession(req));
//
//        assertEquals(ErrorCodes.INVALID_PRIMARY, exc.getErrorCodes());
//        Mockito.verify(sessionMappers, Mockito.times(1)).insertionToPlain(req);
//        Mockito.verify(sessionRepository, Mockito.times(1)).save(savedSession);
//        Mockito.when(musclesOfSessionsRepository.saveAll(musclesOfSessions)).thenReturn(musclesOfSessions);
//        Mockito.verify(exerciseRepository, Mockito.times(1))
//            .findById(req.getExerciseIds().stream().toList().getFirst());
//        assertEquals(savedSession.getLevelEnum(), foundExercises.getFirst().getLevelEnum());
//        Mockito.verify(exerciseRepository, Mockito.times(1)).findById(invalidId);
//    }
//
//    @Test
//    public void createSession_admin_invalidLevel() {
//        var foundExercises = List.of(
//            Exercise.builder().exerciseId(1L).levelEnum(Level.INTERMEDIATE).build(),
//            Exercise.builder().exerciseId(2L).levelEnum(Level.BEGINNER).build()
//        );
//        var req = NewSessionRequest.builder().name("Shoulders - immediate")
//            .description("Just shoulder's exercises in total under 1 hour")
//            .muscleIds(List.of(Muscle.SHOULDERS.getId()))
//            .exerciseIds(foundExercises.stream().map(Exercise::getExerciseId).toList())
//            .level(Level.INTERMEDIATE.getLevel()).build();
//        var savedSession = Session.builder().name(req.getName()).description(req.getDescription())
//            .levelEnum(Level.INTERMEDIATE).build();
//        var musclesOfSessions = req.getMuscleIds().stream().map(id ->
//            MusclesOfSessions.builder().muscle(Muscle.getById(id)).session(savedSession).build()).toList();
//
//        Mockito.when(sessionMappers.insertionToPlain(req)).thenReturn(savedSession);
//        Mockito.when(sessionRepository.save(savedSession)).thenReturn(savedSession);
//        Mockito.when(musclesOfSessionsRepository.saveAll(musclesOfSessions)).thenReturn(musclesOfSessions);
//        foundExercises.forEach(e ->
//            Mockito.when(exerciseRepository.findById(e.getExerciseId())).thenReturn(Optional.of(e)));
//
//        var exc = assertThrows(ApplicationException.class , () -> sessionServiceOfAdmin.createSession(req));
//
//        assertEquals(ErrorCodes.NOT_SYNC_LEVEL, exc.getErrorCodes());
//        Mockito.verify(sessionMappers, Mockito.times(1)).insertionToPlain(req);
//        Mockito.verify(sessionRepository, Mockito.times(1)).save(savedSession);
//        Mockito.when(musclesOfSessionsRepository.saveAll(musclesOfSessions)).thenReturn(musclesOfSessions);
//        Mockito.verify(exerciseRepository, Mockito.times(1))
//            .findById(foundExercises.getFirst().getExerciseId());
//        assertEquals(foundExercises.getFirst().getLevelEnum(), savedSession.getLevelEnum());
//        Mockito.verify(exerciseRepository, Mockito.times(1))
//            .findById(foundExercises.getLast().getExerciseId());
//        assertNotEquals(foundExercises.getLast().getLevelEnum(), savedSession.getLevelEnum());
//    }
//
//    UpdateSessionRequest updateSessionAndMusclesRequest() {
//        return UpdateSessionRequest.builder().sessionId(1L).name("Push-ups").level(2).description("Hello")
//            .muscleIds(List.of(0, 2)).build();
//    }
//
//    @Test
//    public void updateSessionAndMuscles_admin_validWithoutUpdatingMuscles() {
//        var sesReq = this.updateSessionAndMusclesRequest();
//        var sesRes = Session.builder().sessionId(sesReq.getSessionId()).name(sesReq.getName())
//            .description("Hello").levelEnum(Level.getByLevel(sesReq.getLevel())).build();
//        var msByEx = sesReq.getMuscleIds().stream().map(id ->
//            MusclesOfSessions.builder().session(sesRes).muscle(Muscle.getById(id)).build()
//        ).toList();
//
//        Mockito.when(sessionRepository.findById(sesReq.getSessionId())).thenReturn(Optional.of(sesRes));
//        Mockito.when(sessionsOfSchedulesRepository.existsBySessionSessionId(sesRes.getSessionId()))
//            .thenReturn(false);
//        Mockito.when(musclesOfSessionsRepository.findAllBySessionSessionId(sesRes.getSessionId())).thenReturn(msByEx);
//        Mockito.doNothing().when(sessionMappers).updateTarget(sesRes, sesReq);
//        Mockito.doNothing().when(sessionRepository).deleteById(sesRes.getSessionId());
//        Mockito.when(sessionRepository.save(sesRes)).thenReturn(sesRes);
//
//        Session actual = sessionServiceOfAdmin.updateSessionAndMuscles(sesReq);
//
//        Mockito.verify(sessionRepository, Mockito.times(1)).findById(sesReq.getSessionId());
//        Mockito.verify(sessionsOfSchedulesRepository, Mockito.times(1))
//            .existsBySessionSessionId(sesRes.getSessionId());
//        Mockito.verify(musclesOfSessionsRepository, Mockito.times(1))
//            .findAllBySessionSessionId(sesRes.getSessionId());
//        Mockito.verify(sessionMappers, Mockito.times(1)).updateTarget(sesRes, sesReq);
//        Mockito.verify(sessionRepository, Mockito.times(1)).deleteById(sesRes.getSessionId());
//        Mockito.verify(sessionRepository, Mockito.times(1)).save(sesRes);
//
//        assertEquals(sesRes, actual);
//    }
//
//    @Test
//    public void updateSessionAndMuscles_admin_validWithUpdatingMuscles() {
//        var sesReq = this.updateSessionAndMusclesRequest();
//        var sesRes = Session.builder().sessionId(sesReq.getSessionId()).name(sesReq.getName())
//            .description("Hello").levelEnum(Level.getByLevel(sesReq.getLevel())).build();
//        var msByEx = sesReq.getMuscleIds().stream().map(id ->
//            MusclesOfSessions.builder().session(sesRes).muscle(Muscle.getById(id)).build()
//        ).toList();
//        sesReq.setMuscleIds(List.of(5, 6)); //--Actual new muscle-ids of updated session.
//        var newMsByEx = sesReq.getMuscleIds().stream().map(id ->
//            MusclesOfSessions.builder().session(sesRes).muscle(Muscle.getById(id)).build()
//        ).toList();
//
//        Mockito.when(sessionRepository.findById(sesReq.getSessionId())).thenReturn(Optional.of(sesRes));
//        Mockito.when(sessionsOfSchedulesRepository.existsBySessionSessionId(sesRes.getSessionId()))
//            .thenReturn(false);
//        Mockito.when(musclesOfSessionsRepository.findAllBySessionSessionId(sesRes.getSessionId())).thenReturn(msByEx);
//        Mockito.doNothing().when(sessionMappers).updateTarget(sesRes, sesReq);
//        Mockito.doNothing().when(sessionRepository).deleteById(sesRes.getSessionId());
//        Mockito.when(sessionRepository.save(sesRes)).thenReturn(sesRes);
//        Mockito.doNothing().when(musclesOfSessionsRepository).deleteAllBySessionSessionId(sesRes.getSessionId());
//        Mockito.when(musclesOfSessionsRepository.saveAll(newMsByEx)).thenReturn(Mockito.anyList());
//
//        Session actual = sessionServiceOfAdmin.updateSessionAndMuscles(sesReq);
//
//        Mockito.verify(sessionRepository, Mockito.times(1))
//            .findById(sesReq.getSessionId());
//        Mockito.verify(sessionsOfSchedulesRepository, Mockito.times(1))
//            .existsBySessionSessionId(sesRes.getSessionId());
//        Mockito.verify(musclesOfSessionsRepository, Mockito.times(1))
//            .findAllBySessionSessionId(sesRes.getSessionId());
//        Mockito.verify(sessionMappers, Mockito.times(1))
//            .updateTarget(sesRes, sesReq);
//        Mockito.verify(sessionRepository, Mockito.times(1))
//            .deleteById(sesRes.getSessionId());
//        Mockito.verify(sessionRepository, Mockito.times(1))
//            .save(sesRes);
//        Mockito.verify(musclesOfSessionsRepository, Mockito.times(1))
//            .deleteAllBySessionSessionId(sesRes.getSessionId());
//        Mockito.verify(musclesOfSessionsRepository, Mockito.times(1))
//            .saveAll(newMsByEx);
//
//        assertEquals(sesRes, actual);
//    }
//
//    @Test
//    public void updateSessionAndMuscles_admin_sessionIdNotFound() {
//        var sesReq = this.updateSessionAndMusclesRequest();
//
//        Mockito.when(sessionRepository.findById(sesReq.getSessionId())).thenReturn(Optional.empty());
//
//        var exception = assertThrows(ApplicationException.class, () -> sessionServiceOfAdmin.updateSessionAndMuscles(sesReq));
//        Mockito.verify(sessionRepository, Mockito.times(1)).findById(sesReq.getSessionId());
//        assertEquals(ErrorCodes.INVALID_PRIMARY.getMessage(), exception.getMessage());
//    }
//
//    @Test
//    public void updateSessionAndMuscles_admin_sessionIdRelatedToSession() {
//        var sesReq = this.updateSessionAndMusclesRequest();
//        var sesRes = Session.builder().sessionId(sesReq.getSessionId()).name(sesReq.getName())
//            .description("Hello").levelEnum(Level.getByLevel(sesReq.getLevel())).build();
//
//        Mockito.when(sessionRepository.findById(sesReq.getSessionId())).thenReturn(Optional.of(sesRes));
//        Mockito.when(sessionsOfSchedulesRepository.existsBySessionSessionId(sesReq.getSessionId()))
//            .thenReturn(true);
//
//        var exception = assertThrows(ApplicationException.class, () -> sessionServiceOfAdmin.updateSessionAndMuscles(sesReq));
//        Mockito.verify(sessionRepository, Mockito.times(1)).findById(sesReq.getSessionId());
//        Mockito.verify(sessionsOfSchedulesRepository, Mockito.times(1))
//            .existsBySessionSessionId(sesReq.getSessionId());
//        assertEquals(ErrorCodes.FORBIDDEN_UPDATING.getMessage(), exception.getMessage());
//    }
//
//    @Test
//    public void updateSessionAndMuscles_admin_emptyFormerMuscleIds() {
//        var sesReq = this.updateSessionAndMusclesRequest();
//        var sesRes = Session.builder().sessionId(sesReq.getSessionId()).name(sesReq.getName())
//            .description("Hello").levelEnum(Level.getByLevel(sesReq.getLevel())).build();
//
//        Mockito.when(sessionRepository.findById(sesReq.getSessionId())).thenReturn(Optional.of(sesRes));
//        Mockito.when(sessionsOfSchedulesRepository.existsBySessionSessionId(sesReq.getSessionId()))
//            .thenReturn(false);
//        Mockito.when(musclesOfSessionsRepository.findAllBySessionSessionId(sesRes.getSessionId()))
//            .thenReturn(List.of());
//
//        var exception = assertThrows(ApplicationException.class, () -> sessionServiceOfAdmin.updateSessionAndMuscles(sesReq));
//        Mockito.verify(sessionRepository, Mockito.times(1)).findById(sesReq.getSessionId());
//        Mockito.verify(sessionsOfSchedulesRepository, Mockito.times(1))
//            .existsBySessionSessionId(sesReq.getSessionId());
//        Mockito.verify(musclesOfSessionsRepository, Mockito.times(1))
//            .findAllBySessionSessionId(sesReq.getSessionId());
//        assertEquals(ErrorCodes.INVALID_PRIMARY.getMessage(), exception.getMessage());
//    }
//
//    @Test
//    public void deleteSession_admin_valid() {
//        var req = DeleteObjectRequest.builder().id(2L).build();
//        Mockito.when(sessionRepository.existsById(req.getId())).thenReturn(true);
//        Mockito.when(sessionsOfSchedulesRepository.existsBySessionSessionId(req.getId())).thenReturn(false);
//        Mockito.doNothing().when(sessionRepository).deleteById(req.getId());
//        Mockito.doNothing().when(musclesOfSessionsRepository).deleteAllBySessionSessionId(req.getId());
//
//        sessionServiceOfAdmin.deleteSession(req);
//
//        Mockito.verify(sessionRepository, Mockito.times(1)).existsById(req.getId());
//        Mockito.verify(sessionsOfSchedulesRepository, Mockito.times(1))
//            .existsBySessionSessionId(req.getId());
//        Mockito.verify(sessionRepository, Mockito.times(1)).deleteById(req.getId());
//        Mockito.verify(musclesOfSessionsRepository, Mockito.times(1))
//            .deleteAllBySessionSessionId(req.getId());
//    }
//
//    @Test
//    public void deleteSession_admin_sessionIdNotFound() {
//        var req = DeleteObjectRequest.builder().id(2L).build();
//        Mockito.when(sessionRepository.existsById(req.getId())).thenReturn(false);
//
//        var exc = assertThrows(ApplicationException.class, () -> sessionServiceOfAdmin.deleteSession(req));
//        Mockito.verify(sessionRepository, Mockito.times(1)).existsById(req.getId());
//        assertEquals(ErrorCodes.INVALID_PRIMARY.getMessage(), exc.getMessage());
//    }
//
//    @Test
//    public void deleteSession_admin_sessionIdRelatedToSession() {
//        var req = DeleteObjectRequest.builder().id(2L).build();
//        Mockito.when(sessionRepository.existsById(req.getId())).thenReturn(true);
//        Mockito.when(sessionsOfSchedulesRepository.existsBySessionSessionId(req.getId()))
//            .thenReturn(true);
//
//        var exc = assertThrows(ApplicationException.class, () -> sessionServiceOfAdmin.deleteSession(req));
//        Mockito.verify(sessionRepository, Mockito.times(1)).existsById(req.getId());
//        Mockito.verify(sessionsOfSchedulesRepository, Mockito.times(1))
//            .existsBySessionSessionId(req.getId());
//        assertEquals(ErrorCodes.FORBIDDEN_UPDATING.getMessage(), exc.getMessage());
//    }
//}

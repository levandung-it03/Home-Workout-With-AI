package com.restproject.backend.services.Admin;

import com.restproject.backend.dtos.request.NewSessionRequest;
import com.restproject.backend.dtos.request.PaginatedExercisesOfSessionRequest;
import com.restproject.backend.dtos.request.PaginatedObjectRequest;
import com.restproject.backend.entities.*;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.enums.PageEnum;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.mappers.SessionMappers;
import com.restproject.backend.repositories.ExerciseRepository;
import com.restproject.backend.repositories.ExercisesOfSessionsRepository;
import com.restproject.backend.repositories.MusclesOfSessionsRepository;
import com.restproject.backend.repositories.SessionRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionServiceTest {
    @Autowired
    SessionService sessionServiceOfAdmin;
    
    @MockBean
    SessionRepository sessionRepository;
    @MockBean
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;
    @MockBean
    MusclesOfSessionsRepository musclesOfSessionsRepository;
    @MockBean
    ExerciseRepository exerciseRepository;
    @MockBean
    SessionMappers sessionMappers;
    @MockBean
    PageMappers pageMappers;

    @Test
    public void getPaginatedSessions_admin_valid() {
        var req = PaginatedObjectRequest.builder().page(1).build();
        var pgo = PageObject.builder().pageNumber(req.getPage()).pageSize(PageEnum.SIZE.getSize()).build();
        var pgb = pgo.toPageable();
        var res = new PageImpl<>(List.of(new Session(), new Session()));

        Mockito.when(pageMappers.pageRequestToPageable(req)).thenReturn(pgo);
        Mockito.when(sessionRepository.findAll(pgb)).thenReturn(res);

        List<Session> actual = sessionServiceOfAdmin.getPaginatedSessions(req);

        assertNotNull(actual);
        assertEquals(actual.size(), res.stream().toList().size());

        Mockito.verify(pageMappers, Mockito.times(1)).pageRequestToPageable(req);
        Mockito.verify(sessionRepository, Mockito.times(1)).findAll(pgb);
    }

    @Test
    public void createSession_admin_valid() {
        var foundExercises = List.of(
            Exercise.builder().exerciseId(1L).level(Level.INTERMEDIATE).build(),
            Exercise.builder().exerciseId(2L).level(Level.INTERMEDIATE).build()
        );
        var req = NewSessionRequest.builder().name("Shoulders - immediate")
            .description("Just shoulder's exercises in total under 1 hour")
            .muscleIds(List.of(Muscle.SHOULDERS.getId()))
            .exerciseIds(foundExercises.stream().map(Exercise::getExerciseId).toList())
            .level(Level.INTERMEDIATE.getLevel()).build();
        var savedSession = Session.builder().name(req.getName()).description(req.getDescription())
            .level(Level.getByLevel(req.getLevel())).exercisesOfSession(foundExercises).build();
        var exercisesSessions = foundExercises.stream().map(e ->
            ExercisesOfSessions.builder().exercise(e).session(savedSession).build()).toList();
        var musclesOfSessions = req.getMuscleIds().stream().map(id ->
            MusclesOfSessions.builder().muscle(Muscle.getById(id)).session(savedSession).build()).toList();

        Mockito.when(sessionMappers.insertionToPlain(req)).thenReturn(savedSession);
        Mockito.when(sessionRepository.save(savedSession)).thenReturn(savedSession);
        foundExercises.forEach(e -> {
            Mockito.when(exerciseRepository.findById(e.getExerciseId())).thenReturn(Optional.of(e));
        });
        Mockito.when(musclesOfSessionsRepository.saveAll(musclesOfSessions)).thenReturn(Mockito.anyList());
        Mockito.when(exercisesOfSessionsRepository.saveAll(exercisesSessions)).thenReturn(exercisesSessions);

        Session actual = sessionServiceOfAdmin.createSession(req);

        assertNotNull(actual);
        assertEquals(savedSession, actual);
        Mockito.verify(sessionMappers, Mockito.times(1)).insertionToPlain(req);
        Mockito.verify(sessionRepository, Mockito.times(1)).save(savedSession);
        Mockito.verify(exerciseRepository, Mockito.times(2)).findById(Mockito.any(Long.class));
        Mockito.verify(musclesOfSessionsRepository, Mockito.times(1)).saveAll(musclesOfSessions);
        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1)).saveAll(exercisesSessions);
    }

    @Test
    public void createSession_admin_invalidExerciseIds() {
        var req = NewSessionRequest.builder().name("Shoulders - immediate")
            .description("Just shoulder's exercises in total under 1 hour")
            .muscleIds(List.of(Muscle.SHOULDERS.getId()))
            .exerciseIds(List.of(1L, 9_999L))
            .level(Level.INTERMEDIATE.getLevel()).build();
        var savedSession = Session.builder().name(req.getName()).description(req.getDescription()).build();

        Mockito.when(sessionMappers.insertionToPlain(req)).thenReturn(savedSession);
        Mockito.when(sessionRepository.save(savedSession)).thenReturn(savedSession);
        Mockito.when(exerciseRepository.findById(req.getExerciseIds().stream().toList().getFirst()))
            .thenReturn(Optional.of(Exercise.builder().level(Level.getByLevel(req.getLevel())).exerciseId(1L).build()));
        Mockito.when(exerciseRepository.findById(req.getExerciseIds().stream().toList().getLast()))
            .thenReturn(Optional.empty());

        var exc = assertThrows(ApplicationException.class , () -> sessionServiceOfAdmin.createSession(req));

        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_PRIMARY);
        Mockito.verify(sessionMappers, Mockito.times(1)).insertionToPlain(req);
        Mockito.verify(sessionRepository, Mockito.times(1)).save(savedSession);
        Mockito.verify(exerciseRepository, Mockito.times(2)).findById(Mockito.any(Long.class));
    }

    @Test
    public void createSession_admin_invalidLevel() {
        var foundExercises = List.of(
            Exercise.builder().exerciseId(1L).level(Level.INTERMEDIATE).build(),
            Exercise.builder().exerciseId(2L).level(Level.BEGINNER).build()
        );
        var req = NewSessionRequest.builder().name("Shoulders - immediate")
            .description("Just shoulder's exercises in total under 1 hour")
            .muscleIds(List.of(Muscle.SHOULDERS.getId()))
            .exerciseIds(foundExercises.stream().map(Exercise::getExerciseId).toList())
            .level(Level.INTERMEDIATE.getLevel()).build();
        var savedSession = Session.builder().name(req.getName()).description(req.getDescription()).build();

        Mockito.when(sessionMappers.insertionToPlain(req)).thenReturn(savedSession);
        Mockito.when(sessionRepository.save(savedSession)).thenReturn(savedSession);
        foundExercises.forEach(e ->
            Mockito.when(exerciseRepository.findById(e.getExerciseId())).thenReturn(Optional.of(e)));

        var exc = assertThrows(ApplicationException.class , () -> sessionServiceOfAdmin.createSession(req));

        assertEquals(exc.getErrorCodes(), ErrorCodes.NOT_SYNC_LEVEL);
        Mockito.verify(sessionMappers, Mockito.times(1)).insertionToPlain(req);
        Mockito.verify(sessionRepository, Mockito.times(1)).save(savedSession);
        Mockito.verify(exerciseRepository, Mockito.times(2)).findById(Mockito.any(Long.class));
        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(0))
            .saveAll(Mockito.any());
    }

    @Test
    public void getPaginatedExercisesOfSession_admin_valid() {
        var ses = Session.builder().sessionId(1L).build();
        var req = PaginatedExercisesOfSessionRequest.builder().sessionId(ses.getSessionId()).page(1).build();
        var prq = PaginatedObjectRequest.builder().page(req.getPage()).build();
        var pmp = PageObject.builder().pageNumber(1).pageSize(PageEnum.SIZE.getSize()).build();
        var rls = List.of(ExercisesOfSessions.builder().session(ses).build(),
            ExercisesOfSessions.builder().session(ses).build());
        var res = new PageImpl<>(rls, pmp.toPageable(), 10);
        Mockito.when(pageMappers.pageRequestToPageable(prq)).thenReturn(pmp);
        Mockito.when(exercisesOfSessionsRepository.findAllBySessionSessionId(req.getSessionId(), pmp.toPageable()))
            .thenReturn(res);

        List<Exercise> actual = sessionServiceOfAdmin.getPaginatedExercisesOfSession(req);
        assertNotNull(actual);
        assertEquals(actual.size(), res.stream().toList().size());
        Mockito.verify(pageMappers, Mockito.times(1)).pageRequestToPageable(prq);
        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1))
            .findAllBySessionSessionId(req.getSessionId(), pmp.toPageable());
    }


}

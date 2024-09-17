package com.restproject.backend.services.Admin;

import com.restproject.backend.dtos.request.UpdateExercisesOfSessionRequest;
import com.restproject.backend.dtos.request.PaginatedRelationshipRequest;
import com.restproject.backend.dtos.response.ExercisesOfSessionResponse;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.ExercisesOfSessions;
import com.restproject.backend.entities.PageObject;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.PageEnum;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.repositories.ExerciseRepository;
import com.restproject.backend.repositories.ExercisesOfSessionsRepository;
import com.restproject.backend.repositories.SessionRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExercisesOfSessionsServiceTest {
    @Autowired
    ExercisesOfSessionsService exercisesOfSessionsServiceOfAdmin;

    @MockBean
    PageMappers pageMappers;
    @MockBean
    SessionRepository sessionRepository;
    @MockBean
    ExerciseRepository exerciseRepository;
    @MockBean
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;

    @Test
    public void getExercisesHasMusclesOfSessionPages_admin_valid() {
        var exe1 = Exercise.builder().exerciseId(1L).build();
        var exe2 = Exercise.builder().exerciseId(2L).build();
        var ses = Session.builder().sessionId(1L).build();
        var req = PaginatedRelationshipRequest.builder().id(ses.getSessionId()).page(1).build();
        var pmp = PageObject.builder().pageNumber(1).pageSize(PageEnum.SIZE.getSize()).build();
        var res = List.of(
            ExercisesOfSessionResponse.builder().exercise(exe1).muscleList(List.of()).build(),
            ExercisesOfSessionResponse.builder().exercise(exe2).muscleList(List.of()).build());
        Mockito.when(pageMappers.relationshipPageRequestToPageable(req)).thenReturn(pmp);
        Mockito.when(exercisesOfSessionsRepository
            .findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(req.getId(),
                pmp.toPageable())).thenReturn(new PageImpl<>(res, pmp.toPageable(), 10));

        List<ExercisesOfSessionResponse> actual = exercisesOfSessionsServiceOfAdmin
            .getExercisesHasMusclesOfSessionPagesPrioritizeRelationship(req);
        assertNotNull(actual);
        assertEquals(res.stream().toList().size(), actual.size());
        Mockito.verify(pageMappers, Mockito.times(1)).relationshipPageRequestToPageable(req);
        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1))
            .findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(req.getId(), pmp.toPageable());
        assertEquals(exe1.getExerciseId(), actual.get(0).getExercise().getExerciseId());
        assertEquals(exe2.getExerciseId(), actual.get(1).getExercise().getExerciseId());
    }

    @Test
    public void updateExercisesOfSession_admin_valid() {
        var request = UpdateExercisesOfSessionRequest.builder().sessionId(1L).exerciseIds(List.of(1L, 2L)).build();
        var updatedSession = Session.builder().sessionId(1L).build();
        var exercisesFromDB = request.getExerciseIds().stream().map(id ->
            Exercise.builder().exerciseId(id).build()).toList();
        var newRelationship = exercisesFromDB.stream().map(exercise ->
            ExercisesOfSessions.builder().exercise(exercise).session(updatedSession).build()
        ).toList();
        Mockito.when(sessionRepository.findById(request.getSessionId())).thenReturn(Optional.of(updatedSession));
        Mockito.when(exerciseRepository.findAllById(request.getExerciseIds())).thenReturn(exercisesFromDB);
        Mockito.doNothing().when(exercisesOfSessionsRepository).deleteAllBySessionSessionId(updatedSession.getSessionId());
        Mockito.when(exercisesOfSessionsRepository.saveAll(newRelationship)).thenReturn(newRelationship);

        List<Exercise> exercises = exercisesOfSessionsServiceOfAdmin.updateExercisesOfSession(request);

        Mockito.verify(sessionRepository, Mockito.times(1))
            .findById(request.getSessionId());
        Mockito.verify(exerciseRepository, Mockito.times(1))
            .findAllById(request.getExerciseIds());
        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1))
            .deleteAllBySessionSessionId(updatedSession.getSessionId());
        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1)).saveAll(newRelationship);

        assertEquals(exercises, newRelationship.stream().map(ExercisesOfSessions::getExercise).toList());
    }

    @Test
    public void updateExercisesOfSession_admin_invalidPrimary() {
        var request = UpdateExercisesOfSessionRequest.builder().sessionId(999L).exerciseIds(List.of(1L, 2L)).build();
        Mockito.when(sessionRepository.findById(request.getSessionId())).thenReturn(Optional.empty());

        var exc = assertThrows(ApplicationException.class, () -> exercisesOfSessionsServiceOfAdmin
            .updateExercisesOfSession(request));

        Mockito.verify(sessionRepository, Mockito.times(1))
            .findById(request.getSessionId());
        assertEquals(ErrorCodes.INVALID_PRIMARY, exc.getErrorCodes());
    }

    @Test
    public void updateExercisesOfSession_admin_invalidExerciseIds() {
        var request = UpdateExercisesOfSessionRequest.builder().sessionId(1L)
            .exerciseIds(List.of(1L, 9_999L)).build();
        var updatedSession = Session.builder().sessionId(1L).build();
        var exercisesFromDB = List.of(Exercise.builder().exerciseId(1L).build());
        Mockito.when(sessionRepository.findById(request.getSessionId())).thenReturn(Optional.of(updatedSession));
        Mockito.when(exerciseRepository.findAllById(request.getExerciseIds())).thenReturn(exercisesFromDB);

        var exc = assertThrows(ApplicationException.class, () -> exercisesOfSessionsServiceOfAdmin
            .updateExercisesOfSession(request));

        Mockito.verify(sessionRepository, Mockito.times(1))
            .findById(request.getSessionId());
        Mockito.verify(exerciseRepository, Mockito.times(1))
            .findAllById(request.getExerciseIds());
        assertEquals(ErrorCodes.INVALID_IDS_COLLECTION, exc.getErrorCodes());
    }

}

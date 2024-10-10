package com.restproject.backend.services;

import com.restproject.backend.dtos.request.UpdateExercisesOfSessionRequest;
import com.restproject.backend.dtos.request.PaginatedRelationshipRequest;
import com.restproject.backend.dtos.response.ExercisesOfSessionResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.ExercisesOfSessions;
import com.restproject.backend.entities.PageObject;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.enums.PageEnum;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.ExerciseRepository;
import com.restproject.backend.repositories.ExercisesOfSessionsRepository;
import com.restproject.backend.repositories.SessionRepository;
import com.restproject.backend.services.ExercisesOfSessionsService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExercisesOfSessionsServiceTest {
    @Autowired
    ExercisesOfSessionsService exercisesOfSessionsServiceOfAdmin;

    @MockBean
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;
    @MockBean
    SessionRepository sessionRepository;
    @MockBean
    ExerciseRepository exerciseRepository;


    @Test
    public void getExercisesHasMusclesOfSessionPagesPrioritizeRelationship_admin_valid() {
        //--Build request data.
        var request = PaginatedRelationshipRequest.builder().id(1L).page(1)
            .filterFields(new HashMap<>(Map.ofEntries(
                Map.entry("name", "Stre"),
                Map.entry("level", 2),
                Map.entry("muscleList", "[0,2]")
            ))).sortedField("name").sortedMode(1)
            .build();
        var muscleList = List.of(Muscle.CHEST, Muscle.TRICEPS);
        var pageObject = PageObject.builder().page(request.getPage()).build();

        //--Build response data.
        var repoResponse = new ArrayList<Object[]>();
        repoResponse.add(    //--exerciseId,name,basicReps,level::String,withSession,muscleList::List<String>
            new Object[]{10L,"Strength",14,Level.INTERMEDIATE.toString(),true,
                muscleList.stream().map(Muscle::toString).toList()}
        );
        var res = repoResponse.stream().map(ExercisesOfSessionResponse::buildFromNativeQuery).toList();

        //--Mocking Bean's actions.
        Mockito
            .when(exercisesOfSessionsRepository.findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
                Mockito.eq(request.getId()), Mockito.any(ExercisesOfSessionResponse.class), Mockito.any(Pageable.class)))
            .thenReturn(new PageImpl<>(repoResponse, pageObject.toPageable(), PageEnum.SIZE.getSize()));

        TablePagesResponse<ExercisesOfSessionResponse> actual = exercisesOfSessionsServiceOfAdmin
            .getExercisesHasMusclesOfSessionPagesPrioritizeRelationship(request);

        assertNotNull(actual);
        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1))
            .findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
                Mockito.eq(request.getId()), Mockito.any(ExercisesOfSessionResponse.class), Mockito.any(Pageable.class));
        assertEquals(
            new HashSet<>(res.getFirst().getMuscleList()),
            new HashSet<>(actual.getData().getFirst().getMuscleList())
        );
    }

    @Test
    public void getExercisesHasMusclesOfSessionPagesPrioritizeRelationship_admin_invalidSortedField() {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", 2);
        var req = PaginatedRelationshipRequest.builder().page(1).filterFields(ftr).sortedField("unknown").build();
        var exc = assertThrows(ApplicationException.class, () -> exercisesOfSessionsServiceOfAdmin
            .getExercisesHasMusclesOfSessionPagesPrioritizeRelationship(req));

        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_SORTING_FIELD_OR_VALUE);
    }

    @Test
    public void getExercisesHasMusclesOfSessionPagesPrioritizeRelationship_admin_invalidFilteringValues() {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", 4);
        List<Object> muscleList = List.of(1, 2, 3);
        var req = PaginatedRelationshipRequest.builder().page(1).filterFields(ftr).build();

        ftr.put("muscleList", muscleList);
        var exc = assertThrows(ApplicationException.class, () -> exercisesOfSessionsServiceOfAdmin
            .getExercisesHasMusclesOfSessionPagesPrioritizeRelationship(req));

        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
    }

    @Test
    public void getExercisesHasMusclesOfSessionPagesPrioritizeRelationship_admin_invalidFilteringFields() {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("leveling", "INTERMEDIATE");
        List<Object> muscleList = List.of(1, 2, 3);
        var req = PaginatedRelationshipRequest.builder().page(1).filterFields(ftr).build();

        ftr.put("muscleList", muscleList);
        var exc = assertThrows(ApplicationException.class, () -> exercisesOfSessionsServiceOfAdmin
            .getExercisesHasMusclesOfSessionPagesPrioritizeRelationship(req));

        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
    }

//    @Test
//    public void updateExercisesOfSession_admin_valid() {
//        var req = UpdateExercisesOfSessionRequest.builder().sessionId(1L).exerciseIds(List.of(1L, 3L)).build();
//        var session = Session.builder().sessionId(req.getSessionId()).build();
//        var newExercises = req.getExerciseIds().stream().map(id -> Exercise.builder().exerciseId(id).build()).toList();
//        var newRelationships = newExercises.stream().map(e ->
//            ExercisesOfSessions.builder().exercise(e).session(session).build()).toList();
//        Mockito.when(sessionRepository.findById(req.getSessionId())).thenReturn(Optional.of(session));
//        Mockito.when(exerciseRepository.findAllById(req.getExerciseIds())).thenReturn(newExercises);
//        Mockito.doNothing().when(exercisesOfSessionsRepository).deleteAllBySessionSessionId(req.getSessionId());
//        Mockito.when(exercisesOfSessionsRepository.saveAll(newRelationships)).thenReturn(newRelationships);
//
//        List<Exercise> actual = exercisesOfSessionsServiceOfAdmin.updateExercisesOfSession(req);
//
//        assertNotNull(actual);
//        Mockito.verify(sessionRepository, Mockito.times(1)).findById(req.getSessionId());
//        Mockito.verify(exerciseRepository, Mockito.times(1)).findAllById(req.getExerciseIds());
//        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1)).deleteAllBySessionSessionId(req.getSessionId());
//        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1)).saveAll(newRelationships);
//        assertEquals(
//            new HashSet<>(actual),
//            newRelationships.stream().map(ExercisesOfSessions::getExercise).collect(Collectors.toSet())
//        );
//    }
//
//    @Test
//    public void updateExercisesOfSession_admin_invalidSessionId() {
//        var req = UpdateExercisesOfSessionRequest.builder().sessionId(1L).exerciseIds(List.of(1L, 3L)).build();
//        Mockito.when(sessionRepository.findById(req.getSessionId())).thenReturn(Optional.empty());
//
//        var exc = assertThrows(ApplicationException.class, () ->
//            exercisesOfSessionsServiceOfAdmin.updateExercisesOfSession(req));
//
//        Mockito.verify(sessionRepository, Mockito.times(1)).findById(req.getSessionId());
//        assertEquals(ErrorCodes.INVALID_PRIMARY, exc.getErrorCodes());
//    }
//
//    @Test
//    public void updateExercisesOfSession_admin_invalidExerciseIdes() {
//        var req = UpdateExercisesOfSessionRequest.builder().sessionId(1L).exerciseIds(List.of(1L, 3L)).build();
//        var session = Session.builder().sessionId(req.getSessionId()).build();
//        var newExercises = List.of(Exercise.builder().exerciseId(1L).build());
//        Mockito.when(sessionRepository.findById(req.getSessionId())).thenReturn(Optional.of(session));
//        Mockito.when(exerciseRepository.findAllById(req.getExerciseIds())).thenReturn(newExercises);
//
//        var exc = assertThrows(ApplicationException.class, () ->
//            exercisesOfSessionsServiceOfAdmin.updateExercisesOfSession(req));
//
//        Mockito.verify(sessionRepository, Mockito.times(1)).findById(req.getSessionId());
//        Mockito.verify(exerciseRepository, Mockito.times(1)).findAllById(req.getExerciseIds());
//        assertEquals(ErrorCodes.INVALID_IDS_COLLECTION, exc.getErrorCodes());
//    }
}

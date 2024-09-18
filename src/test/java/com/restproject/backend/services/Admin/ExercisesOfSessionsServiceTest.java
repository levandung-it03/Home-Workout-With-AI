package com.restproject.backend.services.Admin;

import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.dtos.request.UpdateExercisesOfSessionRequest;
import com.restproject.backend.dtos.request.PaginatedRelationshipRequest;
import com.restproject.backend.dtos.response.ExerciseHasMusclesResponse;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExercisesOfSessionsServiceTest {
    @Autowired
    ExercisesOfSessionsService exercisesOfSessionsServiceOfAdmin;

    @MockBean
    PageMappers pageMappers;
    @MockBean
    ExercisesOfSessionsRepository exercisesOfSessionsRepository;


    @Test
    public void getExercisesHasMusclesPages_admin_valid() {
        //--Build request data.
        var request = PaginatedRelationshipRequest.builder().id(1L).page(1)
            .filterFields(new HashMap<>(Map.ofEntries(
                Map.entry("name", "Stre"),
                Map.entry("level", 2),
                Map.entry("muscleList", "[0,2]")
            ))).sortedField("name").sortedMode(1)
            .build();
        var muscleList = List.of(Muscle.CHEST, Muscle.TRICEPS);
        var exerciseInfoForFilter = ExercisesOfSessionResponse.builder()
            .name(request.getFilterFields().get("name").toString())
            .level(Level.getByLevel(Integer.parseInt(request.getFilterFields().get("level").toString())))
            .muscleList(muscleList.stream().map(Muscle::toString).toList()).build();
        var pageObject = PageObject.builder().pageNumber(request.getPage()).pageSize(PageEnum.SIZE.getSize()).build();

        //--Build response data.
        var repoResponse = new ArrayList<Object[]>();
        repoResponse.add(    //--exerciseId,name,level::String,muscleList::List<String>,withSession
            new Object[]{10L,"Strength",14,Level.INTERMEDIATE.toString(),
                muscleList.stream().map(Muscle::toString).toList(),true}
        );
        var res = repoResponse.stream().map(ExerciseHasMusclesResponse::buildFromNativeQuery).toList();

        //--Mocking Bean's actions.
        Mockito.when(pageMappers.relationshipPageRequestToPageable(request)).thenReturn(pageObject);
        Mockito
            .when(exercisesOfSessionsRepository.findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
                request.getId(), exerciseInfoForFilter, pageObject.toPageable()))
            .thenReturn(new PageImpl<>(repoResponse, pageObject.toPageable(), PageEnum.SIZE.getSize()));

        TablePagesResponse<ExercisesOfSessionResponse> actual = exercisesOfSessionsServiceOfAdmin
            .getExercisesHasMusclesOfSessionPagesPrioritizeRelationship(request);

        assertNotNull(actual);
        Mockito.verify(pageMappers, Mockito.times(1)).relationshipPageRequestToPageable(request);
        Mockito.verify(exercisesOfSessionsRepository, Mockito.times(1))
            .findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
                request.getId(), exerciseInfoForFilter, pageObject.toPageable());
        assertEquals(
            String.join(",", res.getFirst().getMuscleList()),
            String.join(",", actual.getData().getFirst().getMuscleList())
        );
    }

    @Test
    public void getExercisesHasMusclesPages_admin_invalidSortedField() {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", 2);
        var req = PaginatedRelationshipRequest.builder().page(1).filterFields(ftr).sortedField("unknown").build();
        var exc = assertThrows(ApplicationException.class, () -> exercisesOfSessionsServiceOfAdmin
            .getExercisesHasMusclesOfSessionPagesPrioritizeRelationship(req));

        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_SORTING_FIELD_OR_VALUE);
    }

    @Test
    public void getExercisesHasMusclesPages_admin_invalidFilteringValues() {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", 4);
        List<Object> muscleList = List.of(1, 2, 3);
        var req = PaginatedRelationshipRequest.builder().page(1).filterFields(ftr).build();
        Mockito.when(pageMappers.relationshipPageRequestToPageable(req))
            .thenReturn(PageObject.builder().pageNumber(req.getPage()).pageSize(PageEnum.SIZE.getSize()).build());

        ftr.put("muscleList", muscleList);
        var exc = assertThrows(ApplicationException.class, () -> exercisesOfSessionsServiceOfAdmin
            .getExercisesHasMusclesOfSessionPagesPrioritizeRelationship(req));

        Mockito.verify(pageMappers, Mockito.times(1)).relationshipPageRequestToPageable(req);
        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
    }

    @Test
    public void getExercisesHasMusclesPages_admin_invalidFilteringFields() {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("leveling", "INTERMEDIATE");
        List<Object> muscleList = List.of(1, 2, 3);
        var req = PaginatedRelationshipRequest.builder().page(1).filterFields(ftr).build();
        Mockito.when(pageMappers.relationshipPageRequestToPageable(req))
            .thenReturn(PageObject.builder().pageNumber(req.getPage()).pageSize(PageEnum.SIZE.getSize()).build());

        ftr.put("muscleList", muscleList);
        var exc = assertThrows(ApplicationException.class, () -> exercisesOfSessionsServiceOfAdmin
            .getExercisesHasMusclesOfSessionPagesPrioritizeRelationship(req));

        Mockito.verify(pageMappers, Mockito.times(1)).relationshipPageRequestToPageable(req);
        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
    }
}

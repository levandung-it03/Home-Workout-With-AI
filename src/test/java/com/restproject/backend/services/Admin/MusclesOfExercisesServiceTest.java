package com.restproject.backend.services.Admin;

import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.dtos.response.ExerciseHasMusclesResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.PageObject;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.enums.PageEnum;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.repositories.MusclesOfExercisesRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MusclesOfExercisesServiceTest {
    @Autowired
    MusclesOfExercisesService musclesOfExercisesServiceOfAdmin;

    @MockBean
    MusclesOfExercisesRepository musclesOfExercisesRepository;
    @MockBean
    PageMappers pageMappers;

    @Test
    public void getExercisesHasMusclesPages_admin_valid() {
        //--Build request data.
        var request = PaginatedTableRequest.builder().page(1)
            .filterFields(new HashMap<>(Map.ofEntries(
                Map.entry("name", "Stre"),
                Map.entry("level", 2),
                Map.entry("muscleList", "[0,2]")
            ))).sortedField("name").sortedMode(1)
            .build();
        var muscleList = List.of(Muscle.CHEST, Muscle.TRICEPS);
        var exerciseInfoForFilter = ExerciseHasMusclesResponse.builder()
                .name(request.getFilterFields().get("name").toString())
                .level(Level.getByLevel(Integer.parseInt(request.getFilterFields().get("level").toString())))
            .muscleList(muscleList.stream().map(Muscle::toString).toList()).build();
        var pageObject = PageObject.builder().pageNumber(request.getPage()).pageSize(PageEnum.SIZE.getSize()).build();

        //--Build response data.
        var repoResponse = new ArrayList<Object[]>();
        repoResponse.add(    //--exerciseId,name,level::String,muscleList::List<String>
            new Object[]{10L,"Strength",14,Level.INTERMEDIATE.toString(),muscleList.stream().map(Muscle::toString).toList()}
        );
        var res = repoResponse.stream().map(ExerciseHasMusclesResponse::buildFromNativeQuery).toList();

        //--Mocking Bean's actions.
        Mockito.when(pageMappers.tablePageRequestToPageable(request)).thenReturn(pageObject);
        Mockito
            .when(musclesOfExercisesRepository.findAllExercisesHasMuscles(exerciseInfoForFilter, pageObject.toPageable()))
            .thenReturn(new PageImpl<>(repoResponse, pageObject.toPageable(), PageEnum.SIZE.getSize()));

        TablePagesResponse<ExerciseHasMusclesResponse> actual = musclesOfExercisesServiceOfAdmin
            .getExercisesHasMusclesPages(request);

        assertNotNull(actual);
        Mockito.verify(pageMappers, Mockito.times(1)).tablePageRequestToPageable(request);
        Mockito.verify(musclesOfExercisesRepository, Mockito.times(1))
            .findAllExercisesHasMuscles(exerciseInfoForFilter, pageObject.toPageable());
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
        var req = PaginatedTableRequest.builder().page(1).filterFields(ftr).sortedField("unknown").build();
        var exc = assertThrows(ApplicationException.class, () -> musclesOfExercisesServiceOfAdmin
            .getExercisesHasMusclesPages(req));

        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_SORTING_FIELD_OR_VALUE);
    }

    @Test
    public void getExercisesHasMusclesPages_admin_invalidFilteringValues() {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", 4);
        List<Object> muscleList = List.of(1, 2, 3);
        var req = PaginatedTableRequest.builder().page(1).filterFields(ftr).build();
        Mockito.when(pageMappers.tablePageRequestToPageable(req))
            .thenReturn(PageObject.builder().pageNumber(req.getPage()).pageSize(PageEnum.SIZE.getSize()).build());

        ftr.put("muscleList", muscleList);
        var exc = assertThrows(ApplicationException.class, () -> musclesOfExercisesServiceOfAdmin
            .getExercisesHasMusclesPages(req));

        Mockito.verify(pageMappers, Mockito.times(1)).tablePageRequestToPageable(req);
        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
    }

    @Test
    public void getExercisesHasMusclesPages_admin_invalidFilteringFields() {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("leveling", "INTERMEDIATE");
        List<Object> muscleList = List.of(1, 2, 3);
        var req = PaginatedTableRequest.builder().page(1).filterFields(ftr).build();
        Mockito.when(pageMappers.tablePageRequestToPageable(req))
            .thenReturn(PageObject.builder().pageNumber(req.getPage()).pageSize(PageEnum.SIZE.getSize()).build());

        ftr.put("muscleList", muscleList);
        var exc = assertThrows(ApplicationException.class, () -> musclesOfExercisesServiceOfAdmin
            .getExercisesHasMusclesPages(req));

        Mockito.verify(pageMappers, Mockito.times(1)).tablePageRequestToPageable(req);
        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
    }
}

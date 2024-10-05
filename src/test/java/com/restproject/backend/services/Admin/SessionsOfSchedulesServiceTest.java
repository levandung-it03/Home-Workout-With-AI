package com.restproject.backend.services.Admin;

import com.restproject.backend.dtos.request.UpdateSessionsOfScheduleRequest;
import com.restproject.backend.dtos.request.PaginatedRelationshipRequest;
import com.restproject.backend.dtos.response.SessionsOfScheduleResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Session;
import com.restproject.backend.entities.SessionsOfSchedules;
import com.restproject.backend.entities.PageObject;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.enums.PageEnum;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.mappers.PageMappers;
import com.restproject.backend.repositories.SessionRepository;
import com.restproject.backend.repositories.SessionsOfSchedulesRepository;
import com.restproject.backend.repositories.ScheduleRepository;
import com.restproject.backend.services.SessionsOfSchedulesService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionsOfSchedulesServiceTest {
    @Autowired
    SessionsOfSchedulesService sessionsOfSchedulesServiceOfAdmin;

    @MockBean
    PageMappers pageMappers;
    @MockBean
    SessionsOfSchedulesRepository sessionsOfSchedulesRepository;
    @MockBean
    ScheduleRepository scheduleRepository;
    @MockBean
    SessionRepository sessionRepository;

    @Test
    public void getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship_admin_valid() {
        //--Build request data.
        var request = PaginatedRelationshipRequest.builder().id(1L).page(1)
            .filterFields(new HashMap<>(Map.ofEntries(
                Map.entry("name", "Stre"),
                Map.entry("level", 2),
                Map.entry("muscleList", "[0,2]")
            ))).sortedField("name").sortedMode(1)
            .build();
        var muscleList = List.of(Muscle.CHEST, Muscle.TRICEPS);
        var sessionInfoForFilter = SessionsOfScheduleResponse.builder()
            .name(request.getFilterFields().get("name").toString())
            .levelEnum(Level.getByLevel(Integer.parseInt(request.getFilterFields().get("level").toString())))
            .muscleList(muscleList.stream().map(Muscle::toString).toList()).build();
        var pageObject = PageObject.builder().page(request.getPage()).build();

        //--Build response data.
        var repoResponse = new ArrayList<Object[]>();
        repoResponse.add(    //--sessionId,name,level::String,description,withSchedule,muscleList::List<String>
            new Object[]{10L,"Strength",Level.INTERMEDIATE.toString(),"Description",true,
                muscleList.stream().map(Muscle::toString).toList()}
        );
        var res = repoResponse.stream().map(SessionsOfScheduleResponse::buildFromNativeQuery).toList();

        //--Mocking Bean's actions.
        Mockito.when(pageMappers.relationshipPageRequestToPageable(request)).thenReturn(pageObject);
        Mockito
            .when(sessionsOfSchedulesRepository.findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(
                request.getId(), sessionInfoForFilter, pageObject.toPageable()))
            .thenReturn(new PageImpl<>(repoResponse, pageObject.toPageable(), PageEnum.SIZE.getSize()));

        TablePagesResponse<SessionsOfScheduleResponse> actual = sessionsOfSchedulesServiceOfAdmin
            .getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship(request);

        assertNotNull(actual);
        Mockito.verify(pageMappers, Mockito.times(1)).relationshipPageRequestToPageable(request);
        Mockito.verify(sessionsOfSchedulesRepository, Mockito.times(1))
            .findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(
                request.getId(), sessionInfoForFilter, pageObject.toPageable());
        assertEquals(
            new HashSet<>(res.getFirst().getMuscleList()),
            new HashSet<>(actual.getData().getFirst().getMuscleList())
        );
    }

    @Test
    public void getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship_admin_invalidSortedField() {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", 2);
        var req = PaginatedRelationshipRequest.builder().page(1).filterFields(ftr).sortedField("unknown").build();
        var exc = assertThrows(ApplicationException.class, () -> sessionsOfSchedulesServiceOfAdmin
            .getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship(req));

        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_SORTING_FIELD_OR_VALUE);
    }

    @Test
    public void getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship_admin_invalidFilteringValues() {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", 4);
        List<Object> muscleList = List.of(1, 2, 3);
        var req = PaginatedRelationshipRequest.builder().page(1).filterFields(ftr).build();
        Mockito.when(pageMappers.relationshipPageRequestToPageable(req))
            .thenReturn(PageObject.builder().page(req.getPage()).build());

        ftr.put("muscleList", muscleList);
        var exc = assertThrows(ApplicationException.class, () -> sessionsOfSchedulesServiceOfAdmin
            .getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship(req));

        Mockito.verify(pageMappers, Mockito.times(1)).relationshipPageRequestToPageable(req);
        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
    }

    @Test
    public void getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship_admin_invalidFilteringFields() {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("leveling", "INTERMEDIATE");
        List<Object> muscleList = List.of(1, 2, 3);
        var req = PaginatedRelationshipRequest.builder().page(1).filterFields(ftr).build();
        Mockito.when(pageMappers.relationshipPageRequestToPageable(req))
            .thenReturn(PageObject.builder().page(req.getPage()).build());

        ftr.put("muscleList", muscleList);
        var exc = assertThrows(ApplicationException.class, () -> sessionsOfSchedulesServiceOfAdmin
            .getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship(req));

        Mockito.verify(pageMappers, Mockito.times(1)).relationshipPageRequestToPageable(req);
        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
    }

    @Test
    public void updateSessionsOfSchedule_admin_valid() {
        var req = UpdateSessionsOfScheduleRequest.builder().scheduleId(1L).sessionIds(List.of(1L, 3L)).build();
        var schedule = Schedule.builder().scheduleId(req.getScheduleId()).build();
        var newSessions = req.getSessionIds().stream().map(id -> Session.builder().sessionId(id).build()).toList();
        var newRelationships = newSessions.stream().map(e ->
            SessionsOfSchedules.builder().session(e).schedule(schedule).build()).toList();
        Mockito.when(scheduleRepository.findById(req.getScheduleId())).thenReturn(Optional.of(schedule));
        Mockito.when(sessionRepository.findAllById(req.getSessionIds())).thenReturn(newSessions);
        Mockito.doNothing().when(sessionsOfSchedulesRepository).deleteAllByScheduleScheduleId(req.getScheduleId());
        Mockito.when(sessionsOfSchedulesRepository.saveAll(newRelationships)).thenReturn(newRelationships);

        List<Session> actual = sessionsOfSchedulesServiceOfAdmin.updateSessionsOfSchedule(req);

        assertNotNull(actual);
        Mockito.verify(scheduleRepository, Mockito.times(1)).findById(req.getScheduleId());
        Mockito.verify(sessionRepository, Mockito.times(1)).findAllById(req.getSessionIds());
        Mockito.verify(sessionsOfSchedulesRepository, Mockito.times(1)).deleteAllByScheduleScheduleId(req.getScheduleId());
        Mockito.verify(sessionsOfSchedulesRepository, Mockito.times(1)).saveAll(newRelationships);
        assertEquals(
            new HashSet<>(actual),
            newRelationships.stream().map(SessionsOfSchedules::getSession).collect(Collectors.toSet())
        );
    }

    @Test
    public void updateSessionsOfSchedule_admin_invalidScheduleId() {
        var req = UpdateSessionsOfScheduleRequest.builder().scheduleId(1L).sessionIds(List.of(1L, 3L)).build();
        Mockito.when(scheduleRepository.findById(req.getScheduleId())).thenReturn(Optional.empty());

        var exc = assertThrows(ApplicationException.class, () ->
            sessionsOfSchedulesServiceOfAdmin.updateSessionsOfSchedule(req));

        Mockito.verify(scheduleRepository, Mockito.times(1)).findById(req.getScheduleId());
        assertEquals(ErrorCodes.INVALID_PRIMARY, exc.getErrorCodes());
    }

    @Test
    public void updateSessionsOfSchedule_admin_invalidSessionIdes() {
        var req = UpdateSessionsOfScheduleRequest.builder().scheduleId(1L).sessionIds(List.of(1L, 3L)).build();
        var schedule = Schedule.builder().scheduleId(req.getScheduleId()).build();
        var newSessions = List.of(Session.builder().sessionId(1L).build());
        Mockito.when(scheduleRepository.findById(req.getScheduleId())).thenReturn(Optional.of(schedule));
        Mockito.when(sessionRepository.findAllById(req.getSessionIds())).thenReturn(newSessions);

        var exc = assertThrows(ApplicationException.class, () ->
            sessionsOfSchedulesServiceOfAdmin.updateSessionsOfSchedule(req));

        Mockito.verify(scheduleRepository, Mockito.times(1)).findById(req.getScheduleId());
        Mockito.verify(sessionRepository, Mockito.times(1)).findAllById(req.getSessionIds());
        assertEquals(ErrorCodes.INVALID_IDS_COLLECTION, exc.getErrorCodes());
    }
}

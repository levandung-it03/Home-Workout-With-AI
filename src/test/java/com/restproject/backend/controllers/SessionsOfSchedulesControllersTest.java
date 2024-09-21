package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.PaginatedRelationshipRequest;
import com.restproject.backend.dtos.request.UpdateSessionsOfScheduleRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.response.SessionsOfScheduleResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.helpers.JsonService;
import com.restproject.backend.helpers.MockAuthRequestBuilders;
import com.restproject.backend.helpers.MockAuthentication;
import com.restproject.backend.services.Admin.SessionsOfSchedulesService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionsOfSchedulesControllersTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MockAuthentication mockAuthentication;
    @Autowired
    MockAuthRequestBuilders mockAuthRequestBuilders;
    @Autowired
    JsonService jsonService;

    @MockBean
    SessionsOfSchedulesService sessionsOfSchedulesService;

    @BeforeEach
    public void init() {
        mockAuthRequestBuilders.setJwtTokens(mockAuthentication.generateJwtTokens());
    }


    PaginatedRelationshipRequest paginatedSchedulesOfScheduleRequest() {
        return PaginatedRelationshipRequest.builder().id(1L).page(1).build();
    }

    @Test
    public void getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship_admin_valid() throws Exception {
        var req = this.paginatedSchedulesOfScheduleRequest();
        var res = TablePagesResponse.<SessionsOfScheduleResponse>builder()
            .data(List.of(
                SessionsOfScheduleResponse.builder().sessionId(2L)
                    .muscleList(List.of(Muscle.CHEST.toString())).withCurrentSchedule(true).build()
            ))
            .currentPage(req.getPage())
            .totalPages(10).build();
        Mockito.when(sessionsOfSchedulesService.getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship(req))
            .thenReturn(res);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-sessions-has-muscles-of-schedule-pages"))
            .andExpect(result -> {
                assertNotNull(result);
                ApiResponseObject<TablePagesResponse> apiRes = jsonService
                    .parseResponseJson(result.getResponse().getContentAsString(), TablePagesResponse.class);
                TablePagesResponse response = apiRes.getData();

                assertEquals(response.getData().size(), res.getData().size());
                assertEquals(SucceedCodes.GET_SESSIONS_HAS_MUSCLES_OF_SCHEDULE_PAGES.getCode(), apiRes.getApplicationCode());
                assertEquals(Integer.parseInt(((LinkedHashMap) response.getData().get(0)).get("sessionId").toString()),
                    res.getData().getFirst().getSessionId());
            });
    }

    @Test
    public void getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship_admin_unauthorized() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders
                .request(GET, "/api/private/admin/v1/get-sessions-has-muscles-of-schedule-pages"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship_admin_nullPage() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.paginatedSchedulesOfScheduleRequest())
                .replaceFieldOfContent("page", null)
                .buildAdminRequestWithContent(GET, "/v1/get-sessions-has-muscles-of-schedule-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship_admin_invalidTypePage() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.paginatedSchedulesOfScheduleRequest())
                .replaceFieldOfContent("page", "abc")
                .buildAdminRequestWithContent(GET, "/v1/get-sessions-has-muscles-of-schedule-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship_admin_tooSmallPage() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.paginatedSchedulesOfScheduleRequest())
                .replaceFieldOfContent("page", 0)
                .buildAdminRequestWithContent(GET, "/v1/get-sessions-has-muscles-of-schedule-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship_admin_nullScheduleId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.paginatedSchedulesOfScheduleRequest())
                .replaceFieldOfContent("id", null)
                .buildAdminRequestWithContent(GET, "/v1/get-sessions-has-muscles-of-schedule-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getSessionsHasMusclesOfSchedulePagesPrioritizeRelationship_admin_invalidTypeScheduleId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.paginatedSchedulesOfScheduleRequest())
                .replaceFieldOfContent("scheduleId", "abc")
                .buildAdminRequestWithContent(GET, "/v1/get-sessions-has-muscles-of-schedule-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    UpdateSessionsOfScheduleRequest updateSessionsOfScheduleRequest() {
        return UpdateSessionsOfScheduleRequest.builder().scheduleId(1L).sessionIds(List.of(1L, 3L)).build();
    }

    @Test
    public void updateSchedulesOfSchedule_admin_valid() throws Exception {
        var req = this.updateSessionsOfScheduleRequest();
        Mockito.when(sessionsOfSchedulesService.updateSessionsOfSchedule(req))
            .thenReturn(List.of(Session.builder().sessionId(1L).build(), Session.builder().sessionId(3L).build()));
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(PUT, "/v1/update-sessions-of-schedule"))
            .andExpect(result -> {
                var response = jsonService.parseResJsonByDataList(result.getResponse().getContentAsString(),
                    Session.class);

                assertEquals(
                    response.getData().stream().map(Session::getSessionId).sorted().toList(),
                    req.getSessionIds().stream().sorted().toList());
            });
    }

    @Test
    public void updateSchedulesOfSchedule_admin_unauthorized() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders
                .request(PUT, "/api/private/admin/v1/update-sessions-of-schedule"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateSchedulesOfSchedule_admin_nullScheduleId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateSessionsOfScheduleRequest())
                .replaceFieldOfContent("scheduleId", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-sessions-of-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSchedulesOfSchedule_admin_invalidTypeScheduleId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateSessionsOfScheduleRequest())
                .replaceFieldOfContent("scheduleId", "abc")
                .buildAdminRequestWithContent(PUT, "/v1/update-sessions-of-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void updateSchedulesOfSchedule_admin_emptyScheduleIds() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateSessionsOfScheduleRequest())
                .replaceFieldOfContent("sessionIds", List.of())
                .buildAdminRequestWithContent(PUT, "/v1/update-sessions-of-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSchedulesOfSchedule_admin_nullScheduleIds() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateSessionsOfScheduleRequest())
                .replaceFieldOfContent("sessionIds", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-sessions-of-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSchedulesOfSchedule_admin_invalidTypeScheduleIds() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateSessionsOfScheduleRequest())
                .replaceFieldOfContent("sessionIds", List.of("a", "b", "c"))
                .buildAdminRequestWithContent(PUT, "/v1/update-sessions-of-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void updateSchedulesOfSchedule_admin_violateListTypeConstraintScheduleIds() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateSessionsOfScheduleRequest())
                .replaceFieldOfContent("sessionIds", Arrays.stream(new Integer[] {null, null}).toList())
                .buildAdminRequestWithContent(PUT, "/v1/update-sessions-of-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }
}

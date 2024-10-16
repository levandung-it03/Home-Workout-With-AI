package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.UpdateExercisesOfSessionRequest;
import com.restproject.backend.dtos.request.PaginatedRelationshipRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.response.ExercisesOfSessionResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.helpers.JsonService;
import com.restproject.backend.helpers.MockAuthRequestBuilders;
import com.restproject.backend.helpers.MockAuthentication;
import com.restproject.backend.services.ExercisesOfSessionsService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.springframework.http.HttpMethod.*;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExercisesOfSessionsControllersTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MockAuthentication mockAuthentication;
    @Autowired
    MockAuthRequestBuilders mockAuthRequestBuilders;
    @Autowired
    JsonService jsonService;

    @MockBean
    ExercisesOfSessionsService exercisesOfSessionsService;

    @BeforeEach
    public void init() {
        mockAuthRequestBuilders.setJwtTokens(mockAuthentication.generateJwtTokens());
    }

    PaginatedRelationshipRequest paginatedExercisesOfSessionRequest() {
        return PaginatedRelationshipRequest.builder().id(1L).page(1).build();
    }

    @Test
    public void getExercisesHasMusclesOfSessionPagesPrioritizeRelationship_admin_valid() throws Exception {
        var req = this.paginatedExercisesOfSessionRequest();
        var res = TablePagesResponse.<ExercisesOfSessionResponse>builder()
            .data(List.of(
                ExercisesOfSessionResponse.builder().exerciseId(2L).withCurrentSession(true).build()
            ))
            .currentPage(req.getPage())
            .totalPages(10).build();
        Mockito.when(exercisesOfSessionsService.getExercisesHasMusclesOfSessionPagesPrioritizeRelationship(req))
            .thenReturn(res);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-exercises-has-muscles-of-session-pages"))
            .andExpect(result -> {
                assertNotNull(result);
                ApiResponseObject<TablePagesResponse> apiRes = jsonService
                    .parseResponseJson(result.getResponse().getContentAsString(), TablePagesResponse.class);
                TablePagesResponse response = apiRes.getData();

                assertEquals(response.getData().size(), res.getData().size());
                assertEquals(SucceedCodes.GET_EXERCISES_HAS_MUSCLES_OF_SESSION_PAGES.getCode(), apiRes.getApplicationCode());
                assertEquals(Integer.parseInt(((LinkedHashMap) response.getData().get(0)).get("exerciseId").toString()),
                    res.getData().getFirst().getExerciseId());
            });
    }

    @Test
    public void getExercisesHasMusclesOfSessionPagesPrioritizeRelationship_admin_unauthorized() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders
                .request(GET, "/api/private/admin/v1/get-exercises-has-muscles-of-session-pages"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getExercisesHasMusclesOfSessionPagesPrioritizeRelationship_admin_nullPage() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.paginatedExercisesOfSessionRequest())
                .replaceFieldOfContent("page", null)
                .buildAdminRequestWithContent(GET, "/v1/get-exercises-has-muscles-of-session-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getExercisesHasMusclesOfSessionPagesPrioritizeRelationship_admin_invalidTypePage() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.paginatedExercisesOfSessionRequest())
                .replaceFieldOfContent("page", "abc")
                .buildAdminRequestWithContent(GET, "/v1/get-exercises-has-muscles-of-session-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void getExercisesHasMusclesOfSessionPagesPrioritizeRelationship_admin_tooSmallPage() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.paginatedExercisesOfSessionRequest())
                .replaceFieldOfContent("page", 0)
                .buildAdminRequestWithContent(GET, "/v1/get-exercises-has-muscles-of-session-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getExercisesHasMusclesOfSessionPagesPrioritizeRelationship_admin_nullSessionId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.paginatedExercisesOfSessionRequest())
                .replaceFieldOfContent("id", null)
                .buildAdminRequestWithContent(GET, "/v1/get-exercises-has-muscles-of-session-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getExercisesHasMusclesOfSessionPagesPrioritizeRelationship_admin_invalidTypeSessionId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.paginatedExercisesOfSessionRequest())
                .replaceFieldOfContent("sessionId", "abc")
                .buildAdminRequestWithContent(GET, "/v1/get-exercises-has-muscles-of-session-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    UpdateExercisesOfSessionRequest updateExercisesOfSessionRequest() {
        return UpdateExercisesOfSessionRequest.builder().sessionId(1L).build();
    }

    @Test
    public void updateExercisesOfSession_admin_valid() throws Exception {
        var req = this.updateExercisesOfSessionRequest();
        Mockito.when(exercisesOfSessionsService.updateExercisesOfSession(req))
            .thenReturn(List.of(Exercise.builder().exerciseId(1L).build(), Exercise.builder().exerciseId(3L).build()));
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(PUT, "/v1/update-exercises-of-session"))
            .andExpect(result -> {
                var response = jsonService.parseResJsonByDataList(result.getResponse().getContentAsString(),
                    Exercise.class);
            });
    }

    @Test
    public void updateExercisesOfSession_admin_unauthorized() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders
                .request(PUT, "/api/private/admin/v1/update-exercises-of-session"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateExercisesOfSession_admin_nullSessionId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateExercisesOfSessionRequest())
                .replaceFieldOfContent("sessionId", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-exercises-of-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateExercisesOfSession_admin_invalidTypeSessionId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateExercisesOfSessionRequest())
                .replaceFieldOfContent("sessionId", "abc")
                .buildAdminRequestWithContent(PUT, "/v1/update-exercises-of-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }
}

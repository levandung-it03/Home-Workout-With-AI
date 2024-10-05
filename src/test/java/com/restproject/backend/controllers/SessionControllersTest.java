package com.restproject.backend.controllers;

import com.restproject.backend.annotations.dev.CoreEngines;
import com.restproject.backend.dtos.request.DeleteObjectRequest;
import com.restproject.backend.dtos.request.NewSessionRequest;
import com.restproject.backend.dtos.request.UpdateSessionRequest;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.helpers.JsonService;
import com.restproject.backend.helpers.MockAuthRequestBuilders;
import com.restproject.backend.helpers.MockAuthentication;
import com.restproject.backend.services.SessionService;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionControllersTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MockAuthentication mockAuthentication;
    @Autowired
    MockAuthRequestBuilders mockAuthRequestBuilders;
    @Autowired
    JsonService jsonService;

    @MockBean
    SessionService sessionServiceOfAdmin;

    @BeforeEach
    public void init() {
        mockAuthRequestBuilders.setJwtTokens(mockAuthentication.generateJwtTokens());
    }

    @CoreEngines
    private NewSessionRequest newValidSession() {
        return NewSessionRequest.builder().name("Shoulders exercises for beginner").level(Level.INTERMEDIATE.getLevel())
            .muscleIds(List.of(Muscle.ABS.getId(), Muscle.TRICEPS.getId())).description("Just shoulders in about 1 hour")
            .exerciseIds(List.of(1L, 3L)).build();
    }

    @CoreEngines
    private Session session() {
        return Session.builder().name("Shoulders exercises for beginner").levelEnum(Level.INTERMEDIATE)
            .description("Just shoulders in about 1 hour")
            .build();
    }

    @Test
    public void createSession_admin_valid() throws Exception {
        var request = this.newValidSession();
        var response = this.session();
        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(response);

        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.CREATE_SESSION.getCode()));
    }

    @Test
    public void createSession_admin_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(POST, "/api/private/admin/v1/create-session"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void createSession_admin_emptyName() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("name", "")
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_nullName() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("name", null)
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_tooLongName() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("name", "1234567891234567891234567894123412431543513452345234523452345243523452435243")
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_emptyDescription() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("description", "")
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_nullDescription() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("description", null)
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_tooLongDescription() throws Exception {
        var request = this.newValidSession();
        for (var i = 1; i <= 110; i++) request.setName(request.getName() + "a");
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_nullLevel() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("level", null)
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_invalidTypeLevel() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("level", "abcdef")
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void createSession_admin_outOfLevelRightRange() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("level", 5)
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_outOfLevelLeftRange() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("level", -2)
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_emptyMuscleIds() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("muscleIds", List.of())
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_nullMuscleIds() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("muscleIds", null)
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_nullListAsMuscleIds() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("muscleIds", Arrays.stream(new Integer[]{null, null}).toList())
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_outOfMuscleIdsRange() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("muscleIds", List.of(1, 99))
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_invalidTypeMuscleIds() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("muscleIds", List.of("a", "b", "c"))
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void createSession_admin_emptyExerciseIds() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("exerciseIds", List.of())
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_nullExerciseIds() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("exerciseIds", null)
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_nullListAsExerciseIds() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("exerciseIds", Arrays.stream(new Long[]{null, null}).toList())
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_invalidTypeExerciseIds() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newValidSession())
                .replaceFieldOfContent("exerciseIds", List.of("a", "b", "c"))
                .buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    UpdateSessionRequest updateSessionAndMusclesRequest() {
        return UpdateSessionRequest.builder().sessionId(2L).level(2).name("Push-ups")
            .description("Hello").muscleIds(List.of(0, 2)).build();
    }

    @Test
    public void updateSessionAndMuscles_admin_valid() throws Exception {
        var req = updateSessionAndMusclesRequest();
        var res = Session.builder().sessionId(req.getSessionId()).levelEnum(Level.getByLevel(req.getLevel()))
            .description(req.getDescription()).name(req.getName()).build();

        Mockito.when(sessionServiceOfAdmin.updateSessionAndMuscles(req)).thenReturn(res);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Session.class);
                assertEquals(SucceedCodes.UPDATE_SESSION.getCode(), apiRes.getApplicationCode());
                assertEquals(res, apiRes.getData());
            });
    }

    @Test
    public void updateSessionAndMuscles_admin_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(PUT, "/api/private/admin/v1/update-session-and-muscles"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateSessionAndMuscles_admin_nullSessionId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSessionAndMusclesRequest())
                .replaceFieldOfContent("sessionId", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSessionAndMuscles_admin_invalidTypeSessionId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSessionAndMusclesRequest())
                .replaceFieldOfContent("sessionId", "abc")
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void updateSessionAndMuscles_admin_emptyName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSessionAndMusclesRequest())
                .replaceFieldOfContent("name", "")
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSessionAndMuscles_admin_nullName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSessionAndMusclesRequest())
                .replaceFieldOfContent("name", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSessionAndMuscles_admin_tooLongName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSessionAndMusclesRequest())
                .replaceFieldOfContent("name", "1234567891234567891234567891232352346345746867524435252435")
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSessionAndMuscles_admin_emptyDescription() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.updateSessionAndMusclesRequest())
                .replaceFieldOfContent("description", "")
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSessionAndMuscles_admin_nullDescription() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.updateSessionAndMusclesRequest())
                .replaceFieldOfContent("description", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSessionAndMuscles_admin_tooLongDescription() throws Exception {
        var request = this.updateSessionAndMusclesRequest();
        for (var i = 1; i <= 110; i++) request.setName(request.getName() + "a");
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSessionAndMuscles_admin_nullLevel() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSessionAndMusclesRequest())
                .replaceFieldOfContent("level", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSessionAndMuscles_admin_invalidTypeLevel() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSessionAndMusclesRequest())
                .replaceFieldOfContent("level", "abc")
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void updateSessionAndMuscles_admin_outOfLevelsRightRange() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSessionAndMusclesRequest())
                .replaceFieldOfContent("level", 99)
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSessionAndMuscles_admin_outOfLevelsLeftRange() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSessionAndMusclesRequest())
                .replaceFieldOfContent("level", 0)
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSessionAndMuscles_admin_emptyMuscleIds() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSessionAndMusclesRequest())
                .replaceFieldOfContent("muscleIds", List.of())
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSessionAndMuscles_admin_nullMuscleIds() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSessionAndMusclesRequest())
                .replaceFieldOfContent("muscleIds", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSessionAndMuscles_admin_nullListAsMuscleIds() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSessionAndMusclesRequest())
                .replaceFieldOfContent("muscleIds", Arrays.stream(new Integer[]{null, null}).toList())
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSessionAndMuscles_admin_outOfMuscleIdsRange() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSessionAndMusclesRequest())
                .replaceFieldOfContent("muscleIds", List.of(1, 99))
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateSessionAndMuscles_admin_invalidTypeMuscleIds() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSessionAndMusclesRequest())
                .replaceFieldOfContent("muscleIds", List.of("a", "b", "c"))
                .buildAdminRequestWithContent(PUT, "/v1/update-session-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void deleteSession_admin_valid() throws Exception {
        var req = DeleteObjectRequest.builder().id(2L).build();
        Mockito.doNothing().when(sessionServiceOfAdmin).deleteSession(req);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(DELETE, "/v1/delete-session"))
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.DELETE_SESSION.getCode()));
    }

    @Test
    public void deleteSession_admin_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(POST, "/api/private/admin/v1/delete-session"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteSession_admin_invalidTypeSessionId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(DeleteObjectRequest.builder().id(2L).build())
                .replaceFieldOfContent("sessionId", "abc")
                .buildAdminRequestWithContent(DELETE, "/v1/delete-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void deleteSession_admin_nullSessionId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(DeleteObjectRequest.builder().id(null).build())
                .buildAdminRequestWithContent(DELETE, "/v1/delete-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }
}

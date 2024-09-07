package com.restproject.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restproject.backend.annotations.dev.CoreEngines;
import com.restproject.backend.dtos.request.FilteringPageRequest;
import com.restproject.backend.dtos.request.NewSessionRequest;
import com.restproject.backend.dtos.request.PaginatedObjectRequest;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.helpers.JsonService;
import com.restproject.backend.helpers.MockAuthRequestBuilders;
import com.restproject.backend.helpers.MockAuthentication;
import com.restproject.backend.services.Admin.SessionService;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionControllersTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MockAuthentication mockAuthentication;
    @Autowired
    MockAuthRequestBuilders mockAuthRequest;
    @Autowired
    JsonService jsonService;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    SessionService sessionServiceOfAdmin;
    
    @BeforeEach
    public void init() {
        mockAuthRequest.setJwtTokens(mockAuthentication.generateJwtTokens());
    }

    @Test
    public void getPaginatedSessions_admin_valid() throws Exception {
        var request = PaginatedObjectRequest.builder().page(3).build();

        Mockito.when(sessionServiceOfAdmin.getPaginatedSessions(request)).thenReturn(Mockito.anyList());

        mockAuthRequest.setContent(request);
        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-sessions-by-page"))
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.GET_PAGINATED_EXERCISES.getCode()));
    }

    @Test
    public void getPaginatedSessions_admin_nullPage() throws Exception {
        var request = PaginatedObjectRequest.builder().page(null).build();

        Mockito.when(sessionServiceOfAdmin.getPaginatedSessions(request)).thenReturn(null);

        mockAuthRequest.setContent(request);
        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-sessions-by-page"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getPaginatedSessions_admin_invalidTypePage() throws Exception {
        var request = PaginatedObjectRequest.builder().page(3).build();

        Mockito.when(sessionServiceOfAdmin.getPaginatedSessions(request)).thenReturn(null);

        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("page", "abc");
        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-sessions-by-page"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void getPaginatedSessions_admin_tooSmallPage() throws Exception {
        var request = PaginatedObjectRequest.builder().page(0).build();

        Mockito.when(sessionServiceOfAdmin.getPaginatedSessions(request)).thenReturn(null);

        mockAuthRequest.setContent(request);
        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-sessions-by-page"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getPaginatedFilteringListOfSessions_admin_valid() throws Exception {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", "INTERMEDIATE");
        var req = FilteringPageRequest.builder().page(1).filterFields(ftr).build();
        var res = List.of(Session.builder().build(), Session.builder().build(), Session.builder().build());

        Mockito.when(sessionServiceOfAdmin.getPaginatedFilteringListOfSessions(req)).thenReturn(res);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-sessions-by-page-and-filtering-fields"))
            .andExpect(response -> {
                assertNotNull(response);

                var apiRes = jsonService
                    .parseResJsonByDataList(response.getResponse().getContentAsString(), Session.class);

                assertNotNull(apiRes);
                assertEquals(apiRes.getApplicationCode(), SucceedCodes.GET_PAGINATED_FILTERING_SESSIONS.getCode());
                assertEquals(apiRes.getData().size(), res.size());
            });
    }

    @Test
    public void getPaginatedFilteringListOfSessions_admin_nullPage() throws Exception {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", "INTERMEDIATE");
        var req = FilteringPageRequest.builder().page(null).filterFields(ftr).build();

        Mockito.when(sessionServiceOfAdmin.getPaginatedFilteringListOfSessions(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-sessions-by-page-and-filtering-fields"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @CoreEngines
    private NewSessionRequest newValidSession() {
        return NewSessionRequest.builder().name("Shoulders exercises for beginner").level(Level.INTERMEDIATE.getLevel())
            .muscleIds(List.of(Muscle.ABS.getId(), Muscle.TRICEPS.getId())).description("Just shoulders in about 1 hour")
            .exerciseIds(List.of(1L, 3L)).build();
    }

    @CoreEngines
    private Session session() {
        return Session.builder().name("Shoulders exercises for beginner").level(Level.INTERMEDIATE)
            .muscleList(Muscle.listToString(List.of(Muscle.ABS, Muscle.TRICEPS)))
            .description("Just shoulders in about 1 hour")
            .build();
    }

    @Test
    public void createSession_admin_valid() throws Exception {
        var request = this.newValidSession();
        var response = this.session();
        mockAuthRequest.setContent(request);

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(response);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.CREATE_SESSION.getCode()));
    }

    @Test
    public void createSession_admin_emptyName() throws Exception {
        var request = this.newValidSession();
        request.setName("");
        mockAuthRequest.setContent(request);

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST,"/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_nullName() throws Exception {
        var request = this.newValidSession();
        request.setName(null);
        mockAuthRequest.setContent(request);

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST,"/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_tooLongName() throws Exception {
        var request = this.newValidSession();
        request.setName("1234567891234567891234567894123412431543513452345234523452345243523452435243");
        mockAuthRequest.setContent(request);

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST,"/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_emptyDescription() throws Exception {
        var request = this.newValidSession();
        request.setDescription("");
        mockAuthRequest.setContent(request);

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST,"/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_nullDescription() throws Exception {
        var request = this.newValidSession();
        request.setDescription(null);
        mockAuthRequest.setContent(request);

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST,"/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_tooLongDescription() throws Exception {
        var request = this.newValidSession();
        for (var i=1; i<=110; i++)   request.setName(request.getName() + "a");
        mockAuthRequest.setContent(request);

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST,"/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_nullLevel() throws Exception {
        var request = this.newValidSession();
        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("level", "");

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_invalidTypeLevel() throws Exception {
        var request = this.newValidSession();
        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("level", "abcdef");

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void createSession_admin_outOfLevelRightRange() throws Exception {
        var request = this.newValidSession();
        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("level", 5);

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_outOfLevelLeftRange() throws Exception {
        var request = this.newValidSession();
        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("level", -2);

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_emptyMuscleIds() throws Exception {
        var request = this.newValidSession();
        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("muscleIds", List.of());

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_nullMuscleIds() throws Exception {
        var request = this.newValidSession();
        request.setMuscleIds(null);
        mockAuthRequest.setContent(request);

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_nullListAsMuscleIds() throws Exception {
        var request = this.newValidSession();
        List<Integer> muscleIds = Arrays.stream(new Integer[] {null, null}).toList();
        request.setMuscleIds(muscleIds);
        mockAuthRequest.setContent(request);

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_outOfMuscleIdsRange() throws Exception {
        var request = this.newValidSession();
        request.setMuscleIds(List.of(1, 99));
        mockAuthRequest.setContent(request);

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_invalidTypeMuscleIds() throws Exception {
        var request = this.newValidSession();
        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("muscleIds", List.of("a", "b", "c"));

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void createSession_admin_emptyExerciseIds() throws Exception {
        var request = this.newValidSession();
        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("exerciseIds", List.of());

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_nullExerciseIds() throws Exception {
        var request = this.newValidSession();
        request.setExerciseIds(null);
        mockAuthRequest.setContent(request);

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_nullListAsExerciseIds() throws Exception {
        var request = this.newValidSession();
        List<Long> exerciseIds = Arrays.stream(new Long[] {null, null}).toList();
        request.setExerciseIds(exerciseIds);
        mockAuthRequest.setContent(request);

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSession_admin_invalidTypeExerciseIds() throws Exception {
        var request = this.newValidSession();
        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("exerciseIds", List.of("a", "b", "c"));

        Mockito.when(sessionServiceOfAdmin.createSession(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-session"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

}

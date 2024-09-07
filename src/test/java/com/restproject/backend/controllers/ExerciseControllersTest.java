package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.*;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.helpers.JsonService;
import com.restproject.backend.helpers.MockAuthRequestBuilders;
import com.restproject.backend.helpers.MockAuthentication;
import com.restproject.backend.services.Admin.ExerciseService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.*;

import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseControllersTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MockAuthentication mockAuthentication;
    @Autowired
    MockAuthRequestBuilders mockAuthRequest;
    @Autowired
    JsonService jsonService;

    @MockBean
    ExerciseService exerciseServiceOfAdmin;

    @BeforeEach
    public void init() {
        mockAuthRequest.setJwtTokens(mockAuthentication.generateJwtTokens());
    }

    NewExerciseRequest newValidExercise() {
        return NewExerciseRequest.builder().name("Push-ups").level(1).basicReps(14)
            .muscleIds(List.of(0, 1)).build();
    }

    @Test
    public void createExercise_admin_valid() throws Exception {
        var request = this.newValidExercise();
        mockAuthRequest.setContent(request);

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.CREATE_EXERCISE.getCode()));
    }

    @Test
    public void createExercise_admin_emptyName() throws Exception {
        var request = this.newValidExercise();
        request.setName("");
        mockAuthRequest.setContent(request);

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST,"/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_nullName() throws Exception {
        var request = this.newValidExercise();
        request.setName(null);
        mockAuthRequest.setContent(request);

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST,"/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_tooLongName() throws Exception {
        var request = this.newValidExercise();
        request.setName("12345678912345678912345678912345678952345235");
        mockAuthRequest.setContent(request);

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST,"/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_nullLevel() throws Exception {
        var request = this.newValidExercise();
        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("level", "");

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_invalidTypeLevel() throws Exception {
        var request = this.newValidExercise();
        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("level", "abcdef");

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void createExercise_admin_outOfLevelRightRange() throws Exception {
        var request = this.newValidExercise();
        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("level", 5);

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_outOfLevelLeftRange() throws Exception {
        var request = this.newValidExercise();
        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("level", -2);

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_nullBasicReps() throws Exception {
        var request = this.newValidExercise();
        request.setBasicReps(null);
        mockAuthRequest.setContent(request);

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_invalidTypeBasicReps() throws Exception {
        var request = this.newValidExercise();
        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("basicReps", "abcd");

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void createExercise_admin_negativeBasicReps() throws Exception {
        var request = this.newValidExercise();
        request.setBasicReps(-1);
        mockAuthRequest.setContent(request);

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_outOfBasicRepsRange() throws Exception {
        var request = this.newValidExercise();
        request.setBasicReps(10_000);
        mockAuthRequest.setContent(request);

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_emptyMuscleIds() throws Exception {
        var request = this.newValidExercise();
        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("muscleIds", List.of());

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_nullMuscleIds() throws Exception {
        var request = this.newValidExercise();
        request.setMuscleIds(null);
        mockAuthRequest.setContent(request);

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_nullListAsMuscleIds() throws Exception {
        var request = this.newValidExercise();
        List<Integer> muscleIds = Arrays.stream(new Integer[] {null, null}).toList();
        request.setMuscleIds(muscleIds);
        mockAuthRequest.setContent(request);

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_outOfMuscleIdsRange() throws Exception {
        var request = this.newValidExercise();
        request.setMuscleIds(List.of(1, 99));
        mockAuthRequest.setContent(request);

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_invalidTypeMuscleIds() throws Exception {
        var request = this.newValidExercise();
        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("muscleIds", List.of("a", "b", "c"));

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    ExercisesByLevelAndMusclesRequest exercisesByLevelAndMusclesRequest() {
        return ExercisesByLevelAndMusclesRequest.builder().level(1).muscleIds(List.of(0, 2)).build();
    }
    List<Exercise> exercisesByLevelAndMusclesResponse() {
        return List.of(
            Exercise.builder().exerciseId(0L).level(Level.INTERMEDIATE).build(),
            Exercise.builder().exerciseId(2L).level(Level.INTERMEDIATE).build(),
            Exercise.builder().exerciseId(8L).level(Level.INTERMEDIATE).build()
        );
    }

    @Test
    public void getExercisesByLevelAndMuscles_admin_valid() throws Exception {
        var request = this.exercisesByLevelAndMusclesRequest();
        var validRes = this.exercisesByLevelAndMusclesResponse();
        Mockito.when(exerciseServiceOfAdmin.getExercisesByLevelAndMuscles(request)).thenReturn(validRes);

        mockAuthRequest.setContent(request);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-exercises-by-level-and-muscles"))
            .andExpect(result -> {
                var res = jsonService.parseResJsonByDataList(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(res.getApplicationCode(), SucceedCodes.GET_EXS_BY_LV_AND_MUSCLE.getCode());
                assertEquals(res.getData(), validRes);
            });
    }

    @Test
    public void getExercisesByLevelAndMuscles_admin_nullLevel() throws Exception {
        var request = this.exercisesByLevelAndMusclesRequest();
        request.setLevel(null);
        Mockito.when(exerciseServiceOfAdmin.getExercisesByLevelAndMuscles(request)).thenReturn(null);

        mockAuthRequest.setContent(request);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-exercises-by-level-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getExercisesByLevelAndMuscles_admin_invalidTypeLevel() throws Exception {
        var request = this.exercisesByLevelAndMusclesRequest();
        Mockito.when(exerciseServiceOfAdmin.getExercisesByLevelAndMuscles(request)).thenReturn(null);

        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("level", "abc");
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-exercises-by-level-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void getExercisesByLevelAndMuscles_admin_outOfLevelRightRange() throws Exception {
        var request = this.exercisesByLevelAndMusclesRequest();
        request.setLevel(5);
        Mockito.when(exerciseServiceOfAdmin.getExercisesByLevelAndMuscles(request)).thenReturn(null);

        mockAuthRequest.setContent(request);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-exercises-by-level-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getExercisesByLevelAndMuscles_admin_outOfLevelLeftRange() throws Exception {
        var request = this.exercisesByLevelAndMusclesRequest();
        request.setLevel(-1);
        Mockito.when(exerciseServiceOfAdmin.getExercisesByLevelAndMuscles(request)).thenReturn(null);

        mockAuthRequest.setContent(request);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-exercises-by-level-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getExercisesByLevelAndMuscles_admin_nullMuscleIds() throws Exception {
        var request = this.exercisesByLevelAndMusclesRequest();
        request.setMuscleIds(null);
        Mockito.when(exerciseServiceOfAdmin.getExercisesByLevelAndMuscles(request)).thenReturn(null);

        mockAuthRequest.setContent(request);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-exercises-by-level-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getExercisesByLevelAndMuscles_admin_emptyMuscleIds() throws Exception {
        var request = this.exercisesByLevelAndMusclesRequest();
        request.setMuscleIds(List.of());
        Mockito.when(exerciseServiceOfAdmin.getExercisesByLevelAndMuscles(request)).thenReturn(null);

        mockAuthRequest.setContent(request);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-exercises-by-level-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getExercisesByLevelAndMuscles_admin_invalidTypeMuscleIds() throws Exception {
        var request = this.exercisesByLevelAndMusclesRequest();
        Mockito.when(exerciseServiceOfAdmin.getExercisesByLevelAndMuscles(request)).thenReturn(null);

        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("muscleIds", List.of("a", "b", "c"));
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-exercises-by-level-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void getExercisesByLevelAndMuscles_admin_nullListAsMuscleIds() throws Exception {
        var request = this.exercisesByLevelAndMusclesRequest();
        List<Integer> muscleIds = Arrays.stream(new Integer[] {null, null}).toList();
        request.setMuscleIds(muscleIds);
        Mockito.when(exerciseServiceOfAdmin.getExercisesByLevelAndMuscles(request)).thenReturn(null);

        mockAuthRequest.setContent(request);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-exercises-by-level-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getExercisesByLevelAndMuscles_admin_outOfMuscleIdsRange() throws Exception {
        var request = this.exercisesByLevelAndMusclesRequest();
        request.setMuscleIds(List.of(1, 99));
        Mockito.when(exerciseServiceOfAdmin.getExercisesByLevelAndMuscles(request)).thenReturn(null);

        mockAuthRequest.setContent(request);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-exercises-by-level-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    UpdateExerciseRequest updateExerciseRequest() {
        return UpdateExerciseRequest.builder().exerciseId(2L).level(2).name("Push-ups")
            .basicReps(14).muscleIds(List.of(0, 2)).build();
    }

    @Test
    public void updateExercise_admin_valid() throws Exception {
        var req = updateExerciseRequest();
        var res = Exercise.builder().exerciseId(req.getExerciseId()).level(Level.getByLevel(req.getLevel()))
            .basicReps(req.getBasicReps()).name(req.getName()).build();

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(res);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), SucceedCodes.UPDATE_EXERCISE.getCode());
                assertEquals(apiRes.getData(), res);
            });
    }

    @Test
    public void updateExercise_admin_nullExerciseId() throws Exception {
        var req = updateExerciseRequest();
        req.setExerciseId(null);

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_invalidTypeExerciseId() throws Exception {
        var req = updateExerciseRequest();

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockAuthRequest.replaceFieldOfContent("exerciseId", "abc");
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.PARSE_JSON_ERR.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_emptyName() throws Exception {
        var req = updateExerciseRequest();
        req.setName("");

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_nullName() throws Exception {
        var req = updateExerciseRequest();
        req.setName(null);

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_tooLongName() throws Exception {
        var req = updateExerciseRequest();
        req.setName("1234567891234567891234567891232352346345746867");

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_nullBasicReps() throws Exception {
        var req = updateExerciseRequest();
        req.setBasicReps(null);

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_invalidTypeBasicReps() throws Exception {
        var req = updateExerciseRequest();

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockAuthRequest.replaceFieldOfContent("basicReps", "abc");
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.PARSE_JSON_ERR.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_negativeBasicReps() throws Exception {
        var req = updateExerciseRequest();
        req.setBasicReps(-1);

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_outOfBasicRepsRange() throws Exception {
        var req = updateExerciseRequest();
        req.setBasicReps(10_000);

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_nullLevel() throws Exception {
        var req = updateExerciseRequest();
        req.setLevel(null);

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_invalidTypeLevel() throws Exception {
        var req = updateExerciseRequest();

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockAuthRequest.replaceFieldOfContent("level", "abc");
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.PARSE_JSON_ERR.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_outOfLevelsRightRange() throws Exception {
        var req = updateExerciseRequest();
        req.setLevel(99);

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_outOfLevelsLeftRange() throws Exception {
        var req = updateExerciseRequest();
        req.setLevel(0);

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_emptyMuscleIds() throws Exception {
        var req = updateExerciseRequest();
        req.setMuscleIds(List.of());

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_nullMuscleIds() throws Exception {
        var req = updateExerciseRequest();
        req.setMuscleIds(null);

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_nullListAsMuscleIds() throws Exception {
        var req = updateExerciseRequest();
        List<Integer> muscleIds = Arrays.stream(new Integer[] {null, null}).toList();
        req.setMuscleIds(muscleIds);

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_outOfMuscleIdsRange() throws Exception {
        var req = updateExerciseRequest();
        req.setMuscleIds(List.of(1, 99));

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void updateExercise_admin_invalidTypeMuscleIds() throws Exception {
        var req = updateExerciseRequest();

        Mockito.when(exerciseServiceOfAdmin.updateExercise(req)).thenReturn(null);

        mockAuthRequest.setContent(req);
        mockAuthRequest.replaceFieldOfContent("muscleIds", List.of("a", "b", "c"));
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(PUT, "/v1/update-exercise"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(apiRes.getApplicationCode(), ErrorCodes.PARSE_JSON_ERR.getCode());
                assertNull(apiRes.getData());
            });
    }

    @Test
    public void deleteExercise_admin_valid() throws Exception {
        var req = DeleteObjectRequest.builder().id(2L).build();

        Mockito.doNothing().when(exerciseServiceOfAdmin).deleteExercise(req);

        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(DELETE, "/v1/delete-exercise"))
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.DELETE_EXERCISE.getCode()));
    }

    @Test
    public void deleteExercise_admin_invalidTypeExerciseId() throws Exception {
        var req = DeleteObjectRequest.builder().id(2L).build();
        mockAuthRequest.setContent(req);
        mockAuthRequest.replaceFieldOfContent("exerciseId", "abc");
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(DELETE, "/v1/delete-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void deleteExercise_admin_nullExerciseId() throws Exception {
        var req = DeleteObjectRequest.builder().id(null).build();
        mockAuthRequest.setContent(req);
        mockMvc
            .perform(mockAuthRequest.buildAdminRequestWithContent(DELETE, "/v1/delete-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getPaginatedListOfExercises_admin_valid() throws Exception {
        var request = PaginatedObjectRequest.builder().page(3).build();

        Mockito.when(exerciseServiceOfAdmin.getPaginatedListOfExercises(request)).thenReturn(Mockito.anyList());

        mockAuthRequest.setContent(request);
        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-exercises-by-page"))
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.GET_PAGINATED_EXERCISES.getCode()));
    }

    @Test
    public void getPaginatedListOfExercises_admin_nullPage() throws Exception {
        var request = PaginatedObjectRequest.builder().page(null).build();

        Mockito.when(exerciseServiceOfAdmin.getPaginatedListOfExercises(request)).thenReturn(null);

        mockAuthRequest.setContent(request);
        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-exercises-by-page"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getPaginatedListOfExercises_admin_invalidTypePage() throws Exception {
        var request = PaginatedObjectRequest.builder().page(3).build();

        Mockito.when(exerciseServiceOfAdmin.getPaginatedListOfExercises(request)).thenReturn(null);

        mockAuthRequest.setContent(request);
        mockAuthRequest.replaceFieldOfContent("page", "abc");
        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-exercises-by-page"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void getPaginatedListOfExercises_admin_tooSmallPage() throws Exception {
        var request = PaginatedObjectRequest.builder().page(0).build();

        Mockito.when(exerciseServiceOfAdmin.getPaginatedListOfExercises(request)).thenReturn(null);

        mockAuthRequest.setContent(request);
        mockMvc.perform(mockAuthRequest.buildAdminRequestWithContent(GET, "/v1/get-exercises-by-page"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }
}

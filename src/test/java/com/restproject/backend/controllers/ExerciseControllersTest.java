package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.*;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.helpers.JsonService;
import com.restproject.backend.helpers.MockAuthRequestBuilders;
import com.restproject.backend.helpers.MockAuthentication;
import com.restproject.backend.services.ExerciseService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
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
    MockAuthRequestBuilders mockAuthRequestBuilders;
    @Autowired
    JsonService jsonService;

    @MockBean
    ExerciseService exerciseServiceOfAdmin;

    @BeforeEach
    public void init() {
        mockAuthRequestBuilders.setJwtTokens(mockAuthentication.generateJwtTokens());
    }

    NewExerciseRequest newValidExercise() {
        return NewExerciseRequest.builder().name("Push-ups").level(1).basicReps(14)
            .muscleIds(List.of(0, 1)).build();
    }

    @Test
    public void createExercise_admin_valid() throws Exception {
        var request = this.newValidExercise();
        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.CREATE_EXERCISE.getCode()));
    }

    @Test
    public void createExercise_admin_emptyName() throws Exception {
        var request = this.newValidExercise();
        request.setName("");
        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_nullName() throws Exception {
        var request = this.newValidExercise();
        request.setName(null);

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_tooLongName() throws Exception {
        var request = this.newValidExercise();
        request.setName("12345678912345678912345678912345678952345235");

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_nullLevel() throws Exception {
        var request = this.newValidExercise();

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(request)
                .replaceFieldOfContent("level", "")
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_invalidTypeLevel() throws Exception {
        var request = this.newValidExercise();
        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(request)
                .replaceFieldOfContent("level", "abcdef")
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void createExercise_admin_outOfLevelRightRange() throws Exception {
        var request = this.newValidExercise();
        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(request)
                .replaceFieldOfContent("level", 5)
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_outOfLevelLeftRange() throws Exception {
        var request = this.newValidExercise();
        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(request)
                .replaceFieldOfContent("level", -2)
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_nullBasicReps() throws Exception {
        var request = this.newValidExercise();
        request.setBasicReps(null);
        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_invalidTypeBasicReps() throws Exception {
        var request = this.newValidExercise();
        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .replaceFieldOfContent("basicReps", "abcd")
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void createExercise_admin_negativeBasicReps() throws Exception {
        var request = this.newValidExercise();
        request.setBasicReps(-1);

        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_outOfBasicRepsRange() throws Exception {
        var request = this.newValidExercise();
        request.setBasicReps(10_000);
        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_emptyMuscleIds() throws Exception {
        var request = this.newValidExercise();
        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .replaceFieldOfContent("muscleIds", List.of())
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_nullMuscleIds() throws Exception {
        var request = this.newValidExercise();
        request.setMuscleIds(null);
        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_nullListAsMuscleIds() throws Exception {
        var request = this.newValidExercise();
        request.setMuscleIds(Arrays.stream(new Integer[]{null, null}).toList());
        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_outOfMuscleIdsRange() throws Exception {
        var request = this.newValidExercise();
        request.setMuscleIds(List.of(1, 99));
        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createExercise_admin_invalidTypeMuscleIds() throws Exception {
        var request = this.newValidExercise();
        Mockito.when(exerciseServiceOfAdmin.createExercise(request)).thenReturn(null);

        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .replaceFieldOfContent("muscleIds", List.of("a", "b", "c"))
                .buildAdminRequestWithContent(POST, "/v1/create-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    UpdateExerciseRequest updateExerciseAndMusclesRequest() {
        return UpdateExerciseRequest.builder().exerciseId(2L).level(2).name("Push-ups")
            .basicReps(14).muscleIds(List.of(0, 2)).build();
    }

    @Test
    public void updateExerciseAndMuscles_admin_valid() throws Exception {
        var req = updateExerciseAndMusclesRequest();
        var res = Exercise.builder().exerciseId(req.getExerciseId()).levelEnum(Level.getByLevel(req.getLevel()))
            .basicReps(req.getBasicReps()).name(req.getName()).build();

        Mockito.when(exerciseServiceOfAdmin.updateExerciseAndMuscles(req)).thenReturn(res);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Exercise.class);
                assertEquals(SucceedCodes.UPDATE_EXERCISE.getCode(), apiRes.getApplicationCode());
                assertEquals(res, apiRes.getData());
            });
    }


    @Test
    public void updateExerciseAndMuscles_admin_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(POST, "/api/private/admin/v1/update-exercise-and-muscles"))
            .andExpect(status().isUnauthorized());
    }


    @Test
    public void updateExerciseAndMuscles_admin_nullExerciseId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("exerciseId", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_invalidTypeExerciseId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("exerciseId", "abc")
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_emptyName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("name", "")
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_nullName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("name", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_tooLongName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("name", "1234567891234567891234567891232352346345746867")
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_nullBasicReps() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("basicReps", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_invalidTypeBasicReps() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("basicReps", "abc")
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_negativeBasicReps() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("basicReps", -1)
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_outOfBasicRepsRange() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("basicReps", 10_000)
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_nullLevel() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("level", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_invalidTypeLevel() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("level", "abc")
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_outOfLevelsRightRange() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("level", 99)
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_outOfLevelsLeftRange() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("level", 0)
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_emptyMuscleIds() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("muscleIds", List.of())
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_nullMuscleIds() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("muscleIds", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_nullListAsMuscleIds() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("muscleIds", Arrays.stream(new Integer[]{null, null}).toList())
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_outOfMuscleIdsRange() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("muscleIds", List.of(1, 99))
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateExerciseAndMuscles_admin_invalidTypeMuscleIds() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateExerciseAndMusclesRequest())
                .replaceFieldOfContent("muscleIds", List.of("a", "b", "c"))
                .buildAdminRequestWithContent(PUT, "/v1/update-exercise-and-muscles"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void deleteExercise_admin_valid() throws Exception {
        var req = DeleteObjectRequest.builder().id(2L).build();
        Mockito.doNothing().when(exerciseServiceOfAdmin).deleteExercise(req);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(DELETE, "/v1/delete-exercise"))
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.DELETE_EXERCISE.getCode()));
    }

    @Test
    public void deleteExercise_admin_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(POST, "/api/private/admin/v1/delete-exercise"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteExercise_admin_invalidTypeExerciseId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(DeleteObjectRequest.builder().id(2L).build())
                .replaceFieldOfContent("exerciseId", "abc")
                .buildAdminRequestWithContent(DELETE, "/v1/delete-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void deleteExercise_admin_nullExerciseId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(DeleteObjectRequest.builder().id(null).build())
                .buildAdminRequestWithContent(DELETE, "/v1/delete-exercise"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }
}

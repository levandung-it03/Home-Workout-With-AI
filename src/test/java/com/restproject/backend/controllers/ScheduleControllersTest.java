package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.DeleteObjectRequest;
import com.restproject.backend.dtos.request.NewScheduleRequest;
import com.restproject.backend.dtos.request.UpdateScheduleRequest;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.helpers.JsonService;
import com.restproject.backend.helpers.MockAuthRequestBuilders;
import com.restproject.backend.helpers.MockAuthentication;
import com.restproject.backend.mappers.ScheduleMappers;
import com.restproject.backend.services.Admin.ScheduleService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleControllersTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MockAuthentication mockAuthentication;
    @Autowired
    MockAuthRequestBuilders mockAuthRequestBuilders;
    @Autowired
    JsonService jsonService;
    @Autowired
    ScheduleMappers scheduleMappers;

    @MockBean
    ScheduleService scheduleServiceOfAdmin;

    @BeforeEach
    public void init() {
        mockAuthRequestBuilders.setJwtTokens(mockAuthentication.generateJwtTokens());
    }

    NewScheduleRequest newScheduleRequest() {
        return NewScheduleRequest.builder().name("Schedule").level(Level.INTERMEDIATE.getLevel())
            .description("Description").coins(2000L).sessionIds(List.of(1L,2L)).build();
    }

    @Test
    public void createSchedule_admin_valid() throws Exception {
        var req = this.newScheduleRequest();
        Mockito.when(scheduleServiceOfAdmin.createSchedule(req)).thenReturn(scheduleMappers.insertionToPlain(req));
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(result -> {
                assertNotNull(result);
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(),
                    Schedule.class);

                assertEquals(apiRes.getApplicationCode(), SucceedCodes.CREATE_SCHEDULE.getCode());
                assertEquals(apiRes.getData().getName(), req.getName());
                assertEquals(apiRes.getData().getDescription(), req.getDescription());
                assertEquals(apiRes.getData().getLevel().getLevel(), req.getLevel());
                assertEquals(apiRes.getData().getCoins(), req.getCoins());
            });
    }

    @Test
    public void createSchedule_admin_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(POST, "/api/private/admin/v1/create-schedule"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void createSchedule_admin_emptyName() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newScheduleRequest())
                .replaceFieldOfContent("name", "")
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSchedule_admin_nullName() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newScheduleRequest())
                .replaceFieldOfContent("name", null)
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSchedule_admin_tooLongName() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newScheduleRequest())
                .replaceFieldOfContent("name", "1234567891234567891234567894123412431543513452345234523452345243523452435243")
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSchedule_admin_emptyDescription() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newScheduleRequest())
                .replaceFieldOfContent("description", "")
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSchedule_admin_nullDescription() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newScheduleRequest())
                .replaceFieldOfContent("description", null)
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSchedule_admin_tooLongDescription() throws Exception {
        var request = this.newScheduleRequest();
        for (var i = 1; i <= 110; i++) request.setName(request.getName() + "a");
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSchedule_admin_nullCoins() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newScheduleRequest())
                .replaceFieldOfContent("coins", null)
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSchedule_admin_invalidTypeCoins() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newScheduleRequest())
                .replaceFieldOfContent("coins", "abcdef")
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void createSchedule_admin_nullLevel() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newScheduleRequest())
                .replaceFieldOfContent("level", null)
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSchedule_admin_invalidTypeLevel() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newScheduleRequest())
                .replaceFieldOfContent("level", "abcdef")
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void createSchedule_admin_outOfLevelRightRange() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newScheduleRequest())
                .replaceFieldOfContent("level", 5)
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSchedule_admin_outOfLevelLeftRange() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newScheduleRequest())
                .replaceFieldOfContent("level", -2)
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSchedule_admin_emptySessionIds() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newScheduleRequest())
                .replaceFieldOfContent("sessionIds", List.of())
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSchedule_admin_nullSessionIds() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newScheduleRequest())
                .replaceFieldOfContent("sessionIds", null)
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSchedule_admin_nullListAsSessionIds() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newScheduleRequest())
                .replaceFieldOfContent("sessionIds", Arrays.stream(new Long[]{null, null}).toList())
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void createSchedule_admin_invalidTypeSessionIds() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.newScheduleRequest())
                .replaceFieldOfContent("sessionIds", List.of("a", "b", "c"))
                .buildAdminRequestWithContent(POST, "/v1/create-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }
    
    UpdateScheduleRequest updateSchedule() {
        return UpdateScheduleRequest.builder().scheduleId(2L).level(2).name("Push-ups")
            .description("Hello").coins(2000L).build();
    }

    @Test
    public void updateScheduleAndMuscles_admin_valid() throws Exception {
        var req = updateSchedule();
        var res = Schedule.builder().scheduleId(req.getScheduleId()).level(Level.getByLevel(req.getLevel()))
            .description(req.getDescription()).name(req.getName()).coins(2000L).build();

        Mockito.when(scheduleServiceOfAdmin.updateSchedule(req)).thenReturn(res);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(PUT, "/v1/update-schedule"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), Schedule.class);
                assertEquals(SucceedCodes.UPDATE_SESSION.getCode(), apiRes.getApplicationCode());
                assertEquals(res, apiRes.getData());
            });
    }

    @Test
    public void updateScheduleAndMuscles_admin_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(PUT, "/api/private/admin/v1/update-schedule"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateScheduleAndMuscles_admin_nullScheduleId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSchedule())
                .replaceFieldOfContent("scheduleId", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateScheduleAndMuscles_admin_invalidTypeScheduleId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSchedule())
                .replaceFieldOfContent("scheduleId", "abc")
                .buildAdminRequestWithContent(PUT, "/v1/update-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void updateScheduleAndMuscles_admin_emptyName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSchedule())
                .replaceFieldOfContent("name", "")
                .buildAdminRequestWithContent(PUT, "/v1/update-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateScheduleAndMuscles_admin_nullName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSchedule())
                .replaceFieldOfContent("name", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateScheduleAndMuscles_admin_tooLongName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSchedule())
                .replaceFieldOfContent("name", "1234567891234567891234567891232352346345746867524435252435")
                .buildAdminRequestWithContent(PUT, "/v1/update-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateScheduleAndMuscles_admin_emptyDescription() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.updateSchedule())
                .replaceFieldOfContent("description", "")
                .buildAdminRequestWithContent(PUT, "/v1/update-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateScheduleAndMuscles_admin_nullDescription() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.updateSchedule())
                .replaceFieldOfContent("description", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateScheduleAndMuscles_admin_tooLongDescription() throws Exception {
        var request = this.updateSchedule();
        for (var i = 1; i <= 110; i++) request.setName(request.getName() + "a");
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(request)
                .buildAdminRequestWithContent(PUT, "/v1/update-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateScheduleAndMuscles_admin_nullLevel() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSchedule())
                .replaceFieldOfContent("level", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateScheduleAndMuscles_admin_invalidTypeLevel() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSchedule())
                .replaceFieldOfContent("level", "abc")
                .buildAdminRequestWithContent(PUT, "/v1/update-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void updateScheduleAndMuscles_admin_outOfLevelsRightRange() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSchedule())
                .replaceFieldOfContent("level", 99)
                .buildAdminRequestWithContent(PUT, "/v1/update-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateScheduleAndMuscles_admin_outOfLevelsLeftRange() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(updateSchedule())
                .replaceFieldOfContent("level", 0)
                .buildAdminRequestWithContent(PUT, "/v1/update-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateScheduleAndMuscles_admin_nullCoins() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.updateSchedule())
                .replaceFieldOfContent("coins", null)
                .buildAdminRequestWithContent(PUT, "/v1/update-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateScheduleAndMuscles_admin_invalidTypeCoins() throws Exception {
        mockMvc.perform(mockAuthRequestBuilders
                .setContent(this.updateSchedule())
                .replaceFieldOfContent("coins", "abcdef")
                .buildAdminRequestWithContent(PUT, "/v1/update-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void deleteSchedule_admin_valid() throws Exception {
        var req = DeleteObjectRequest.builder().id(2L).build();
        Mockito.doNothing().when(scheduleServiceOfAdmin).deleteSchedule(req);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(DELETE, "/v1/delete-schedule"))
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.DELETE_SCHEDULE.getCode()));
    }

    @Test
    public void deleteSchedule_admin_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.request(POST, "/api/private/admin/v1/delete-schedule"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteSchedule_admin_invalidTypeScheduleId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(DeleteObjectRequest.builder().id(2L).build())
                .replaceFieldOfContent("scheduleId", "abc")
                .buildAdminRequestWithContent(DELETE, "/v1/delete-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void deleteSchedule_admin_nullScheduleId() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(DeleteObjectRequest.builder().id(null).build())
                .buildAdminRequestWithContent(DELETE, "/v1/delete-schedule"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }
}

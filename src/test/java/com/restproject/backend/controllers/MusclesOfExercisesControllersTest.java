package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.response.ExerciseHasMusclesResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.helpers.JsonService;
import com.restproject.backend.helpers.MockAuthRequestBuilders;
import com.restproject.backend.helpers.MockAuthentication;
import com.restproject.backend.services.Admin.MusclesOfExercisesService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.http.HttpMethod.*;

import java.util.HashMap;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MusclesOfExercisesControllersTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MockAuthentication mockAuthentication;
    @Autowired
    MockAuthRequestBuilders mockAuthRequestBuilders;
    @Autowired
    JsonService jsonService;

    @MockBean
    MusclesOfExercisesService musclesOfExercisesServiceOfAdmin;

    @BeforeEach
    public void init() {
        mockAuthRequestBuilders.setJwtTokens(mockAuthentication.generateJwtTokens());
    }
    
    @Test
    public void getExercisesHasMusclesPages_admin_valid() throws Exception {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", "INTERMEDIATE");
        var req = PaginatedTableRequest.builder().page(1).filterFields(ftr).sortedField("name").sortedMode(1).build();
        var res = TablePagesResponse.<ExerciseHasMusclesResponse>builder()
            .data(List.of(
                ExerciseHasMusclesResponse.builder().build(),
                ExerciseHasMusclesResponse.builder().build()
            ))
            .totalPages(1).build();
        Mockito.when(musclesOfExercisesServiceOfAdmin.getExercisesHasMusclesPages(req)).thenReturn(res);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-exercises-has-muscles-pages"))
            .andExpect(response -> {
                assertNotNull(response);

                ApiResponseObject<TablePagesResponse> apiRes = jsonService
                    .parseResponseJson(response.getResponse().getContentAsString(), TablePagesResponse.class);

                TablePagesResponse<ExerciseHasMusclesResponse> dataFromApiRes = apiRes.getData();

                assertNotNull(apiRes);
                assertEquals(SucceedCodes.GET_EXERCISES_HAS_MUSCLES_PAGES.getCode(), apiRes.getApplicationCode());
                assertEquals(res.getData().size(), dataFromApiRes.getData().size());
            });
    }

    @Test
    public void getExercisesHasMusclesPages_admin_unauthorized() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.request(GET,
                "/api/private/admin/v1/get-exercises-has-muscles-pages"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getExercisesHasMusclesPages_admin_nullPage() throws Exception {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", "INTERMEDIATE");
        var req = PaginatedTableRequest.builder().page(null).filterFields(ftr).build();

        Mockito.when(musclesOfExercisesServiceOfAdmin.getExercisesHasMusclesPages(req)).thenReturn(null);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-exercises-has-muscles-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getExercisesHasMusclesPages_admin_tooSmallPage() throws Exception {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", "INTERMEDIATE");
        var req = PaginatedTableRequest.builder().page(0).filterFields(ftr).build();

        Mockito.when(musclesOfExercisesServiceOfAdmin.getExercisesHasMusclesPages(req)).thenReturn(null);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-exercises-has-muscles-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getExercisesHasMusclesPages_admin_nullFilterFields() throws Exception {
        var req = PaginatedTableRequest.builder().page(1).filterFields(null).build();

        Mockito.when(musclesOfExercisesServiceOfAdmin.getExercisesHasMusclesPages(req)).thenReturn(null);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-exercises-has-muscles-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getExercisesHasMusclesPages_admin_invalidSortedMode() throws Exception {
        var ftr = new HashMap<String, Object>();
        var req = PaginatedTableRequest.builder().page(1).filterFields(ftr).sortedMode(0).build();

        Mockito.when(musclesOfExercisesServiceOfAdmin.getExercisesHasMusclesPages(req)).thenReturn(null);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-exercises-has-muscles-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }
}

package com.restproject.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.initialization.MockAuthentication;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;

@SpringBootTest
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnumsControllersTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MockAuthentication mockAuthentication;
    @Autowired
    ObjectMapper objectMapper;

    final String adminUrl = "/api/private/admin";
    final String userUrl = "/api/private/user";
    HashMap<String, String> jwtTokens;

    @BeforeEach
    public void init() {
        jwtTokens = mockAuthentication.generateJwtTokens();
    }

    @Test
    public void valid_admin_getAllMuscles() throws Exception {
        var mockRequest = MockMvcRequestBuilders
            .request(HttpMethod.GET, adminUrl + "/v1/get-all-muscles")
            .header("Authorization", "Bearer " + jwtTokens.get("root_accessToken"))
            .contentType(MediaType.APPLICATION_JSON_VALUE);
        var expectedValue = Muscle.getAllMuscles().stream().map(Muscle::toString).toArray();

        mockMvc.perform(mockRequest)
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.GET_ALL_MUSCLE_ENUMS.getCode()))
            .andExpect(jsonPath("data[*]", Matchers.contains(expectedValue)));
    }

    @Test
    public void valid_user_getAllMuscles() throws Exception {
        var mockRequest = MockMvcRequestBuilders
            .request(HttpMethod.GET, userUrl + "/v1/get-all-muscles")
            .header("Authorization", "Bearer " + jwtTokens.get("user_accessToken"))
            .contentType(MediaType.APPLICATION_JSON_VALUE);
        var expectedValue = Muscle.getAllMuscles().stream().map(Muscle::toString).toArray();

        mockMvc.perform(mockRequest)
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.GET_ALL_MUSCLE_ENUMS.getCode()))
            .andExpect(jsonPath("data[*]", Matchers.contains(expectedValue)));
    }

    @Test
    public void valid_admin_getAllLevels() throws Exception {
        var mockRequest = MockMvcRequestBuilders
            .request(HttpMethod.GET, adminUrl + "/v1/get-all-levels")
            .header("Authorization", "Bearer " + jwtTokens.get("root_accessToken"))
            .contentType(MediaType.APPLICATION_JSON_VALUE);
        var expectedValue = Level.getAllLevels().stream().map(Level::toString).toArray();

        mockMvc.perform(mockRequest)
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.GET_ALL_LEVEL_ENUMS.getCode()))
            .andExpect(jsonPath("data[*]", Matchers.contains(expectedValue)));
    }

    @Test
    public void valid_user_getAllLevels() throws Exception {
        var mockRequest = MockMvcRequestBuilders
            .request(HttpMethod.GET, userUrl + "/v1/get-all-levels")
            .header("Authorization", "Bearer " + jwtTokens.get("user_accessToken"))
            .contentType(MediaType.APPLICATION_JSON_VALUE);
        var expectedValue = Level.getAllLevels().stream().map(Level::toString).toArray();

        mockMvc.perform(mockRequest)
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.GET_ALL_LEVEL_ENUMS.getCode()))
            .andExpect(jsonPath("data[*]", Matchers.contains(expectedValue)));
    }
}

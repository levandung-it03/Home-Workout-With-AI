package com.restproject.backend.controllers;

import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.helpers.MockAuthRequestBuilders;
import com.restproject.backend.helpers.MockAuthentication;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnumsControllersTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MockAuthentication mockAuthentication;
    @Autowired
    MockAuthRequestBuilders mockAuthRequest;


    @BeforeEach
    public void init() {
        mockAuthRequest.setJwtTokens(mockAuthentication.generateJwtTokens());
    }

    @Test
    public void getAllMuscles_admin_valid() throws Exception {
        var mockRequest = mockAuthRequest.buildAdminRequestNonContent(HttpMethod.GET, "/v1/get-all-muscles");
        var expectedValue = Muscle.getAllMuscles().stream().map(Muscle::toString).toArray();    //--enums list

        mockMvc.perform(mockRequest)
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.GET_ALL_MUSCLE_ENUMS.getCode()))
            .andExpect(jsonPath("data[*]", Matchers.contains(expectedValue)));
    }

    @Test
    public void getAllMuscles_user_valid() throws Exception {
        var mockRequest = mockAuthRequest.buildUserRequestNonContent(HttpMethod.GET, "/v1/get-all-muscles");
        var expectedValue = Muscle.getAllMuscles().stream().map(Muscle::toString).toArray();

        mockMvc.perform(mockRequest)
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.GET_ALL_MUSCLE_ENUMS.getCode()))
            .andExpect(jsonPath("data[*]", Matchers.contains(expectedValue)));
    }

    @Test
    public void getAllLevels_admin_valid() throws Exception {
        var mockRequest = mockAuthRequest.buildAdminRequestNonContent(HttpMethod.GET, "/v1/get-all-levels");
        var expectedValue = Level.getAllLevels().stream().map(Level::toString).toArray();

        mockMvc.perform(mockRequest)
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.GET_ALL_LEVEL_ENUMS.getCode()))
            .andExpect(jsonPath("data[*]", Matchers.contains(expectedValue)));
    }

    @Test
    public void getAllLevels_user_valid() throws Exception {
        var mockRequest = mockAuthRequest.buildUserRequestNonContent(HttpMethod.GET, "/v1/get-all-levels");
        var expectedValue = Level.getAllLevels().stream().map(Level::toString).toArray();

        mockMvc.perform(mockRequest)
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.GET_ALL_LEVEL_ENUMS.getCode()))
            .andExpect(jsonPath("data[*]", Matchers.contains(expectedValue)));
    }
}

package com.restproject.backend.controllers;

import com.restproject.backend.enums.Gender;
import com.restproject.backend.enums.Level;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

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
    MockAuthRequestBuilders mockAuthRequestBuilders;


    @BeforeEach
    public void init() {
        mockAuthRequestBuilders.setJwtTokens(mockAuthentication.generateJwtTokens());
    }

    @Test
    public void getAllGenders_admin_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/private/admin/v1/get-all-genders"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllGenders_user_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/private/user/v1/get-all-genders"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllMuscles_admin_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/private/admin/v1/get-all-muscles"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllMuscles_user_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/private/user/v1/get-all-muscles"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllLevels_admin_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/private/admin/v1/get-all-levels"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllLevels_user_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/private/user/v1/get-all-levels"))
            .andExpect(status().isUnauthorized());
    }

//    @Test
//    public void getAllMuscles_admin_valid() throws Exception {
//        var mockRequest = mockAuthRequestBuilders.buildAdminRequestNonContent(HttpMethod.GET, "/v1/get-all-muscles");
//        var expectedValue = Muscle.getAllMuscles().stream().map(Muscle::toString).toArray();    //--enums list
//
//        mockMvc.perform(mockRequest)
//            .andExpect(jsonPath("applicationCode").value(SucceedCodes.GET_ALL_MUSCLE_ENUMS.getCode()))
//            .andExpect(jsonPath("data[*]", Matchers.contains(expectedValue)));
//    }
//
//    @Test
//    public void getAllMuscles_user_valid() throws Exception {
//        var mockRequest = mockAuthRequestBuilders.buildUserRequestNonContent(HttpMethod.GET, "/v1/get-all-muscles");
//        var expectedValue = Muscle.getAllMuscles().stream().map(Muscle::toString).toArray();
//
//        mockMvc.perform(mockRequest)
//            .andExpect(jsonPath("applicationCode").value(SucceedCodes.GET_ALL_MUSCLE_ENUMS.getCode()))
//            .andExpect(jsonPath("data[*]", Matchers.contains(expectedValue)));
//    }

    @Test
    public void getAllGenders_admin_valid() throws Exception {
        var mockRequest = mockAuthRequestBuilders.buildAdminRequestNonContent(HttpMethod.GET, "/v1/get-all-genders");
        var expectedValue = Arrays.stream(Gender.values()).map(Gender::toString).toArray();    //--enums list

        mockMvc.perform(mockRequest)
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.GET_ALL_GENDER_ENUMS.getCode()))
            .andExpect(jsonPath("data[*]", Matchers.contains(expectedValue)));
    }

    @Test
    public void getAllGenders_user_valid() throws Exception {
        var mockRequest = mockAuthRequestBuilders.buildUserRequestNonContent(HttpMethod.GET, "/v1/get-all-genders");
        var expectedValue = Arrays.stream(Gender.values()).map(Gender::toString).toArray();    //--enums list

        mockMvc.perform(mockRequest)
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.GET_ALL_GENDER_ENUMS.getCode()))
            .andExpect(jsonPath("data[*]", Matchers.contains(expectedValue)));
    }

    @Test
    public void getAllLevels_admin_valid() throws Exception {
        var mockRequest = mockAuthRequestBuilders.buildAdminRequestNonContent(HttpMethod.GET, "/v1/get-all-levels");
        var expectedValue = Level.getAllLevels().stream().map(Level::toString).toArray();

        mockMvc.perform(mockRequest)
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.GET_ALL_LEVEL_ENUMS.getCode()))
            .andExpect(jsonPath("data[*]", Matchers.contains(expectedValue)));
    }

    @Test
    public void getAllLevels_user_valid() throws Exception {
        var mockRequest = mockAuthRequestBuilders.buildUserRequestNonContent(HttpMethod.GET, "/v1/get-all-levels");
        var expectedValue = Level.getAllLevels().stream().map(Level::toString).toArray();

        mockMvc.perform(mockRequest)
            .andExpect(jsonPath("applicationCode").value(SucceedCodes.GET_ALL_LEVEL_ENUMS.getCode()))
            .andExpect(jsonPath("data[*]", Matchers.contains(expectedValue)));
    }
}

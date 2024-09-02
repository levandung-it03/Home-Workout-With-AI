package com.restproject.backend.controllers;

import com.restproject.backend.dtos.reponse.ApiResponseObject;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.exceptions.ApplicationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class EnumsControllers {
    MockMvc mockMvc;

    String adminUrl = "/api/private/admin/";

    @Test
    public ApiResponseObject<List<Muscle>> valid_getAllMuscles() throws ApplicationException {

    }
}

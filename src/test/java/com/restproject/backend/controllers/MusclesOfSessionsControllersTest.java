package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.response.SessionHasMusclesResponse;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.helpers.JsonService;
import com.restproject.backend.helpers.MockAuthRequestBuilders;
import com.restproject.backend.helpers.MockAuthentication;
import com.restproject.backend.services.Admin.MusclesOfSessionsService;
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

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MusclesOfSessionsControllersTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MockAuthentication mockAuthentication;
    @Autowired
    MockAuthRequestBuilders mockAuthRequestBuilders;
    @Autowired
    JsonService jsonService;
    
    @MockBean
    MusclesOfSessionsService musclesOfSessionsServiceOfAdmin;
    
    @BeforeEach
    public void init() {
        mockAuthRequestBuilders.setJwtTokens(mockAuthentication.generateJwtTokens());
    }
    
    @Test
    public void getSessionsHasMusclesPages_admin_valid() throws Exception {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", 2);
        var req = PaginatedTableRequest.builder().page(1).filterFields(ftr).build();
        var res = TablePagesResponse.<SessionHasMusclesResponse>builder().data(
            List.of(new SessionHasMusclesResponse(), new SessionHasMusclesResponse())
        ).build();
        Mockito.when(musclesOfSessionsServiceOfAdmin.getSessionsHasMusclesPages(req)).thenReturn(res);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-sessions-has-muscles-pages"))
            .andExpect(response -> {
                assertNotNull(response);

                ApiResponseObject<TablePagesResponse> apiRes = jsonService
                    .parseResponseJson(response.getResponse().getContentAsString(), TablePagesResponse.class);
                List<SessionHasMusclesResponse> data = apiRes.getData().getData();

                assertNotNull(apiRes);
                assertEquals(SucceedCodes.GET_SESSIONS_HAS_MUSCLES_PAGES.getCode(), apiRes.getApplicationCode());
                assertEquals(res.getData().size(), data.size());
            });
    }

    @Test
    public void getSessionsHasMusclesPages_admin_unauthorized() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.request(GET,
                "/api/private/admin/v1/get-sessions-has-muscles-pages"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getSessionsHasMusclesPages_admin_nullPage() throws Exception {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", "INTERMEDIATE");
        var req = PaginatedTableRequest.builder().page(null).filterFields(ftr).build();

        Mockito.when(musclesOfSessionsServiceOfAdmin.getSessionsHasMusclesPages(req)).thenReturn(null);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-sessions-has-muscles-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getSessionsHasMusclesPages_admin_tooSmallPage() throws Exception {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", "INTERMEDIATE");
        var req = PaginatedTableRequest.builder().page(0).filterFields(ftr).build();

        Mockito.when(musclesOfSessionsServiceOfAdmin.getSessionsHasMusclesPages(req)).thenReturn(null);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-sessions-has-muscles-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getSessionsHasMusclesPages_admin_nullFilterFields() throws Exception {
        var req = PaginatedTableRequest.builder().page(1).filterFields(null).build();

        Mockito.when(musclesOfSessionsServiceOfAdmin.getSessionsHasMusclesPages(req)).thenReturn(null);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-sessions-has-muscles-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getSessionsHasMusclesPages_admin_invalidSortedMode() throws Exception {
        var ftr = new HashMap<String, Object>();
        var req = PaginatedTableRequest.builder().page(1).filterFields(ftr).sortedMode(0).build();

        Mockito.when(musclesOfSessionsServiceOfAdmin.getSessionsHasMusclesPages(req)).thenReturn(null);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-sessions-has-muscles-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

}

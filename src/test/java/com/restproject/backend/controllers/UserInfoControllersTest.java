package com.restproject.backend.controllers;

import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.dtos.request.UpdateUserInfoRequest;
import com.restproject.backend.dtos.response.ApiResponseObject;
import com.restproject.backend.dtos.response.TablePagesResponse;
import com.restproject.backend.dtos.response.UserInfoAndStatusResponse;
import com.restproject.backend.entities.UserInfo;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Gender;
import com.restproject.backend.enums.SucceedCodes;
import com.restproject.backend.helpers.JsonService;
import com.restproject.backend.helpers.MockAuthRequestBuilders;
import com.restproject.backend.helpers.MockAuthentication;
import com.restproject.backend.services.UserInfoService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoControllersTest {
    @Value("${services.back-end.user-info.min-age}")
    int minAge;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MockAuthentication mockAuthentication;
    @Autowired
    MockAuthRequestBuilders mockAuthRequestBuilders;
    @Autowired
    JsonService jsonService;

    @MockBean
    UserInfoService userInfoService;

    @BeforeEach
    public void init() {
        mockAuthRequestBuilders.setJwtTokens(mockAuthentication.generateJwtTokens());
    }

    @Test
    public void getUserInfoAndStatusPages_admin_valid() throws Exception {
        var ftr = new HashMap<String, Object>();
        ftr.put("firstName", "Stre");
        ftr.put("dob", "2023-09-24T14:00:00.000Z");
        var req = PaginatedTableRequest.builder().page(1).filterFields(ftr).sortedField("coins").sortedMode(1).build();
        var res = TablePagesResponse.<UserInfoAndStatusResponse>builder()
            .data(List.of(
                UserInfoAndStatusResponse.builder().build(),
                UserInfoAndStatusResponse.builder().build()
            ))
            .totalPages(1).build();
        Mockito.when(userInfoService.getUserInfoAndStatusPages(req)).thenReturn(res);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-user-info-and-status-pages"))
            .andExpect(response -> {
                assertNotNull(response);

                ApiResponseObject<TablePagesResponse> apiRes = jsonService
                    .parseResponseJson(response.getResponse().getContentAsString(), TablePagesResponse.class);

                TablePagesResponse<UserInfoAndStatusResponse> dataFromApiRes = apiRes.getData();

                assertNotNull(apiRes);
                assertEquals(SucceedCodes.GET_USER_INFO_PAGES.getCode(), apiRes.getApplicationCode());
                assertEquals(res.getData().size(), dataFromApiRes.getData().size());
            });
    }

    @Test
    public void getUserInfoAndStatusPages_admin_unauthorized() throws Exception {
        mockMvc
            .perform(MockMvcRequestBuilders.request(GET,
                "/api/private/admin/v1/get-user-info-and-status-pages"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getUserInfoAndStatusPages_admin_nullPage() throws Exception {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", "INTERMEDIATE");
        var req = PaginatedTableRequest.builder().page(null).filterFields(ftr).build();

        Mockito.when(userInfoService.getUserInfoAndStatusPages(req)).thenReturn(null);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-user-info-and-status-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getUserInfoAndStatusPages_admin_tooSmallPage() throws Exception {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        ftr.put("level", "INTERMEDIATE");
        var req = PaginatedTableRequest.builder().page(0).filterFields(ftr).build();

        Mockito.when(userInfoService.getUserInfoAndStatusPages(req)).thenReturn(null);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-user-info-and-status-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getUserInfoAndStatusPages_admin_nullFilterFields() throws Exception {
        var req = PaginatedTableRequest.builder().page(0).filterFields(null).build();

        Mockito.when(userInfoService.getUserInfoAndStatusPages(req)).thenReturn(null);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-user-info-and-status-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void getUserInfoAndStatusPages_admin_invalidSortedMode() throws Exception {
        var ftr = new HashMap<String, Object>();
        var req = PaginatedTableRequest.builder().page(1).filterFields(ftr).sortedMode(0).build();

        Mockito.when(userInfoService.getUserInfoAndStatusPages(req)).thenReturn(null);

        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildAdminRequestWithContent(GET, "/v1/get-user-info-and-status-pages"))
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }


    UpdateUserInfoRequest updateUserInfoRequest() {
        return UpdateUserInfoRequest.builder().firstName("Dung").lastName("Le Van")
            .genderId(1).dob(LocalDate.of(2003, 12, 11)).build();
    }

    @Test
    public void updateUserInfo_user_valid() throws Exception {
        var req = this.updateUserInfoRequest();
        var expected = UserInfo.builder().user(null).lastName(req.getLastName()).firstName(req.getFirstName())
            .coins(2000L).gender(Gender.getByGenderId(req.getGenderId())).dob(req.getDob())
            .build();
        Mockito.when(userInfoService.updateUserInfo(req,
            "Bearer " + mockAuthRequestBuilders.getJwtTokens().get("user_accessToken"))).thenReturn(expected);
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(req)
                .buildUserRequestWithContent(POST, "/v1/update-user-info"))
            .andExpect(result -> {
                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), UserInfo.class);

                assertNotNull(result.getResponse().getContentAsString());
                assertEquals(SucceedCodes.UPDATE_USER_INFO.getCode(), apiRes.getApplicationCode());
                assertEquals(expected, apiRes.getData());
            });
    }

    @Test
    public void updateUserInfo_user_invalidPatternFirstName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("firstName", "Le Van1234")
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_invalidTypeFirstName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("firstName", 1234)
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_tooLongFirstName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("firstName", "abcdacsdfnasdfkjasdlfbalksjdfblaiusbfdjasblfibwaefblkdsajbladsudbf")
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_nullFirstName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("firstName", null)
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_tooShortFirstName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("firstName", "")
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_invalidPatternLastName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("lastName", "1234")
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_invalidTypeLastName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("lastName", 1234)
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_tooLongLastName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("lastName", "abcdacsdfnasdfkjasdlfbalksjdfblaiusbfdjasblfibwaefblkdsajbladsudbf")
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_nullLastName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("lastName", null)
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_tooShortLastName() throws Exception {
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("lastName", "")
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_nullDob() throws Exception{
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("dob", null)
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_invalidTypeDob() throws Exception{
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("dob", "abcd")
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void updateUserInfo_user_invalidConstraintDob() throws Exception{
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("dob", LocalDate.now().plusYears(this.minAge - 1))
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_nullGenderId() throws Exception{
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("genderId", null)
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_invalidTypeGenderId() throws Exception{
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("genderId", "abcd")
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }

    @Test
    public void updateUserInfo_user_invalidConstraintGenderId() throws Exception{
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("genderId", 2)
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_nullEmail() throws Exception{
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("email", null)
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_invalidPatternEmail() throws Exception{
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("email", "levandung@")
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_nullCoins() throws Exception{
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("coins", null)
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
    }

    @Test
    public void updateUserInfo_user_invalidTypeCoins() throws Exception{
        mockMvc
            .perform(mockAuthRequestBuilders
                .setContent(this.updateUserInfoRequest())
                .replaceFieldOfContent("coins", "abcd")
                .buildUserRequestWithContent(POST, "/v1/update-user-info")
            )
            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
    }
}

//package com.restproject.backend.controllers;
//
//import com.restproject.backend.dtos.request.NewUserRequest;
//import com.restproject.backend.entities.UserInfo;
//import com.restproject.backend.enums.ErrorCodes;
//import com.restproject.backend.enums.Gender;
//import com.restproject.backend.enums.SucceedCodes;
//import com.restproject.backend.helpers.JsonService;
//import com.restproject.backend.helpers.MockAuthRequestBuilders;
//import com.restproject.backend.helpers.MockAuthentication;
//import com.restproject.backend.services.UserService;
//import lombok.AccessLevel;
//import lombok.experimental.FieldDefaults;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.springframework.http.HttpMethod.POST;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class UserControllersTest {
//    @Value("${services.back-end.user-info.min-age}")
//    int minAge;
//    @Autowired
//    MockMvc mockMvc;
//    @Autowired
//    MockAuthentication mockAuthentication;
//    @Autowired
//    MockAuthRequestBuilders mockAuthRequestBuilders;
//    @Autowired
//    JsonService jsonService;
//
//    @MockBean
//    UserService userService;
//
//    @BeforeEach
//    public void init() {
//        mockAuthRequestBuilders.setJwtTokens(mockAuthentication.generateJwtTokens());
//    }
//
//
//
//    NewUserRequest newUserRequest() {
//        return NewUserRequest.builder().firstName("Dung").lastName("Le Van").email("levandung.it03@gmail.com")
//            .genderId(1).dob(LocalDate.of(2003, 12, 11)).password("1234567").build();
//    }
//
//    @Test
//    public void registerUser_user_valid() throws Exception {
//        var req = this.newUserRequest();
//        var expected = UserInfo.builder().user(null).lastName(req.getLastName()).firstName(req.getFirstName())
//            .coins(2000L).gender(Gender.getByGenderId(req.getGenderId())).dob(req.getDob())
//            .build();
//        Mockito.when(userService.registerUser(req)).thenReturn(expected);
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(req)
//                .buildUserRequestWithContent(POST, "/v1/register-user-info"))
//            .andExpect(result -> {
//                var apiRes = jsonService.parseResponseJson(result.getResponse().getContentAsString(), UserInfo.class);
//
//                assertNotNull(result.getResponse().getContentAsString());
//                assertEquals(SucceedCodes.CREATE_USER_INFO.getCode(), apiRes.getApplicationCode());
//                assertEquals(expected, apiRes.getData());
//            });
//    }
//
//    @Test
//    public void registerUser_user_invalidPatternFirstName() throws Exception {
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("firstName", "Le Van1234")
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_invalidTypeFirstName() throws Exception {
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("firstName", 1234)
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_tooLongFirstName() throws Exception {
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("firstName", "abcdacsdfnasdfkjasdlfbalksjdfblaiusbfdjasblfibwaefblkdsajbladsudbf")
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_nullFirstName() throws Exception {
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("firstName", null)
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_tooShortFirstName() throws Exception {
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("firstName", "")
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_invalidPatternLastName() throws Exception {
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("lastName", "1234")
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_invalidTypeLastName() throws Exception {
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("lastName", 1234)
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_tooLongLastName() throws Exception {
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("lastName", "abcdacsdfnasdfkjasdlfbalksjdfblaiusbfdjasblfibwaefblkdsajbladsudbf")
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_nullLastName() throws Exception {
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("lastName", null)
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_tooShortLastName() throws Exception {
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("lastName", "")
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_nullDob() throws Exception{
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("dob", null)
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_invalidTypeDob() throws Exception{
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("dob", "abcd")
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_invalidConstraintDob() throws Exception{
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("dob", LocalDate.now().plusYears(this.minAge - 1))
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_nullGenderId() throws Exception{
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("genderId", null)
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_invalidTypeGenderId() throws Exception{
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("genderId", "abcd")
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.PARSE_JSON_ERR.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_invalidConstraintGenderId() throws Exception{
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("genderId", 2)
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_nullEmail() throws Exception{
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("email", null)
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
//    }
//
//    @Test
//    public void registerUser_user_invalidPatternEmail() throws Exception{
//        mockMvc
//            .perform(mockAuthRequestBuilders
//                .setContent(this.newUserRequest())
//                .replaceFieldOfContent("email", "levandung@")
//                .buildUserRequestWithContent(POST, "/v1/register-user-info")
//            )
//            .andExpect(jsonPath("applicationCode").value(ErrorCodes.VALIDATOR_ERR_RESPONSE.getCode()));
//    }
//}

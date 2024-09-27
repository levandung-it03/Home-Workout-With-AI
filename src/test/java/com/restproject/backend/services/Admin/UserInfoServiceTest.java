package com.restproject.backend.services.Admin;

import com.restproject.backend.dtos.request.NewUserInfoRequest;
import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.entities.UserInfo;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.Gender;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.helpers.MockAuthentication;
import com.restproject.backend.repositories.UserInfoRepository;
import com.restproject.backend.repositories.UserRepository;
import com.restproject.backend.services.UserInfoService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoServiceTest {
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    MockAuthentication mockAuthentication;

    @MockBean
    UserInfoRepository userInfoRepository;
    @MockBean
    UserRepository userRepository;

    @Test
    public void registerUserInfo_user_valid() {
        var token = "Bearer " + mockAuthentication.generateJwtTokens().get("user_accessToken");
        var req = NewUserInfoRequest.builder().firstName("Dung").lastName("Le Van").email("levandung.it03@gmail.com")
            .genderId(1).dob(LocalDate.of(2003, 12, 11)).build();
        var repoReq = UserInfo.builder()
            .firstName(req.getFirstName())
            .lastName(req.getLastName())
            .dob(req.getDob())
            .email(req.getEmail())
            .gender(Gender.getByGenderId(req.getGenderId()))
            .coins(2000L)
            .build();
        var expected = UserInfo.builder().user(null).lastName(req.getLastName()).firstName(req.getFirstName())
            .coins(2000L).gender(Gender.getByGenderId(req.getGenderId())).email(req.getEmail()).dob(req.getDob())
            .build();

        Mockito.when(userRepository.findByUsername(mockAuthentication.getUserUser().getUsername()))
            .thenReturn(Optional.of(mockAuthentication.getUserUser()));
        Mockito.when(userInfoRepository.save(Mockito.any(UserInfo.class))).thenReturn(repoReq);

        UserInfo actual = userInfoService.registerUserInfo(req, token);

        assertNotNull(actual);
        Mockito.verify(userRepository, Mockito.times(1))
            .findByUsername(mockAuthentication.getUserUser().getUsername());
        Mockito.verify(userInfoRepository, Mockito.times(1)).save(Mockito.any(UserInfo.class));
        assertEquals(expected, actual);
    }

    @Test
    public void getUserInfoAndStatusPages_admin_invalidSortedField() {
        var ftr = new HashMap<String, Object>();
        ftr.put("firstName", "Stre");
        var req = PaginatedTableRequest.builder().page(1).filterFields(ftr).sortedField("unknown").build();
        var exc = assertThrows(ApplicationException.class, () -> userInfoService
            .getUserInfoAndStatusPages(req));

        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_SORTING_FIELD_OR_VALUE);
    }

    @Test
    public void getUserInfoAndStatusPages_admin_invalidFilteringValues() {
        var ftr = new HashMap<String, Object>();
        ftr.put("genderId", 2);
        var req = PaginatedTableRequest.builder().page(1).filterFields(ftr).build();

        var exc = assertThrows(ApplicationException.class, () -> userInfoService
            .getUserInfoAndStatusPages(req));

        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
    }

    @Test
    public void getUserInfoAndStatusPages_admin_invalidFilteringFields() {
        var ftr = new HashMap<String, Object>();
        ftr.put("name", "Stre");
        var req = PaginatedTableRequest.builder().page(1).filterFields(ftr).build();

        var exc = assertThrows(ApplicationException.class, () -> userInfoService
            .getUserInfoAndStatusPages(req));

        assertEquals(exc.getErrorCodes(), ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
    }
}

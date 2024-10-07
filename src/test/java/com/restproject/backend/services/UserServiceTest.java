package com.restproject.backend.services;

import com.restproject.backend.dtos.request.NewUserRequest;
import com.restproject.backend.entities.Auth.Authority;
import com.restproject.backend.entities.Auth.User;
import com.restproject.backend.entities.UserInfo;
import com.restproject.backend.enums.Gender;
import com.restproject.backend.helpers.MockAuthentication;
import com.restproject.backend.repositories.AuthorityRepository;
import com.restproject.backend.repositories.UserInfoRepository;
import com.restproject.backend.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder userPasswordEncoder;

    @MockBean
    AuthorityRepository authorityRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    UserInfoRepository userInfoRepository;

    @Test
    public void registerUserInfo_user_valid() {
        var req = NewUserRequest.builder().firstName("Dung").lastName("Le Van").email("levandung.it03@gmail.com")
            .password("123456").genderId(1).dob(LocalDate.of(2003, 12, 11)).build();
        var newUserInfo = UserInfo.builder()
            .firstName(req.getFirstName())
            .lastName(req.getLastName())
            .dob(req.getDob())
            .gender(Gender.getByGenderId(req.getGenderId()))
            .coins(2000L)
            .build();
        var savedAuthority = Authority.builder().authorityName("ROLE_USER").build();
        var newUser = User.builder()
            .email(req.getEmail())
            .password(userPasswordEncoder.encode(req.getPassword()))
            .authorities(List.of(savedAuthority))
            .active(true).createdTime(LocalDateTime.now())
            .build();
        var expected = UserInfo.builder().user(newUser).lastName(req.getLastName()).firstName(req.getFirstName())
            .coins(2000L).gender(Gender.getByGenderId(req.getGenderId())).dob(req.getDob()).build();

        Mockito.when(authorityRepository.findByAuthorityName(savedAuthority.getAuthorityName()))
            .thenReturn(Optional.of(savedAuthority));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(newUser);
        Mockito.when(userInfoRepository.save(Mockito.any(UserInfo.class))).thenReturn(newUserInfo);

        UserInfo actual = userService.registerUser(req);

        assertNotNull(actual);
        Mockito.verify(authorityRepository, Mockito.times(1))
            .findByAuthorityName(savedAuthority.getAuthorityName());
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(userInfoRepository, Mockito.times(1)).save(Mockito.any(UserInfo.class));
        assertEquals(expected, actual);
    }

}

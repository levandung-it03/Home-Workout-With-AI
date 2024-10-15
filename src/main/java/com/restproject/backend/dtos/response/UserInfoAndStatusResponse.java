package com.restproject.backend.dtos.response;

import com.restproject.backend.dtos.request.UserInfoAndStatusRequest;
import com.restproject.backend.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoAndStatusResponse {
    Long userInfoId;
    String firstName;
    String lastName;
    Gender gender;
    String email;
    Long coins;
    LocalDate dob;
    Long userId;
    boolean isActive;
    LocalDateTime createdTime;
}

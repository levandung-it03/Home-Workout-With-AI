package com.restproject.backend.dtos.response;

import com.restproject.backend.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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
}

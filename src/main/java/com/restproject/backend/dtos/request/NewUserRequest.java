package com.restproject.backend.dtos.request;

import com.restproject.backend.annotations.constraint.DobConstraint;
import com.restproject.backend.annotations.constraint.GenderEnumConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {

    @NotNull
    @Pattern(regexp = "^[A-Za-zÀ-ỹ'\\-_]{1,50}( [A-Za-zÀ-ỹ'\\-_]{1,50})*$")
    String firstName;

    @NotNull
    @Pattern(regexp = "^[A-Za-zÀ-ỹ'\\-_]{1,50}( [A-Za-zÀ-ỹ'\\-_]{1,50})*$")
    String lastName;

    @NotNull
    @DobConstraint
    LocalDate dob;  //--Can receive Long type

    @NotNull
    @GenderEnumConstraint
    Integer genderId;

    @NotBlank
    @NotNull
    @Email
    String email;

    @NotBlank
    @NotNull
    @Length(min = 6)
    String password;

    @NotBlank
    @NotNull
    String otpCode;
}

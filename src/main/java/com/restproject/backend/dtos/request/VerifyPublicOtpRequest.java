package com.restproject.backend.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerifyPublicOtpRequest {
    @NotBlank
    @NotNull
    @Email
    String email;

    @NotBlank
    @NotNull
    String otpCode;
}

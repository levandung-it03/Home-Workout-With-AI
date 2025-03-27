package com.restproject.backend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Oauth2AuthorizationRequest {
    @NotBlank
    @NotNull
    String code;

    @NotBlank
    @NotNull
    String loginType;
}

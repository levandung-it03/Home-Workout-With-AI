package com.restproject.backend.dtos.general;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionInfoDto {
    @NotNull
    Long sessionId;

    @NotNull
    Long ordinal;
}

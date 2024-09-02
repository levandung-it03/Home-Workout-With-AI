package com.restproject.backend.dtos.request;

import com.restproject.backend.annotations.constraint.LevelEnumConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionsByLevelRequest {
    @NotNull(message = "ErrorCodes.INVALID_LEVEL")
    @LevelEnumConstraint(message = "ErrorCodes.INVALID_LEVEL")
    int level;
}

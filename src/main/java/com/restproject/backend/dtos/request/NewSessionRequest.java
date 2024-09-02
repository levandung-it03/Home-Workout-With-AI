package com.restproject.backend.dtos.request;

import com.restproject.backend.annotations.constraint.LevelEnumConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewSessionRequest {
    @NotBlank(message = "ErrorCodes.BLANK_NAME")
    String name;

    @NotBlank(message = "ErrorCodes.BLANK_DESCRIPTION")
    String description;

    @NotBlank(message = "ErrorCodes.BLANK_MUSCLE_LIST")
    String muscleList;

    @NotNull(message = "ErrorCodes.INVALID_LEVEL")
    @LevelEnumConstraint(message = "ErrorCodes.INVALID_LEVEL")
    Integer level;

    @NotEmpty(message = "ErrorCodes.INVALID_IDS_COLLECTION")
    Collection<Long> exerciseIds;
}

package com.restproject.backend.dtos.request;

import com.restproject.backend.annotations.constraint.LevelEnumConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExercisesByLevelAndMusclesRequest {
    @NotNull(message = "ErrorCodes.INVALID_LEVEL")
    @LevelEnumConstraint(message = "ErrorCodes.INVALID_LEVEL")
    int level;

    @NotBlank(message = "ErrorCodes.INVALID_IDS_COLLECTION")
    Collection<Integer> muscleIds;
}

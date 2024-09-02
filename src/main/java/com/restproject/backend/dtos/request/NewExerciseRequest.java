package com.restproject.backend.dtos.request;

import com.restproject.backend.annotations.constraint.LevelEnumConstraint;
import com.restproject.backend.enums.Muscle;
import jakarta.validation.constraints.Min;
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
public class NewExerciseRequest {
    @NotBlank(message = "ErrorCodes.BLANK_NAME")
    String name;

    @NotNull(message = "ErrorCodes.INVALID_LEVEL")
    @LevelEnumConstraint(message = "ErrorCodes.INVALID_LEVEL")
    Integer level;

    @NotNull(message = "ErrorCodes.INVALID_BASIC_REPS")
    @Min(value = 0, message = "ErrorCodes.INVALID_BASIC_REPS")
    Byte basicReps;

    @NotBlank(message = "ErrorCodes.INVALID_IDS_COLLECTION")
    Collection<Integer> muscleIds;
}

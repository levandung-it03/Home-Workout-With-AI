package com.restproject.backend.dtos.request;

import com.restproject.backend.annotations.constraint.LevelEnumConstraint;
import com.restproject.backend.annotations.constraint.ListTypeConstraint;
import com.restproject.backend.annotations.constraint.MuscleIdsEnumConstraint;
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
public class ExercisesByLevelAndMusclesRequest {
    @NotNull
    @LevelEnumConstraint
    Integer level;

    @NotEmpty
    @NotNull
    @ListTypeConstraint(type = Integer.class)
    @MuscleIdsEnumConstraint
    Collection<Integer> muscleIds;
}

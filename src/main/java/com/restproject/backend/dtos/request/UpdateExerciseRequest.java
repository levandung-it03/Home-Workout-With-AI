package com.restproject.backend.dtos.request;

import com.restproject.backend.annotations.constraint.LevelEnumConstraint;
import com.restproject.backend.annotations.constraint.ListTypeConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateExerciseRequest {
    @NotNull
    Long exerciseId;

    @NotBlank
    @Length(max = 100)
    String name;

    @NotNull
    @Min(0)
    @Max(9999)
    Integer basicReps;

    @NotNull
    @LevelEnumConstraint
    Integer level;

    @NotEmpty
    @NotNull
    @ListTypeConstraint(type = Long.class)
    Collection<Long> muscleIds;
}

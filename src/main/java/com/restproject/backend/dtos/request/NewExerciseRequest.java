package com.restproject.backend.dtos.request;

import com.restproject.backend.annotations.constraint.LevelEnumConstraint;
import com.restproject.backend.annotations.constraint.MuscleIdsEnumConstraint;
import com.restproject.backend.enums.Muscle;
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
public class NewExerciseRequest {

    @NotBlank
    @Length(max = 20)
    String name;

    @NotNull
    @LevelEnumConstraint
    Integer level;

    @NotNull
    @Min(0)
    @Max(9999)
    Integer basicReps;

    @NotEmpty
    @NotNull
    @MuscleIdsEnumConstraint
    Collection<Integer> muscleIds;
}

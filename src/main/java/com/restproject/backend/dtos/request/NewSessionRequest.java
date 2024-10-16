package com.restproject.backend.dtos.request;

import com.restproject.backend.annotations.constraint.LevelEnumConstraint;
import com.restproject.backend.annotations.constraint.ListTypeConstraint;
import com.restproject.backend.dtos.general.ExerciseInfoDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewSessionRequest {

    @NotBlank
    @Length(max = 100)
    String name;

    @NotBlank
    @Length(max = 200)
    String description;

    @NotEmpty
    @NotNull
    @ListTypeConstraint(type = Long.class)
    Collection<Long> muscleIds;

    @NotNull
    @LevelEnumConstraint
    Integer level;

    @NotNull
    Integer switchExerciseDelay;

    @NotEmpty
    @NotNull
    Collection<ExerciseInfoDto> exercisesInfo;
}

package com.restproject.backend.dtos.general;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseInfoDto {
    @NotNull
    Long exerciseId;

    @NotNull
    Integer ordinal;

    @NotNull
    Float downRepsRatio;

    @NotNull
    Integer slackInSecond;

    @NotNull
    Integer raiseSlackInSecond;

    @NotNull
    @Min(1)
    Integer iteration;

    @NotNull
    Boolean needSwitchExerciseDelay;
}
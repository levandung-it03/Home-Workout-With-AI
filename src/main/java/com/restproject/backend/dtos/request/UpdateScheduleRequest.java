package com.restproject.backend.dtos.request;

import com.restproject.backend.annotations.constraint.LevelEnumConstraint;
import com.restproject.backend.annotations.constraint.ListTypeConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateScheduleRequest {
    @NotNull
    Long scheduleId;

    @NotBlank
    @Length(max = 50)
    String name;

    @NotBlank
    @Length(max = 100)
    String description;

    @NotNull
    @LevelEnumConstraint
    Integer level;

    @NotNull
    Long coins;
}
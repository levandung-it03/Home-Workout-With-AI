package com.restproject.backend.dtos.request;

import com.restproject.backend.annotations.constraint.ListTypeConstraint;
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
public class UpdateExercisesOfSessionRequest {

    @NotNull
    Long sessionId;

    @NotEmpty
    @NotNull
    @ListTypeConstraint(type = Long.class)
    Collection<Long> exerciseIds;
}
package com.restproject.backend.dtos.request;

import com.restproject.backend.annotations.constraint.LevelEnumConstraint;
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
    @Length(max = 20)
    String name;

    @NotBlank
    String description;

    @NotBlank
    String muscleList;

    @NotNull
    @LevelEnumConstraint
    Integer level;

    @NotEmpty
    Collection<Long> exerciseIds;
}

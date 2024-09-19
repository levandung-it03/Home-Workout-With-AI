package com.restproject.backend.dtos.request;

import com.restproject.backend.annotations.constraint.LevelEnumConstraint;
import com.restproject.backend.annotations.constraint.ListTypeConstraint;
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
public class NewScheduleRequest {

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

    @NotNull
    @NotEmpty
    @ListTypeConstraint(type = Long.class)
    Collection<Long> sessionIds;
}

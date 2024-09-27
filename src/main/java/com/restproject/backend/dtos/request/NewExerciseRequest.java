package com.restproject.backend.dtos.request;

import com.restproject.backend.annotations.constraint.LevelEnumConstraint;
import com.restproject.backend.annotations.constraint.ListTypeConstraint;
import com.restproject.backend.annotations.constraint.MuscleIdsEnumConstraint;
import com.restproject.backend.enums.Muscle;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewExerciseRequest {

    @NotBlank
    @Length(max = 30)
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
    @ListTypeConstraint(type = Integer.class)
    @MuscleIdsEnumConstraint
    Collection<Integer> muscleIds;
}

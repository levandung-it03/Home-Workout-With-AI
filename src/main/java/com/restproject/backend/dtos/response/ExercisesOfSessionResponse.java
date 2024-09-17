package com.restproject.backend.dtos.response;

import com.restproject.backend.annotations.dev.Constructors;
import com.restproject.backend.annotations.dev.Overload;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.enums.Muscle;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExercisesOfSessionResponse {
    Exercise exercise;
    List<Muscle> muscleList;
    boolean withCurrentSession;

    @Constructors
    @Overload
    public ExercisesOfSessionResponse(Exercise exercise, String muscleListFromRepo, boolean withCurrentSession) {
        this.exercise = exercise;
        this.withCurrentSession = withCurrentSession;
        this.muscleList = Arrays.stream(muscleListFromRepo.split(",")).map(Muscle::valueOf).toList();
    }
}

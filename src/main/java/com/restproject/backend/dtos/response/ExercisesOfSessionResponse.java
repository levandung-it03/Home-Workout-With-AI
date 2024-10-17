package com.restproject.backend.dtos.response;

import com.restproject.backend.dtos.general.ObjectDto;
import com.restproject.backend.entities.Exercise;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExercisesOfSessionResponse {
    Exercise exercise;
    Boolean withCurrentSession;
    Integer ordinal;
    Float downRepsRatio;
    Integer slackInSecond;
    Integer raiseSlackInSecond;
    Integer iteration;
    Boolean needSwitchExerciseDelay;

    public ExercisesOfSessionResponse(Exercise exercise, Long sessionId, Long sessionIdInput, Integer ordinal,
                                      Float downRepsRatio, Integer slackInSecond, Integer raiseSlackInSecond,
                                      Integer iteration, Boolean needSwitchExerciseDelay) {
        this.exercise = exercise;
        this.withCurrentSession = sessionId.equals(sessionIdInput);
        this.ordinal = ordinal;
        this.downRepsRatio = downRepsRatio;
        this.slackInSecond = slackInSecond;
        this.raiseSlackInSecond = raiseSlackInSecond;
        this.iteration = iteration;
        this.needSwitchExerciseDelay = needSwitchExerciseDelay;
    }
}
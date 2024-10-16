package com.restproject.backend.dtos.response;

import com.restproject.backend.dtos.general.ObjectDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExercisesOfSessionResponse {
    Object exerciseId;
    Object name;
    Object basicReps;
    Object levelEnum;
    Object muscles;
    Object withCurrentSession;
    Object ordinal;
    Object downRepsRatio;
    Object slackInSecond;
    Object raiseSlackInSecond;
    Object iteration;
    Object needSwitchExerciseDelay;


    public static ExercisesOfSessionResponse buildFromQuery(Object[] params) {
        var res = new ExercisesOfSessionResponse();
        ObjectDto.mappingValues(res, params);
        return res;
    }
}
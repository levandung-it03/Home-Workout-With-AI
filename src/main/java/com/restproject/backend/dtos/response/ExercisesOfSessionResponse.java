package com.restproject.backend.dtos.response;

import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.MusclesOfExercisesRepository;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExercisesOfSessionResponse {
    Long exerciseId;
    String name;
    Integer basicReps;
    Level levelEnum;
    boolean withCurrentSession;
    Integer ordinal;
    Float downRepsRatio;
    Integer slackInSecond;
    Integer raiseSlackInSecond;
    Integer iteration;
    Boolean needSwitchExerciseDelay;

    public static ExercisesOfSessionResponse buildFromNativeQuery(Object[] params) {
        return ExercisesOfSessionResponse.builder()
            .exerciseId(Long.parseLong(params[0].toString()))
            .name(params[1].toString())
            .basicReps(Integer.parseInt(params[2].toString()))
            .levelEnum(Level.valueOf(params[3].toString()))
            .withCurrentSession(!Objects.isNull(params[4]) && params[4].toString().equals("1"))
            .ordinal(Integer.parseInt(params[5].toString()))
            .downRepsRatio(Float.parseFloat(params[6].toString()))
            .slackInSecond(Integer.parseInt(params[7].toString()))
            .raiseSlackInSecond(Integer.parseInt(params[8].toString()))
            .iteration(Integer.parseInt(params[9].toString()))
            .needSwitchExerciseDelay(Boolean.parseBoolean(params[10].toString()))
            .build();
    }

    public static ExercisesOfSessionResponse buildFromHashMap(HashMap<String, Object> map)
        throws ApplicationException, IllegalArgumentException, NullPointerException, NoSuchFieldException {
        for (String key : map.keySet()) {
            if (key.equals("level"))    continue;
            if (Arrays.stream(ExercisesOfSessionResponse.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key))) throw new NoSuchFieldException();
        }

        var exerciseInfo = new ExercisesOfSessionResponse();
        exerciseInfo.setName(!map.containsKey("name") ? null : map.get("name").toString());
        exerciseInfo.setBasicReps(!map.containsKey("basicReps") ? null
            : Integer.parseInt(map.get("basicReps").toString()));
        exerciseInfo.setLevelEnum(!map.containsKey("level") ? null : Level.getByLevel(map.get("level")));
        exerciseInfo.setOrdinal(!map.containsKey("ordinal") ? null
            : Integer.parseInt(map.get("ordinal").toString()));
        exerciseInfo.setDownRepsRatio(!map.containsKey("downRepsRatio") ? null
            : Float.parseFloat(map.get("downRepsRatio").toString()));
        exerciseInfo.setSlackInSecond(!map.containsKey("slackInSecond") ? null
            : Integer.parseInt(map.get("slackInSecond").toString()));
        exerciseInfo.setRaiseSlackInSecond(!map.containsKey("raiseSlackInSecond") ? null
            : Integer.parseInt(map.get("raiseSlackInSecond").toString()));
        exerciseInfo.setIteration(!map.containsKey("iteration") ? null
            : Integer.parseInt(map.get("iteration").toString()));
        exerciseInfo.setNeedSwitchExerciseDelay(!map.containsKey("needSwitchExerciseDelay") ? null
            : Boolean.parseBoolean(map.get("needSwitchExerciseDelay").toString()));
        return exerciseInfo;
    }
}

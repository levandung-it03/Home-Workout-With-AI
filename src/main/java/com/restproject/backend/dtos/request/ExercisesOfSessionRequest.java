package com.restproject.backend.dtos.request;

import com.restproject.backend.entities.Muscle;
import com.restproject.backend.enums.Level;
import com.restproject.backend.exceptions.ApplicationException;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExercisesOfSessionRequest {
    Long exerciseId;
    String name;
    Integer basicReps;
    Level levelEnum;
    List<Long> muscleIds;
    Boolean withCurrentSession;

    public static ExercisesOfSessionRequest buildFromHashMap(HashMap<String, Object> map)
        throws ApplicationException, IllegalArgumentException, NullPointerException, NoSuchFieldException {
        for (String key : map.keySet()) {
            if (key.equals("level"))    continue;
            if (Arrays.stream(ExercisesOfSessionRequest.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key))) throw new NoSuchFieldException();
        }
        var exerciseInfo = new ExercisesOfSessionRequest();
        exerciseInfo.setName(!map.containsKey("name") ? null : map.get("name").toString());
        exerciseInfo.setBasicReps(!map.containsKey("basicReps") ? null
            : Integer.parseInt(map.get("basicReps").toString()));
        exerciseInfo.setLevelEnum(!map.containsKey("level") ? null : Level.getByLevel(map.get("level")));
        exerciseInfo.setMuscleIds(!map.containsKey("muscleIds") ? null : Muscle.parseStrIdsToList(map.get("muscleIds")));
        exerciseInfo.setWithCurrentSession(!map.containsKey("withCurrentSession") ? null
            : Boolean.parseBoolean(map.get("withCurrentSession").toString()));
        return exerciseInfo;
    }
}

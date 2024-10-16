package com.restproject.backend.dtos.request;

import com.restproject.backend.entities.Muscle;
import com.restproject.backend.enums.Level;
import com.restproject.backend.exceptions.ApplicationException;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExercisePagesRequest {
    String name;
    Level levelEnum;
    Integer basicReps;
    String imageUrl;
    List<Long> muscleIds;

    public static ExercisePagesRequest buildFromHashMap(HashMap<String, Object> map)
        throws ApplicationException, IllegalArgumentException, NullPointerException, NoSuchFieldException {
        for (String key : map.keySet()) {
            if (key.equals("level"))    continue;
            if (Arrays.stream(ExercisePagesRequest.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key))) throw new NoSuchFieldException();
        }
        var exerciseInfo = new ExercisePagesRequest();
        exerciseInfo.setName(!map.containsKey("name") ? null : map.get("name").toString());
        exerciseInfo.setLevelEnum(!map.containsKey("level") ? null : Level.getByLevel(map.get("level").toString()));
        exerciseInfo.setBasicReps(!map.containsKey("basicReps") ? null
            : Integer.parseInt(map.get("basicReps").toString()));
        exerciseInfo.setMuscleIds(!map.containsKey("muscleIds") ? null : Muscle.parseStrIdsToList(map.get("muscleIds")));
        return exerciseInfo;
    }
}

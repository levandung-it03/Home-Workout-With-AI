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
public class SessionsOfScheduleRequest {
    Long sessionId;
    String name;
    Level levelEnum;
    List<Long> muscleIds;
    boolean withCurrentSchedule;
    Integer switchExerciseDelay;

    public static SessionsOfScheduleRequest buildFromHashMap(HashMap<String, Object> map)
        throws ApplicationException, IllegalArgumentException, NullPointerException, NoSuchFieldException {
        for (String key : map.keySet()) {
            if (key.equals("level"))    continue;
            if (Arrays.stream(SessionsOfScheduleRequest.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key))) throw new NoSuchFieldException();
        }

        var sessionInfo = new SessionsOfScheduleRequest();
        sessionInfo.setName(!map.containsKey("name") ? null : map.get("name").toString());
        sessionInfo.setLevelEnum(!map.containsKey("level") ? null : Level.getByLevel(map.containsKey("level")));
        sessionInfo.setMuscleIds(!map.containsKey("muscleIds") ? null
            : Muscle.parseStrIdsToList(map.containsKey("muscleIds")));
        sessionInfo.setSwitchExerciseDelay(!map.containsKey("switchExerciseDelay") ? null
            : Integer.parseInt(map.get("switchExerciseDelay").toString()));
        return sessionInfo;
    }
}

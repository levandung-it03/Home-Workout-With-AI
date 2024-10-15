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
public class SessionsOfScheduleResponse {
    Long sessionId;
    String name;
    Level levelEnum;
    boolean withCurrentSchedule;
    Integer switchExerciseDelay;
    Integer ordinal;

    public static SessionsOfScheduleResponse buildFromNativeQuery(Object[] params) {
        return SessionsOfScheduleResponse.builder()
            .sessionId(Long.parseLong(params[0].toString()))
            .name(params[1].toString())
            .levelEnum(Level.valueOf(params[2].toString()))
            .withCurrentSchedule(!Objects.isNull(params[3]) && params[3].toString().equals("1"))
            .switchExerciseDelay(Integer.parseInt(params[4].toString()))
            .ordinal(Integer.parseInt(params[5].toString()))
            .build();
    }

    public static SessionsOfScheduleResponse buildFromHashMap(HashMap<String, Object> map)
        throws ApplicationException, IllegalArgumentException, NullPointerException, NoSuchFieldException {
        for (String key : map.keySet()) {
            if (key.equals("level"))    continue;
            if (Arrays.stream(SessionsOfScheduleResponse.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key))) throw new NoSuchFieldException();
        }

        var sessionInfo = new SessionsOfScheduleResponse();
        sessionInfo.setName(!map.containsKey("name") ? null : map.get("name").toString());
        sessionInfo.setLevelEnum(!map.containsKey("level") ? null : Level.getByLevel(map.containsKey("level")));
        sessionInfo.setOrdinal(!map.containsKey("ordinal") ? null : Integer.parseInt(map.get("ordinal").toString()));
        sessionInfo.setOrdinal(!map.containsKey("switchExerciseDelay") ? null
            : Integer.parseInt(map.get("switchExerciseDelay").toString()));
        return sessionInfo;
    }
}

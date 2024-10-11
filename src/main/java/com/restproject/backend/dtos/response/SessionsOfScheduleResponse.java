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
    String levelEnum;
    String description;
    boolean withCurrentSchedule;
    List<String> muscleList;    //--Keep MuscleEnum as String to make filter works correctly.
    Integer ordinal;


    public static SessionsOfScheduleResponse buildFromNativeQuery(Object[] params) {
        return SessionsOfScheduleResponse.builder()
            .sessionId(Long.parseLong(params[0].toString()))
            .name(params[1].toString())
            .levelEnum(params[2].toString())
            .description(params[3].toString())
            .withCurrentSchedule(!Objects.isNull(params[4]) && params[4].toString().equals("1"))
            .muscleList(Arrays.stream(params[5].toString()
                .replaceAll("[\\[\\]]", "")
                .split(String.valueOf(MusclesOfExercisesRepository.GROUP_CONCAT_SEPARATOR))
            ).toList())
            .ordinal(Integer.parseInt(params[6].toString()))
            .build();
    }

    public static SessionsOfScheduleResponse buildFromHashMap(HashMap<String, Object> map)
        throws ApplicationException, IllegalArgumentException, NullPointerException, NoSuchFieldException {
        for (String key : map.keySet())
            SessionsOfScheduleResponse.class.getDeclaredField(key); //--Ignored value.

        var sessionInfo = new SessionsOfScheduleResponse();
        sessionInfo.setMuscleList(!map.containsKey("muscleIds") ? new ArrayList<>()   //--May throw IllegalArgExc
            : Muscle.parseAllMuscleIdsArrToRaw(map.get("muscleIds")));
        sessionInfo.setName(!map.containsKey("name") ? null : map.get("name").toString());
        sessionInfo.setDescription(!map.containsKey("description") ? null : map.get("description").toString());
        sessionInfo.setLevelEnum(!map.containsKey("level") ? null
            : Level.getByLevel(Integer.parseInt(map.get("level").toString())).toString()); //--May throw AppExc
        sessionInfo.setOrdinal(!map.containsKey("ordinal") ? null : Integer.parseInt(map.get("ordinal").toString()));
        return sessionInfo;
    }
}

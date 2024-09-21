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
    Level level;
    String description;
    List<String> muscleList;    //--Keep MuscleEnum as String to make filter works correctly.
    boolean withCurrentSchedule;


    public static SessionsOfScheduleResponse buildFromNativeQuery(Object[] params) {
        return SessionsOfScheduleResponse.builder()
            .sessionId(Long.parseLong(params[0].toString()))
            .name(params[1].toString())
            .level(Level.valueOf(params[2].toString()))
            .description(params[3].toString())
            .withCurrentSchedule(!Objects.isNull(params[4]) && params[4].toString().equals("1"))
            .muscleList(Arrays.stream(params[5].toString()
                .replaceAll("[\\[\\]]", "")
                .split(String.valueOf(MusclesOfExercisesRepository.GROUP_CONCAT_SEPARATOR))
            ).toList())
            .build();
    }

    public static SessionsOfScheduleResponse buildFromHashMap(HashMap<String, Object> map)
        throws ApplicationException, IllegalArgumentException, NullPointerException, NoSuchFieldException {
        for (String key : map.keySet())
            SessionsOfScheduleResponse.class.getDeclaredField(key); //--Ignored value.

        var sessionInfo = new SessionsOfScheduleResponse();
        sessionInfo.setMuscleList(!map.containsKey("muscleList") ? new ArrayList<>()   //--May throw IllegalArgExc
            : Arrays.stream(map.get("muscleList").toString()
            .replaceAll("[\\[\\]]", "").split(",")
        ).map(id -> Muscle.getById(id).toString()).toList());
        sessionInfo.setName(!map.containsKey("name") ? null : map.get("name").toString());
        sessionInfo.setDescription(!map.containsKey("description") ? null : map.get("description").toString());
        sessionInfo.setLevel(!map.containsKey("level") ? null
            : Level.getByLevel(Integer.parseInt(map.get("level").toString()))); //--May throw AppExc
        return sessionInfo;
    }
}

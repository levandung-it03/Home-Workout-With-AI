package com.restproject.backend.dtos.response;

import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.MusclesOfSessionsRepository;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionHasMusclesResponse {
    Long sessionId;
    String name;
    Level level;
    String description;
    List<String> muscleList;    //--Keep MuscleEnum as String to make filter works correctly.

    public static SessionHasMusclesResponse buildFromNativeQuery(Object[] params) {
        return SessionHasMusclesResponse.builder()
            .sessionId(Long.parseLong(params[0].toString()))
            .name(params[1].toString())
            .description(params[2].toString())
            .level(Level.valueOf(params[3].toString()))
            .muscleList(
                Arrays.stream(params[4].toString()
                    .replaceAll("[\\[\\]]", "")
                    .split(String.valueOf(MusclesOfSessionsRepository.GROUP_CONCAT_SEPARATOR))
                ).map(m -> Muscle.valueOf(m.toUpperCase().trim()).toString()).toList())
            .build();
    }

    public static SessionHasMusclesResponse buildFromHashMap(HashMap<String, Object> map)
        throws ApplicationException, IllegalArgumentException, NullPointerException, NoSuchFieldException {
        for (String key: map.keySet())
            SessionHasMusclesResponse.class.getDeclaredField(key); //--Ignored value.

        var sessionInfo = new SessionHasMusclesResponse();
        sessionInfo.setName(!map.containsKey("name") ? null : map.get("name").toString());
        sessionInfo.setLevel(!map.containsKey("level") ? null
            : Level.getByLevel(Integer.parseInt(map.get("level").toString()))); //--May throw AppExc
        sessionInfo.setDescription(!map.containsKey("description") ? null : map.get("description").toString());
        sessionInfo.setMuscleList(!map.containsKey("muscleList") ? new ArrayList<>()   //--May throw IllegalArgExc
            : Arrays.stream(map.get("muscleList").toString()
            .replaceAll("[\\[\\]]", "").split(",")
        ).map(id -> Muscle.getById(id).toString()).toList());
        return sessionInfo;
    }

    public SessionHasMusclesResponse injectMuscleList(List<String> muscleList) {
        this.muscleList = muscleList;
        return this;
    }
}
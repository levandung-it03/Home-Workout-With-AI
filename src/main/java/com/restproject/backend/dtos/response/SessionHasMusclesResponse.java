package com.restproject.backend.dtos.response;

import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.MusclesOfSessionsRepository;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String levelEnum;   //--Keep String type to make filter work correctly
    String description;
    List<String> muscleList;    //--Keep MuscleEnum as String to make filter works correctly.

    public static SessionHasMusclesResponse buildFromNativeQuery(Object[] params) {
        return SessionHasMusclesResponse.builder()
            .sessionId(Long.parseLong(params[0].toString()))
            .name(params[1].toString())
            .description(params[2].toString())
            .levelEnum(params[3].toString())
            .muscleList(Arrays.stream(params[4].toString()
                .replaceAll("[\\[\\]]", "")
                .split(String.valueOf(MusclesOfSessionsRepository.GROUP_CONCAT_SEPARATOR))
            ).toList())
            .build();
    }

    public static SessionHasMusclesResponse buildFromHashMap(HashMap<String, Object> map)
        throws ApplicationException, IllegalArgumentException, NullPointerException, NoSuchFieldException {
        for (String key : map.keySet()) {
            if (key.equals("muscleIds"))    continue;
            if (Arrays.stream(SessionHasMusclesResponse.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key))) throw new NoSuchFieldException(); //--Ignored value.
        }

        var sessionInfo = new SessionHasMusclesResponse();
        sessionInfo.setName(!map.containsKey("name") ? null : map.get("name").toString());
        sessionInfo.setLevelEnum(!map.containsKey("level") ? null : Level.getRawLevelByLevel(map.get("level")));
        sessionInfo.setDescription(!map.containsKey("description") ? null : map.get("description").toString());
        sessionInfo.setMuscleList(!map.containsKey("muscleIds") ? new ArrayList<>()   //--May throw IllegalArgExc
            : Muscle.parseAllMuscleIdsArrToRaw(map.get("muscleIds")));
        return sessionInfo;
    }

    public SessionHasMusclesResponse injectMuscleList(List<String> muscleList) {
        this.muscleList = muscleList;
        return this;
    }
}

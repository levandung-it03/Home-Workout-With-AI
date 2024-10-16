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
public class SessionPagesRequest {
    String name;
    Level levelEnum;
    String description;
    List<Long> muscleIds;
    Integer switchExerciseDelay;

    public static SessionPagesRequest buildFromHashMap(HashMap<String, Object> map)
        throws ApplicationException, IllegalArgumentException, NullPointerException, NoSuchFieldException {
        for (String key : map.keySet()) {
            if (key.equals("level"))    continue;
            if (Arrays.stream(SessionPagesRequest.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key))) throw new NoSuchFieldException();
        }
        var sessionRequest = new SessionPagesRequest();
        sessionRequest.setName(!map.containsKey("name") ? null : map.get("name").toString());
        sessionRequest.setLevelEnum(!map.containsKey("level") ? null : Level.getByLevel(map.get("level").toString()));
        sessionRequest.setDescription(!map.containsKey("description") ? null : map.get("description").toString());
        sessionRequest.setMuscleIds(!map.containsKey("muscleIds") ? null
            : Muscle.parseStrIdsToList(map.get("muscleIds")));
        return sessionRequest;
    }
}

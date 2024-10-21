package com.restproject.backend.dtos.request;

import com.restproject.backend.entities.Schedule;
import com.restproject.backend.enums.Level;
import com.restproject.backend.exceptions.ApplicationException;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleRequest {
    Long scheduleId;
    String name;
    String description;
    Level levelEnum;
    Long fromCoins;
    Long toCoins;

    public static ScheduleRequest buildFromHashMap(HashMap<String, Object> map)
        throws NullPointerException, ApplicationException, IllegalArgumentException, NoSuchFieldException {
        for (String key: map.keySet()) {
            if (key.equals("level"))    continue;
            if (Arrays.stream(ScheduleRequest.class.getDeclaredFields()).noneMatch(f -> f.getName().equals(key)))
                throw new NoSuchFieldException();
        }

        var result = new ScheduleRequest();
        result.setName(!map.containsKey("name") ? null : map.get("name").toString());
        result.setLevelEnum(!map.containsKey("level") ? null : Level.getByLevel(map.get("level")));
        result.setDescription(!map.containsKey("description") ? null : map.get("description").toString());
        result.setFromCoins(!map.containsKey("fromCoins") ? null : Long.parseLong(map.get("fromCoins").toString()));
        result.setToCoins(!map.containsKey("fromCoins") ? null : Long.parseLong(map.get("fromCoins").toString()));
        return result;
    }
}

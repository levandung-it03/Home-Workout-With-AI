package com.restproject.backend.dtos.response;

import com.restproject.backend.dtos.general.ObjectDto;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionsOfScheduleResponse {
    Object sessionId;
    Object name;
    Object levelEnum;
    Object muscles;
    Object switchExerciseDelay;
    Object ordinal;
    Object withCurrentSchedule;

    public static SessionsOfScheduleResponse buildFromQuery(Object[] params) {
        var res = new SessionsOfScheduleResponse();
        ObjectDto.mappingValues(res, params);
        return res;
    }
}

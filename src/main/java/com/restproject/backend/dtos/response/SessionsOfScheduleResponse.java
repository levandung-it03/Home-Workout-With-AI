package com.restproject.backend.dtos.response;

import com.restproject.backend.entities.Session;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionsOfScheduleResponse {
    Session session;
    Boolean withCurrentSchedule;
    Long ordinal;

    public SessionsOfScheduleResponse(Session session, Long scheduleId, Long scheduleIdInput, Long ordinal) {
        this.session = session;
        this.ordinal = ordinal;
        this.withCurrentSchedule = scheduleId.equals(scheduleIdInput);
    }
}

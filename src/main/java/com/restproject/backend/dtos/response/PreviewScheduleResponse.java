package com.restproject.backend.dtos.response;

import com.restproject.backend.entities.Schedule;
import com.restproject.backend.entities.Session;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PreviewScheduleResponse {
    Schedule schedule;
    int totalSessions;
    boolean wasSubscribed;
    Set<PreviewSession> sessionsOfSchedules;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PreviewSession {
        Session session;
        Set<String> exerciseNames;

        @Override
        public boolean equals(Object o) {
            if (this == o)  return true;
            if (o == null || this.getClass() != o.getClass())   return false;
            PreviewSession that = (PreviewSession) o;
            return Objects.equals(session.getSessionId(), that.session.getSessionId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(session.getSessionId());
        }
    }
}

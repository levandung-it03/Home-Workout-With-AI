package com.restproject.backend.dtos.response;

import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.ExercisesOfSessions;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.entities.Session;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PreviewFullScheduleResponse {
    Schedule schedule;
    int totalSessions;
    List<PreviewSession> sessionsOfSchedules;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PreviewSession {
        Session session;
        int ordinal;
        List<ExercisesOfSessions> exercisesOfSessions;
    }
}

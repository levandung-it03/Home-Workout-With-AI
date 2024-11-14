package com.restproject.backend.dtos.response;

import com.restproject.backend.entities.Schedule;
import com.restproject.backend.entities.SessionsOfSchedules;
import com.restproject.backend.entities.Subscription;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PreviewSubscribedScheduleResponse {
    Schedule schedule;
    Subscription subscription;
    Double TDEE;
    List<SessionsOfSchedules> sessions;
}

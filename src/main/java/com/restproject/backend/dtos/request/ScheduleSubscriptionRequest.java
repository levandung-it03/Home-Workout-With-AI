package com.restproject.backend.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleSubscriptionRequest {
    @NotNull
    Integer aimType;

    @NotNull
    Byte repRatio;   //--Receive values: [100, 90, 80]

    @NotNull
    Long scheduleId;
}

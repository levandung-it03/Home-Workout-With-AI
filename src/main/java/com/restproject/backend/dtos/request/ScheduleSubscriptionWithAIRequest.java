package com.restproject.backend.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleSubscriptionWithAIRequest {
    @NotNull
    Integer aimType;   //--Receive values: [1, 0, -1]

    @NotNull
    Byte repRatio;  //--Receive values: [100, 90, 80]

    @NotNull
    Float weight;

    @NotNull
    Long scheduleId;

    @NotNull
    Long bodyFat;

    Long aimRatio;  //--Receive values: [15, 10, 5, null, -10, -20, -30, -40]
    Float weightAimByDiet;
}

package com.restproject.backend.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateSubscribedScheduleRepRatioRequest {
    @NotNull
    Byte newRepRatio;

    @NotNull
    Long scheduleId;
}

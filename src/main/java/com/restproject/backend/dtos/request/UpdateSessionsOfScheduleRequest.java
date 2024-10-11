package com.restproject.backend.dtos.request;

import com.restproject.backend.dtos.general.SessionInfoDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateSessionsOfScheduleRequest {

    @NotNull
    Long scheduleId;

    @NotEmpty
    @NotNull
    Collection<SessionInfoDto> sessionsInfo;
}

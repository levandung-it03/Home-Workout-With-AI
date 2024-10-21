package com.restproject.backend.dtos.response;

import com.restproject.backend.entities.Session;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionToPerformResponse {
    Session session;
    Byte repRatio;
}

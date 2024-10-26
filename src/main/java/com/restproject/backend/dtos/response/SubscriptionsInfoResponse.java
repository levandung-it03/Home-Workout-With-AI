package com.restproject.backend.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionsInfoResponse {
    Long subscriptionId;
    String firstName;
    String lastName;
    LocalDateTime subscribedTime;
    Integer efficientDays;
    String scheduleName;
    String scheduleLevelEnum;
    Long scheduleCoins;
    LocalDateTime completedTime;

    public static SubscriptionsInfoResponse buildFromNativeQuery(Object[] params) {
        return SubscriptionsInfoResponse.builder()
            .subscriptionId(Long.parseLong(params[0].toString()))
            .firstName(params[1].toString())
            .lastName(params[2].toString())
            .subscribedTime(stringToLocalTime(params[3]))
            .efficientDays(Objects.isNull(params[4]) ? null : Integer.parseInt(params[4].toString()))
            .scheduleName(params[5].toString())
            .scheduleLevelEnum(params[6].toString())
            .scheduleCoins(Long.parseLong(params[7].toString()))
            .completedTime(stringToLocalTime(params[8]))
            .build();
    }

    public static LocalDateTime stringToLocalTime(Object time) {
        if (Objects.isNull(time))   return null;
        return LocalDateTime.parse(time.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}

package com.restproject.backend.dtos.request;

import com.restproject.backend.dtos.response.SubscriptionsInfoResponse;
import com.restproject.backend.enums.Level;
import com.restproject.backend.exceptions.ApplicationException;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionsInfoRequest {
    String firstName;
    String lastName;
    LocalDateTime fromSubscribedTime;
    LocalDateTime toSubscribedTime;
    Integer efficientDays;
    String scheduleName;
    Long fromCoins;
    Long toCoins;
    LocalDateTime fromCompletedTime;
    LocalDateTime toCompletedTime;

    public static SubscriptionsInfoRequest buildFromHashMap(HashMap<String, Object> map)
        throws ApplicationException, IllegalArgumentException, NullPointerException, NoSuchFieldException {
        for (String key : map.keySet())
            if (Arrays.stream(SubscriptionsInfoRequest.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key))) throw new NoSuchFieldException();

        var subscriptionInfo = new SubscriptionsInfoRequest();
        subscriptionInfo.setFirstName(!map.containsKey("firstName") ? null : map.get("firstName").toString());
        subscriptionInfo.setLastName(!map.containsKey("lastName") ? null : map.get("lastName").toString());
        subscriptionInfo.setFromSubscribedTime(!map.containsKey("fromSubscribedTime") ? null
            : SubscriptionsInfoResponse.stringToLocalTime(map.containsKey("fromSubscribedTime")));
        subscriptionInfo.setToCompletedTime(!map.containsKey("toSubscribedTime") ? null
            : SubscriptionsInfoResponse.stringToLocalTime(map.containsKey("toSubscribedTime")));
        subscriptionInfo.setEfficientDays(!map.containsKey("efficientDays") ? null
            : Integer.parseInt(map.get("efficientDays").toString()));
        subscriptionInfo.setScheduleName(!map.containsKey("scheduleName") ? null
            : map.get("scheduleName").toString());
        subscriptionInfo.setFromCoins(!map.containsKey("fromCoins") ? null
            : Long.parseLong(map.get("fromCoins").toString()));
        subscriptionInfo.setToCoins(!map.containsKey("toCoins") ? null
            : Long.parseLong(map.get("toCoins").toString()));
        subscriptionInfo.setFromCompletedTime(!map.containsKey("fromCompletedTime") ? null
            : SubscriptionsInfoResponse.stringToLocalTime(map.containsKey("fromCompletedTime")));
        subscriptionInfo.setToCompletedTime(!map.containsKey("toCompletedTime") ? null
            : SubscriptionsInfoResponse.stringToLocalTime(map.containsKey("toCompletedTime")));
        return subscriptionInfo;
    }
}

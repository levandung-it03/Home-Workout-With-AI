package com.restproject.backend.dtos.request;

import com.restproject.backend.dtos.response.SubscriptionsInfoResponse;
import com.restproject.backend.enums.ChangingCoinsType;
import com.restproject.backend.exceptions.ApplicationException;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FullChangingCoinsRequest {
    String changingCoinsHistoriesId;
    Long changingCoins;
    ChangingCoinsType changingCoinsType;
    String fullName;
    LocalDateTime fromChangingTime;
    LocalDateTime toChangingTime;


    public static FullChangingCoinsRequest buildFromHashMap(HashMap<String, Object> map)
        throws NullPointerException, ApplicationException, IllegalArgumentException, NoSuchFieldException {
        var result = new FullChangingCoinsRequest();
        result.setFullName(!map.containsKey("fullName") ? null : map.get("fullName").toString());
        result.setChangingCoinsHistoriesId(!map.containsKey("changingCoinsHistoriesId") ? null
            : map.get("changingCoinsHistoriesId").toString());
        result.setChangingCoins(!map.containsKey("changingCoins") ? null
            : Long.parseLong(map.get("changingCoins").toString()));
        result.setChangingCoinsType(!map.containsKey("changingCoinsType") ? null
            : ChangingCoinsType.valueOf(map.get("changingCoinsType").toString()));
        result.setFromChangingTime(!map.containsKey("fromChangingTime") ? null
            : SubscriptionsInfoResponse.stringToLocalTime(map.get("fromChangingTime").toString()));
        result.setToChangingTime(!map.containsKey("toChangingTime") ? null
            : SubscriptionsInfoResponse.stringToLocalTime(map.get("toChangingTime").toString()));
        return result;
    }
}

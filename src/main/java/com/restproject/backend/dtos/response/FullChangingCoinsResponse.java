package com.restproject.backend.dtos.response;

import com.restproject.backend.enums.ChangingCoinsType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static com.restproject.backend.dtos.response.SubscriptionsInfoResponse.stringToLocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FullChangingCoinsResponse {
    String changingCoinsHistoriesId;
    Long changingCoins;
    LocalDateTime changingTime;
    ChangingCoinsType changingCoinsType;
    String fullName;

    public static FullChangingCoinsResponse buildFromNativeQuery(Object[] params) {
        return FullChangingCoinsResponse.builder()
            .changingCoinsHistoriesId(String.valueOf(params[0]))
            .changingCoins(Long.valueOf(String.valueOf(params[1])))
            .changingTime(stringToLocalTime(String.valueOf(params[2])))
            .changingCoinsType(ChangingCoinsType.valueOf(String.valueOf(params[3])))
            .fullName(String.valueOf(params[4]))
            .build();
    }
}

package com.restproject.backend.dtos.request;

import com.restproject.backend.enums.Gender;
import com.restproject.backend.exceptions.ApplicationException;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfoAndStatusRequest {
    String firstName;
    String lastName;
    Gender gender;
    String email;
    Long fromCoins;
    Long toCoins;
    LocalDate fromDob;
    LocalDate toDob;
    LocalDateTime fromCreatedTime;
    LocalDateTime toCreatedTime;
    Boolean active;

    public static UserInfoAndStatusRequest buildFromHashMap(HashMap<String, Object> map)
        throws ApplicationException, NoSuchFieldException, IllegalArgumentException, NullPointerException {
        for (String key : map.keySet())
            if (Arrays.stream(UserInfoAndStatusRequest.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key))) throw new NoSuchFieldException();

        var result = new UserInfoAndStatusRequest();
        result.setFirstName(map.containsKey("firstName") ? map.get("firstName").toString() : null);
        result.setLastName(map.containsKey("lastName") ? map.get("lastName").toString() : null);
        result.setFromCoins(map.containsKey("fromCoins") ? Long.parseLong(map.get("fromCoins").toString()) : null);
        result.setToCoins(map.containsKey("toCoins") ? Long.parseLong(map.get("toCoins").toString()) : null);
        result.setEmail(map.containsKey("email") ? map.get("email").toString() : null);
        result.setActive(map.containsKey("active") ? Boolean.parseBoolean(map.get("active").toString()) : null);

        result.setGender(!map.containsKey("genderId") ? null
            : Gender.getByGenderId(Integer.parseInt(map.get("genderId").toString())));
        result.setToDob(!map.containsKey("toDob") ? null
            : LocalDate.parse(map.get("toDob").toString().split("T")[0], DateTimeFormatter.ISO_LOCAL_DATE));
        result.setToDob(!map.containsKey("fromDob") ? null
            : LocalDate.parse(map.get("fromDob").toString().split("T")[0], DateTimeFormatter.ISO_LOCAL_DATE));
        result.setFromCreatedTime(!map.containsKey("fromCreatedTime") ? null
            : LocalDateTime.parse(map.get("fromCreatedTime").toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        result.setToCreatedTime(!map.containsKey("toCreatedTime") ? null
            : LocalDateTime.parse(map.get("toCreatedTime").toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return result;
    }
}

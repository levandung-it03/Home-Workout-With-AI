package com.restproject.backend.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum DefaultOauth2Password {
    GOOGLE("DEFAULT_OAUTH2_PASSWORD_FOR_GOOGLE"),
    FACEBOOK("DEFAULT_OAUTH2_PASSWORD_FOR_FACEBOOK"),
    ;
    String virtualPassword;
    DefaultOauth2Password(String virtualPassword) {
        this.virtualPassword = virtualPassword;
    }

    public static boolean isDefaultOauth2Password(String password) {
        return Arrays.stream(DefaultOauth2Password.values())
            .map(DefaultOauth2Password::getVirtualPassword)
            .anyMatch(password::equals);
    }
}

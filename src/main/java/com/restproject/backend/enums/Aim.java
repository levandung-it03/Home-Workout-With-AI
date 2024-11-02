package com.restproject.backend.enums;

import com.restproject.backend.exceptions.ApplicationException;
import lombok.Getter;

@Getter
public enum Aim {
    WEIGHT_UP(1, "Weight up"),
    MAINTAIN_WEIGHT(0, "Maintain weight"),
    WEIGHT_DOWN(-1, "Weight down"),
    ;
    final Integer type;
    final String name;
    Aim(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static Aim getByType(Integer aimType) {
        for (Aim aim: Aim.values())
            if (aim.type.equals(aimType))
                return aim;
        throw new ApplicationException(ErrorCodes.INVALID_AIM);
    }
}

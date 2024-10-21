package com.restproject.backend.enums;

import com.restproject.backend.exceptions.ApplicationException;
import lombok.Getter;

@Getter
public enum Aim {
    WEIGHT_UP(1, "Weight up"),
    MAINTAIN_WEIGHT(0, "Maintain weight"),
    WEIGHT_DOWN(-1, "Weight down"),
    ;
    final Integer level;
    final String name;
    Aim(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public static Aim getByLevel(Integer aimLevel) {
        for (Aim aim: Aim.values())
            if (aim.level.equals(aimLevel))
                return aim;
        throw new ApplicationException(ErrorCodes.INVALID_AIM);
    }
}

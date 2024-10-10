package com.restproject.backend.enums;

import com.restproject.backend.annotations.dev.Overload;
import com.restproject.backend.exceptions.ApplicationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Level {
    BEGINNER(1, "Beginner"),
    INTERMEDIATE(2, "Intermediate"),
    ADVANCE(3, "Advance"),
    ;

    Integer level;
    String name;

    public static Level getByLevel(int level) throws ApplicationException {
        for (Level levelEnum: Level.values())
            if (levelEnum.getLevel() == level)
                return levelEnum;
        throw new ApplicationException(ErrorCodes.INVALID_LEVEL);
    }

    @Overload
    public static Level getByLevel(Object level) {
        return Level.getByLevel(Integer.parseInt(level.toString()));
    }

    public static String getRawLevelByLevel(Object level) {
        return Level.getByLevel(Integer.parseInt(level.toString())).toString();
    }

    public static List<Level> getAllLevels() {
        return Arrays.asList(Level.values());
    }
}

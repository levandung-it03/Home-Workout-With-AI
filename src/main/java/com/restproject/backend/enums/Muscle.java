package com.restproject.backend.enums;

import com.restproject.backend.exceptions.ApplicationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Muscle {
    CHEST(0, "Chest"),
    BICEPS(1, "Biceps"),
    TRICEPS(2, "Triceps"),
    BACK_LATS(3, "Back&Lats"),
    LEG(4, "Leg"),
    ABS(5, "Abs"),
    SHOULDERS(6, "Shoulders"),
    CARDIO(7, "Cardio"),
    ;

    int id;
    String name;

    public static Muscle getById(int id) throws ApplicationException {
        for (Muscle muscleEnum: Muscle.values())
            if (muscleEnum.getId() == id)
                return muscleEnum;
        throw new ApplicationException(ErrorCodes.INVALID_MUSCLE_ID);
    }

    public static List<Muscle> getAllMuscles() {
        return Arrays.asList(Muscle.values());
    }
}

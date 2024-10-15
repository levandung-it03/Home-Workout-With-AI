package com.restproject.backend.enums;

import com.restproject.backend.annotations.dev.Overload;
import com.restproject.backend.exceptions.ApplicationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Muscle {
    CHEST(0, "Chest"),
    BICEPS(1, "Biceps"),
    TRICEPS(2, "Triceps"),
    BACK_LATS(3, "Back_Lats"),
    LEG(4, "Leg"),
    ABS(5, "Abs"),
    SHOULDERS(6, "Shoulders"),
    CARDIO(7, "Cardio"),
    ;

    Integer id;
    String name;

    public static Muscle getById(int id) throws ApplicationException {
        for (Muscle muscleEnum: Muscle.values())
            if (muscleEnum.getId() == id) return muscleEnum;
        throw new ApplicationException(ErrorCodes.INVALID_MUSCLE_ID);
    }

    @Overload
    public static Muscle getById(String id) throws ApplicationException {
        for (Muscle muscleEnum: Muscle.values())
            if (muscleEnum.getId() == Integer.parseInt(id.trim())) return muscleEnum;
        throw new ApplicationException(ErrorCodes.INVALID_MUSCLE_ID);
    }

    public static List<Muscle> parseAllMuscleIdsArrToRaw(String muscleIdsArrAsStr) {
        return Arrays
            .stream(muscleIdsArrAsStr.replaceAll("[\\[\\]]", "").split(","))
            .map(id -> Muscle.getById(Integer.parseInt(id.trim()))).toList();
    }

    public static List<Muscle> getAllMuscles() {
        return Arrays.asList(Muscle.values());
    }
}

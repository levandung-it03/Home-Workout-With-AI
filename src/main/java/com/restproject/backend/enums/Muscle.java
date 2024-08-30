package com.restproject.backend.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Muscle {
    CHEST(0, "Chest"),
    BICEPS(1, "Biceps"),
    TRICEPS(2, "Triceps"),
    BACK_LATS(3, "Back & Lats"),
    LEG(4, "Leg"),
    ABS(5, "Abs"),
    SHOULDERS(6, "Shoulders"),
    CARDIO(7, "Cardio"),
    ;

    int id;
    String name;
}

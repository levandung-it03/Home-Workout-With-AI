package com.restproject.backend.enums;

public enum Gender {
    MALE(1),
    FEMALE(0)
    ;
    final int genderId;
    Gender(int genderId) {
        this.genderId = genderId;
    }
}

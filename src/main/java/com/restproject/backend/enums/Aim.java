package com.restproject.backend.enums;

public enum Aim {
    WEIGHT_UP(1),
    MAINTAIN_WEIGHT(0),
    WEIGHT_DOWN(-1),
    ;
    final int level;
    Aim(int level) {
        this.level = level;
    }
}

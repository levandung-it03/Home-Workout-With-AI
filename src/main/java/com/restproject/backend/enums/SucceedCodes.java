package com.restproject.backend.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SucceedCodes {
    //--Auth(21)
    AUTHENTICATION(21001, "Authenticate successfully"),
    REFRESHING_TOKEN(21002, "Refresh Token successfully"),
    LOGOUT(21003, "Logout successfully"),
    //--Enums(22)
    GET_ALL_LEVEL_ENUMS(22001, "Get all Levels successfully"),
    GET_ALL_MUSCLE_ENUMS(22002, "Get all Muscles successfully"),
    //--Exercise(23)
    CREATE_EXERCISE(23002, "Create new Exercise successfully"),
    UPDATE_EXERCISE(23003, "Update Exercise successfully"),
    DELETE_EXERCISE(23004, "Delete Exercise successfully"),
    GET_EXERCISES_HAS_MUSCLES_PAGES(23005, "Get Exercise has Muscles pages successfully"),
    //--Schedule(24)
    CREATE_SCHEDULE(24001, "Create new Schedule successfully"),
    GET_SCHEDULES_PAGES(24002, "Get Schedule pages successfully"),
    //--Session(25)
    CREATE_SESSION(25001, "Create new Session successfully"),
    UPDATE_SESSION(23003, "Update Session successfully"),
    GET_SESSIONS_HAS_MUSCLES_PAGES(23002, "Get Session has Muscles pages successfully"),
    GET_EXERCISES_HAS_MUSCLES_OF_SESSION_PAGES(23003, "Get Exercises in Session pages successfully"),
    ;

    int code;
    String message;
}

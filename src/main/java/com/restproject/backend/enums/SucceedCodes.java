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
    GET_EXS_BY_LV_AND_MUSCLE(23001, "Get Exercises successfully"),
    CREATE_EXERCISE(23002, "Create new Exercise successfully"),
    UPDATE_EXERCISE(23003, "Update Exercise successfully"),
    DELETE_EXERCISE(23004, "Delete Exercise successfully"),
    GET_PAGINATED_EXERCISES(23005, "Get paginated list of Exercise successfully"),
    //--Schedule(24)
    CREATE_SCHEDULE(24001, "Create new Schedule successfully"),
    GET_PAGINATED_SCHEDULES(24002, "Get paginated list of Schedule successfully"),
    //--Session(25)
    GET_SESSIONS_BY_LV(25001, "Get Sessions by level successfully"),
    CREATE_SESSION(25002, "Create new Session successfully"),
    GET_PAGINATED_SESSIONS(23003, "Get paginated list of Session successfully"),
    GET_PAGINATED_EXERCISES_OF_SESSION(23004, "Get paginated list of Exercises in Session successfully"),
    GET_PAGINATED_FILTERING_SESSIONS(23005, "Get paginated filtering list of Session successfully"),
    ;

    int code;
    String message;
}

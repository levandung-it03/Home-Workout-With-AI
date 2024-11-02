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
    GET_OTP(21004, "Get OTP successfully"),
    VERIFY_OTP(21005, "Verify OTP successfully"),
    SEND_RANDOM_PASSWORD(21006, "Send random password into your email successfully"),
    GET_OTP_TO_CHANGE_PASSWORD(21007, "Authentication successfully and sent OTP"),
    CHANGE_PASSWORD(21008, "Change password successfully"),
    //--Enums(22)
    GET_ALL_LEVEL_ENUMS(22001, "Get all Levels successfully"),
    GET_ALL_MUSCLE_ENUMS(22002, "Get all Muscles successfully"),
    GET_ALL_GENDER_ENUMS(22003, "Get all Genders successfully"),
    GET_ALL_AIMS_ENUMS(22003, "Get all Aims successfully"),
    //--Exercise(23)
    CREATE_EXERCISE(23001, "Create new Exercise successfully"),
    UPDATE_EXERCISE(23002, "Update Exercise successfully"),
    DELETE_EXERCISE(23003, "Delete Exercise successfully"),
    GET_EXERCISES_HAS_MUSCLES_PAGES(23004, "Get Exercise has Muscles pages successfully"),
    UPLOAD_EXERCISE_IMG(23005, "Upload Exercise image successfully"),
    //--Schedule(24)
    CREATE_SCHEDULE(24001, "Create new Schedule successfully"),
    UPDATE_SCHEDULE(24002, "Update Exercise successfully"),
    DELETE_SCHEDULE(24003, "Delete Exercise successfully"),
    GET_SCHEDULES_PAGES(24004, "Get Schedule pages successfully"),
    GET_SESSIONS_HAS_MUSCLES_OF_SCHEDULE_PAGES(24005, "Get Sessions in Schedule pages successfully"),
    GET_SESSIONS_OF_SCHEDULE_RELATIONSHIP(24006, "Get Sessions in Schedule successfully"),
    GET_PREVIEW_SCHEDULE(24007, "Get preview Schedule successfully"),
    GET_SESSION_QUANTITY_OF_SCHEDULE(24008, "Get Sessions quantity of Schedule successfully"),
    //--Session(25)
    CREATE_SESSION(25001, "Create new Session successfully"),
    UPDATE_SESSION(25002, "Update Session successfully"),
    DELETE_SESSION(25003, "Delete Exercise successfully"),
    GET_SESSIONS_HAS_MUSCLES_PAGES(25004, "Get Session has Muscles pages successfully"),
    GET_EXERCISES_HAS_MUSCLES_OF_SESSION_PAGES(25005, "Get Exercises in Session pages successfully"),
    GET_EXERCISES_OF_SESSION_RELATIONSHIP(25006, "Get Exercises in Session successfully"),
    //--UserInfo(26)
    CREATE_USER_INFO(26001, "Create new User Info successfully"),
    GET_USER_INFO_PAGES(26002, "Get User Info pages successfully"),
    UPDATE_USER_INFO(26003, "Update User Info successfully"),
    GET_USER_INFO(26004, "Get User Info successfully"),
    //--User(27)
    UPDATE_USER_STATUS(27001, "Update User status"),
    //--Subscription(28)
    GET_SUBSCRIPTIONS_OF_USER_INFO_PAGES(28001, "Get Subscriptions successfully!"),
    SUBSCRIBE_SCHEDULE(28002, "You've been subscribed Schedule successfully!"),
    //--Slides(29)
    GET_ALL_SLIDES_FOR_HOME(29001, "Get all Slides successfully"),
    UPLOAD_SLIDES(29002, "Upload Slide successfully"),
    DELETE_SLIDES(29003, "Delete Slide successfully"),
    ;

    int code;
    String message;
}

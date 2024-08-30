package com.restproject.backend.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCodes {
    //--SuccessfullyCode: "0000"
    UNAWARE_ERR(1000, "Unaware exception's thrown from resource server", BAD_REQUEST),
    INVALID_CREDENTIALS(1001, "Username or Password is invalid", UNAUTHORIZED),
    INVALID_TOKEN(1002, "Token or its claims are invalid", FORBIDDEN),
    EXPIRED_TOKEN(1003, "Token is expired", FORBIDDEN),
    FORBIDDEN_USER(1004, "User not found or access denied", BAD_REQUEST),
    LOGIN_SESSION_EXPIRED(1005, "Login session is expired, please login again", BAD_REQUEST),
    VALIDATOR_ERR_RESPONSE(1006, "Invalid form fields", BAD_REQUEST),
    BLANK_NAME(1007, "Name can't be blank", BAD_REQUEST),
    BLANK_DESCRIPTION(1007, "Description can't be blank", BAD_REQUEST),
    INVALID_LEVEL(1008, "Level is invalid", BAD_REQUEST),
    INVALID_IDS_COLLECTION(1009, "Collection of Ids is invalid", BAD_REQUEST),
    ;

    int code;
    String message;
    HttpStatus httpStatus;
}

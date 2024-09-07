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
    //--Generals(10)
    UNAWARE_ERR(10000, "Unaware exception's thrown from resource server", BAD_REQUEST),
    VALIDATOR_ERR_RESPONSE(10001, "Invalid variable type or format of field \"${field}\"", BAD_REQUEST),
    PARSE_JSON_ERR(10002, "Invalid variable type or format of field '${field}'", BAD_REQUEST),
    CONSTRAINT_VIOLATION(10004, "Constraint was triggered because of invalid data", BAD_REQUEST),
    INVALID_IDS_COLLECTION(10004, "Collection of Ids is invalid", BAD_REQUEST),
    INVALID_PRIMARY(10005, "Can not get object because id or primary fields are invalid", BAD_REQUEST),
    FORBIDDEN_UPDATING(10006, "Can not update or delete a depended object", BAD_REQUEST),
    NOT_SYNC_LEVEL(10007, "Level between relationships don't synchronize to each other", BAD_REQUEST),
    INVALID_FILTERING_FIELD_OR_VALUE(10008, "Invalid filtering field or value", BAD_REQUEST),
    //--Auth(11)
    INVALID_CREDENTIALS(11001, "Username or Password is invalid", UNAUTHORIZED),
    INVALID_TOKEN(11002, "Token or its claims are invalid", FORBIDDEN),
    EXPIRED_TOKEN(11003, "Token is expired", FORBIDDEN),
    FORBIDDEN_USER(11004, "User not found or access denied", BAD_REQUEST),
    LOGIN_SESSION_EXPIRED(11005, "Login session is expired, please login again", BAD_REQUEST),
    //--Enums(12)
    INVALID_LEVEL(12001, "Level is invalid", BAD_REQUEST),
    INVALID_MUSCLE_ID(12002, "Muscle Id is invalid", BAD_REQUEST),
    ;

    int code;
    String message;
    HttpStatus httpStatus;
}

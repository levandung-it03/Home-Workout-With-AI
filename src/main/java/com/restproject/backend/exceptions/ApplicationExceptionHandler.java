package com.restproject.backend.exceptions;

import com.restproject.backend.dtos.reponse.ApiResponseObject;
import com.restproject.backend.enums.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(value = AccessDeniedException.class)
    public ApiResponseObject<Void> handleAuthenticationException(AccessDeniedException exception) {
        var response = ApiResponseObject.buildByErrorCodes(ErrorCodes.FORBIDDEN_USER);
        log.info("[HANDLER]_AccessDeniedException: " + exception.getMessage());
        return response;
    }

    @ExceptionHandler(value = AuthenticationException.class)
    public ApiResponseObject<Void> handleAuthenticationException(AuthenticationException exception) {
        var response = ApiResponseObject.buildByErrorCodes(ErrorCodes.INVALID_CREDENTIALS);
        log.info("[HANDLER]_AuthenticationException: " + exception.getMessage());
        return response;
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResponseObject<Void> handleHibernateValidationException(MethodArgumentNotValidException exception) {
        ApiResponseObject<Void> response;
        if (exception.getMessage().contains("ErrorCodes.")) {
            var errorCode = ErrorCodes.valueOf(exception.getMessage().split("ErrorCodes.")[1]);
            response = ApiResponseObject.buildByErrorCodes(errorCode);
            log.info("[HANDLER]_ValidatorException: " + exception.getMessage());
        } else {
            response = ApiResponseObject.buildByErrorCodes(ErrorCodes.VALIDATOR_ERR_RESPONSE);
            log.info("[HANDLER]_ValidatorException: " + exception.getMessage());
        }
        return response;
    }

    @ExceptionHandler(value = ApplicationException.class)
    public ApiResponseObject<Void> handleCustomApplicationException(ApplicationException exception) {
        var response = ApiResponseObject.buildByErrorCodes(exception.getErrorCodes());
        log.info("[HANDLER]_ApplicationException: " + exception.getMessage());
        return response;
    }

    @ExceptionHandler(value = Exception.class)
    public ApiResponseObject<Void> handleUnawareException(Exception exception) {
        var response = ApiResponseObject.buildByErrorCodes(ErrorCodes.UNAWARE_ERR);

        log.info("[HANDLER]_Exception: " + exception.getMessage());
        return response;
    }
}

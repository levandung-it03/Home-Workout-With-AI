package com.restproject.backend.exceptions;

import com.restproject.backend.enums.ErrorCodes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationException extends RuntimeException{
    private ErrorCodes errorCodes;

    public ApplicationException(ErrorCodes errorCodes) {
        super(errorCodes.getMessage());
        this.errorCodes = errorCodes;
    }

}

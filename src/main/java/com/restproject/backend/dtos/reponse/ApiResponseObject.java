package com.restproject.backend.dtos.reponse;

import com.restproject.backend.annotations.dev.Overload;
import com.restproject.backend.enums.ErrorCodes;
import com.restproject.backend.enums.SucceedCodes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseObject <T> {
    private int applicationCode;
    private String message;
    private int httpStatusCode;
    private T data;
    private LocalDateTime responseTime;

    public static ApiResponseObject<Void> buildByErrorCodes(ErrorCodes errorCodes) {
        var result = new ApiResponseObject<Void>();
        result.setApplicationCode(errorCodes.getCode());
        result.setMessage(errorCodes.getMessage());
        result.setHttpStatusCode(errorCodes.getHttpStatus().value());
        result.setData(null);
        result.setResponseTime(LocalDateTime.now(ZoneId.from(ZonedDateTime.now())));
        return result;
    }

    public ApiResponseObject<T> buildSuccessResponse(SucceedCodes succeedCodes, T data) {
        this.setApplicationCode(succeedCodes.getCode());
        this.setMessage(succeedCodes.getMessage());
        this.setHttpStatusCode(HttpStatus.OK.value());
        this.setData(data);
        this.setResponseTime(LocalDateTime.now(ZoneId.from(ZonedDateTime.now())));
        return this;
    }

    @Overload
    public ApiResponseObject<T> buildSuccessResponse(SucceedCodes succeedCodes) {
        return this.buildSuccessResponse(succeedCodes, null);
    }
}

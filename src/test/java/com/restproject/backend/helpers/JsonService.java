package com.restproject.backend.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restproject.backend.dtos.reponse.ApiResponseObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JsonService {
    @Autowired
    ObjectMapper objectMapper;

    public <T> ApiResponseObject<T> parseResponseJson(String json, Class<T> type) throws JsonProcessingException {
        ApiResponseObject<?> apiObject = objectMapper.readValue(json, ApiResponseObject.class);
        T data = objectMapper.convertValue(apiObject.getData(), type);
        return ApiResponseObject.<T>builder()
            .applicationCode(apiObject.getApplicationCode())
            .message(apiObject.getMessage())
            .httpStatusCode(apiObject.getHttpStatusCode())
            .data(data)
            .responseTime(apiObject.getResponseTime())
            .build();
    }

    public <T> ApiResponseObject<List<T>> parseResJsonByDataList(String json, Class<T> rawTypeToList)
        throws JsonProcessingException {
        ApiResponseObject<?> apiObject = objectMapper.readValue(json, ApiResponseObject.class);
        List<?> rawData = objectMapper.convertValue(apiObject.getData(), List.class);
        List<T> data = rawData.stream().map(d -> objectMapper.convertValue(d, rawTypeToList)).toList();
        return ApiResponseObject.<List<T>>builder()
            .applicationCode(apiObject.getApplicationCode())
            .message(apiObject.getMessage())
            .httpStatusCode(apiObject.getHttpStatusCode())
            .data(data)
            .responseTime(apiObject.getResponseTime())
            .build();
    }
}

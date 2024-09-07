package com.restproject.backend.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilteringPageRequest {
    @NotNull
    @Min(1)
    Integer page;

    @NotNull
    @NotEmpty
    Map<String, Object> filterFields;
}

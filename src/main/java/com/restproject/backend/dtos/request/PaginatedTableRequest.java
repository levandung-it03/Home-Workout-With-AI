package com.restproject.backend.dtos.request;

import com.restproject.backend.annotations.constraint.SortedModeConstraint;
import com.restproject.backend.enums.Level;
import com.restproject.backend.exceptions.ApplicationException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginatedTableRequest {

    @NotNull
    @Min(1)
    Integer page;

    HashMap<String, Object> filterFields;

    String sortedField;

    @SortedModeConstraint
    Integer sortedMode;
}

package com.restproject.backend.dtos.request;

import com.restproject.backend.annotations.constraint.SortedModeConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginatedRelationshipRequest {

    @NotNull
    Long id;

    @NotNull
    @Min(1)
    Integer page;

    @NotNull
    Map<String, Object> filterFields;

    String sortedField;

    @SortedModeConstraint
    Integer sortedMode;
}

package com.restproject.backend.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageObject {
    int pageNumber;
    int pageSize;
    String sortedField;
    Integer sortedMode;
    private final HashMap<Integer, Direction> SORTING_DIRECTION = new HashMap<>(Map.ofEntries(
        Map.entry(1, Direction.ASC),
        Map.entry(-1, Direction.DESC)
    ));

    public Pageable toPageable() {
        if (Objects.isNull(sortedField) || sortedField.isEmpty())
            return PageRequest.of(pageNumber - 1, pageSize);
        if (Objects.isNull(sortedMode))
            sortedMode = 1;
        return PageRequest.of(pageNumber - 1, pageSize, SORTING_DIRECTION.get(sortedMode), sortedField);
    }
}

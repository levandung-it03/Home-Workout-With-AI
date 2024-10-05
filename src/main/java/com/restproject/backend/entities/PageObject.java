package com.restproject.backend.entities;

import com.restproject.backend.enums.PageEnum;
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
    private final int pageSize = PageEnum.SIZE.getSize();
    Integer page;
    String sortedField;
    Integer sortedMode;

    private final HashMap<Integer, Direction> SORTING_DIRECTION = new HashMap<>(Map.ofEntries(
        Map.entry(1, Direction.ASC),
        Map.entry(-1, Direction.DESC)
    ));

    public Pageable toPageable() {
        if (!Objects.isNull(this.sortedField))
            this.sortedField = this.camelCaseToUnderscore(this.sortedField);
        if (Objects.isNull(sortedField) || sortedField.isEmpty())
            return PageRequest.of(page - 1, pageSize);
        if (Objects.isNull(sortedMode))
            sortedMode = 1;
        return PageRequest.of(page - 1, pageSize, SORTING_DIRECTION.get(sortedMode), sortedField);
    }

    public String camelCaseToUnderscore(String s) {
        StringBuilder r = new StringBuilder();
        for (char c: s.toCharArray()) {
            if (65 <= (int) c && (int) c <= 90)
                r.append("_").append(Character.toChars(c + 32));
            else r.append(c);
        }
        return r.toString();
    }
}

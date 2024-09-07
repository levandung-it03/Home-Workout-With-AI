package com.restproject.backend.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageObject {
    int pageNumber;
    int pageSize;

    public Pageable toPageable() {
        return PageRequest.of(pageNumber - 1, pageSize);
    }
}

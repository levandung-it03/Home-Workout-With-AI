package com.restproject.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "muscle",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"muscle_name"})}
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Muscle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "muscle_id", nullable = false)
    Long muscleId;

    @Column(name = "muscle_name", nullable = false)
    String muscleName;

    public static List<Long> parseStrIdsToList(Object ids) {
        return Arrays.stream(ids.toString()
                .replaceAll("[\\[\\]]", "")
                .split(",")
            ).map(Long::parseLong).toList();
    }
}

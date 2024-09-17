package com.restproject.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restproject.backend.enums.Muscle;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "muscles_of_exercises",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"muscle_enum, exercise_id"})},
    indexes = {
        @Index(name = "exercise_index", columnList = "exercise_id"),
        @Index(name = "muscle_index", columnList = "muscle_enum")
    }
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MusclesOfExercises {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "muscle_enum", nullable = false, updatable = false)
    Muscle muscle;

    @ManyToOne(targetEntity = Exercise.class)
    @JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id", updatable = false)
    @JsonIgnore
    Exercise exercise;
}

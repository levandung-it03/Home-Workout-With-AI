package com.restproject.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "muscle_exercise",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"muscle_id, exercise_id"})},
    indexes = {
        @Index(name = "exercise_index", columnList = "exercise_id"),
        @Index(name = "muscle_index", columnList = "muscle_id")
    }
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MuscleExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(targetEntity = Muscle.class)
    @JoinColumn(name = "muscle_id", referencedColumnName = "muscle_id", updatable = false)
    @JsonIgnore
    Muscle muscle;

    @ManyToOne(targetEntity = Exercise.class)
    @JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id", updatable = false)
    @JsonIgnore
    Exercise exercise;
}

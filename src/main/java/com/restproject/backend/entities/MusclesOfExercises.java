package com.restproject.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restproject.backend.enums.Muscle;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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
    public static final Set<String> INSTANCE_FIELDS;
    static {    //--Initializing when static field is called
        INSTANCE_FIELDS = Arrays.stream(Exercise.class.getDeclaredFields())
            .map(Field::getName)
            .collect(Collectors.toSet());
        INSTANCE_FIELDS.add("muscleList"); // Directly add the field name
    }

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

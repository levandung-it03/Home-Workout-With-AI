package com.restproject.backend.entities;

import com.restproject.backend.enums.Muscle;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import net.minidev.json.annotate.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "muscles_of_exercises",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"muscle_enum, exercise_id"})}
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MusclesOfExercises {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "muscle_enum", nullable = false)
    Muscle muscle;

    @ManyToOne(targetEntity = Exercise.class, cascade = CascadeType.ALL)
    @MapsId("exercise_id")
    @JoinColumn(columnDefinition = "exercise_id", referencedColumnName = "exercise_id", insertable = false,
        updatable = false)
    @JsonIgnore
    Exercise exercise;
}

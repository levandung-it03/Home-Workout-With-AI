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
    name = "exercises_of_sessions",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"exercise_id", "session_id"})}
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExercisesOfSessions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(targetEntity = Exercise.class)
    @JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id", updatable = false)
    @JsonIgnore
    Exercise exercise;

    @ManyToOne(targetEntity = Session.class)
    @JoinColumn(name = "session_id", referencedColumnName = "session_id", updatable = false)
    @JsonIgnore
    Session session;
}

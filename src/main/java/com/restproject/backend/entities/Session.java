package com.restproject.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restproject.backend.enums.Level;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "session",
    uniqueConstraints = @UniqueConstraint(columnNames = {"name","level_enum"})
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    Long sessionId;

    @Column(name = "name", nullable = false, length = 100)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "level_enum", nullable = false)
    Level levelEnum;

    @Column(name = "description", nullable = false, length = 200)
    String description;

    @Column(name = "switch_exercise_delay", nullable = false)
    Integer switchExerciseDelay;

    @ManyToMany(targetEntity = Exercise.class, fetch = FetchType.LAZY)
    @JoinTable(
        name = "exercises_of_sessions",
        joinColumns = @JoinColumn(name = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    @JsonIgnore
    Collection<Exercise> exercisesOfSession;

    @ManyToMany
    @JoinTable(
        name = "muscle_session",
        joinColumns = @JoinColumn(name = "session_id", referencedColumnName = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "muscle_id", referencedColumnName = "muscle_id")
    )
    Collection<Muscle> muscles;
}

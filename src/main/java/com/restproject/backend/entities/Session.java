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
@Table(name = "session")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    Long sessionId;

    @Column(name = "name", nullable = false)
    @Length(max = 20)
    String name;

    @Column(name = "muscle_list", nullable = false)
    String muscleList;

    @Enumerated(EnumType.STRING)
    @Column(name = "level_enum", nullable = false)
    Level level;

    @Column(name = "description", nullable = false)
    String description;

    @ManyToMany(targetEntity = Exercise.class, fetch = FetchType.LAZY)
    @JoinTable(
        name = "exercises_of_sessions",
        joinColumns = @JoinColumn(name = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    @JsonIgnore
    Collection<Exercise> exercisesOfSession;
}

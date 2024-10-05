package com.restproject.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Column(name = "name", nullable = false)
    @Length(max = 50)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "level_enum", nullable = false)
    Level levelEnum;

    @Column(name = "description", nullable = false)
    @Length(max = 100)
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

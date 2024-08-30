package com.restproject.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import net.minidev.json.annotate.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "exercises_of_sessions")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExercisesOfSessions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(targetEntity = Exercise.class, cascade = CascadeType.ALL)
    @JoinColumn(columnDefinition = "exercise_id", referencedColumnName = "exercise_id", insertable = false,
        updatable = false)
    @JsonIgnore
    Exercise exercise;

    @ManyToOne(targetEntity = Session.class, cascade = CascadeType.ALL)
    @JoinColumn(columnDefinition = "session_id", referencedColumnName = "session_id", insertable = false,
        updatable = false)
    @JsonIgnore
    Session session;
}

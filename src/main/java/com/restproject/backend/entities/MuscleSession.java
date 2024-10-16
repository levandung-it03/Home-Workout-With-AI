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
    name = "muscle_session",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"muscle_id, session_id"})},
    indexes = {
        @Index(name = "session_index", columnList = "session_id"),
        @Index(name = "muscle_index", columnList = "muscle_id")
    }
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MuscleSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(targetEntity = Muscle.class)
    @JoinColumn(name = "muscle_id", referencedColumnName = "muscle_id", updatable = false)
    @JsonIgnore
    Muscle muscle;

    @ManyToOne(targetEntity = Session.class)
    @JoinColumn(name = "session_id", referencedColumnName = "session_id", updatable = false)
    @JsonIgnore
    Session session;
}

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
    name = "muscles_of_sessions",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"muscle_enum, session_id"})},
    indexes = {
        @Index(name = "session_index", columnList = "session_id"),
        @Index(name = "muscle_index", columnList = "muscle_enum")
    }
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MusclesOfSessions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(targetEntity = Session.class)
    @JoinColumn(name = "session_id", referencedColumnName = "session_id", updatable = false)
    @JsonIgnore
    Session session;

    @Enumerated(EnumType.STRING)
    @Column(name = "muscle_enum", nullable = false, updatable = false)
    Muscle muscleEnum;
}

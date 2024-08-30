package com.restproject.backend.entities;

import com.restproject.backend.enums.Level;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    String name;

    @Column(name = "muscleList", nullable = false)
    String muscleList;

    @Enumerated(EnumType.STRING)
    @Column(name = "level_enum", nullable = false)
    Level level;

    @Column(name = "name", nullable = false)
    String description;
}

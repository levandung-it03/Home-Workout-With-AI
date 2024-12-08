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
    name = "sessions_of_schedules",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"schedule_id", "ordinal"})}
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionsOfSchedules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(targetEntity = Session.class)
    @JoinColumn(name = "session_id", referencedColumnName = "session_id")
    Session session;

    @ManyToOne(targetEntity = Schedule.class)
    @JoinColumn(name = "schedule_id", referencedColumnName = "schedule_id")
    @JsonIgnore
    Schedule schedule;

    @Column(name = "ordinal", nullable = false)
    Integer ordinal;
}

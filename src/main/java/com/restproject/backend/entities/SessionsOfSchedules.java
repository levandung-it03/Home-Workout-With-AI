package com.restproject.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import net.minidev.json.annotate.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "sessions_of_schedules")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionsOfSchedules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(targetEntity = Session.class, cascade = CascadeType.ALL)
    @MapsId("session_id")
    @JoinColumn(columnDefinition = "session_id", referencedColumnName = "session_id", insertable = false,
        updatable = false)
    @JsonIgnore
    Session session;

    @ManyToOne(targetEntity = Schedule.class, cascade = CascadeType.ALL)
    @MapsId("schedule_id")
    @JoinColumn(columnDefinition = "schedule_id", referencedColumnName = "shcedule_id", insertable = false,
        updatable = false)
    @JsonIgnore
    Schedule schedule;
}

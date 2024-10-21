package com.restproject.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restproject.backend.enums.Level;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "schedule",
    uniqueConstraints = @UniqueConstraint(columnNames = {"name","level_enum"})
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    Long scheduleId;

    @Column(name = "name", nullable = false)
    @Length(max = 100)
    String name;

    @Column(name = "description", nullable = false)
    @Length(max = 200)
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "level_enum", nullable = false)
    Level levelEnum;

    @Column(name = "coins", nullable = false)
    @Min(0)
    Long coins;

    @ManyToMany(targetEntity = Session.class, fetch = FetchType.LAZY)
    @JoinTable(
        name = "sessions_of_schedules",
        joinColumns = @JoinColumn(name = "schedule_id"),
        inverseJoinColumns = @JoinColumn(name = "session_id")
    )
    @JsonIgnore
    Collection<Session> sessionsOfSchedule;
}

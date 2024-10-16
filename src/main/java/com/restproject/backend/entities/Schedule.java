package com.restproject.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restproject.backend.enums.Level;
import com.restproject.backend.exceptions.ApplicationException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

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

    public static Schedule buildFromHashMap(HashMap<String, Object> map)
        throws NullPointerException, ApplicationException, IllegalArgumentException, NoSuchFieldException {
        for (String key: map.keySet())
            if (Arrays.stream(Schedule.class.getDeclaredFields()).noneMatch(f -> f.getName().equals(key)))
                throw new NoSuchFieldException();

        var result = new Schedule();
        result.setName(!map.containsKey("name") ? null : map.get("name").toString());
        result.setLevelEnum(!map.containsKey("level") ? null : Level.getByLevel(map.get("level")));
        result.setDescription(!map.containsKey("description") ? null : map.get("description").toString());
        result.setCoins(!map.containsKey("coins") ? null : Long.parseLong(map.get("coins").toString()));
        return result;
    }
}

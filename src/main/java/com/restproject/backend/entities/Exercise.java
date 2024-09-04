package com.restproject.backend.entities;

import com.restproject.backend.enums.Level;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "exercise",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "level_enum", "basic_reps"})
    }
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    Long exerciseId;

    @Column(name = "name", nullable = false)
    @Length(max = 20)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "level_enum", nullable = false)
    Level level;

    @Column(name = "basic_reps", nullable = false)
    @Min(0)
    @Max(9999)
    Integer basicReps;
}

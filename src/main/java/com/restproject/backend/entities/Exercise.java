package com.restproject.backend.entities;

import com.restproject.backend.enums.Level;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Length(max = 100)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "level_enum", nullable = false)
    Level levelEnum;

    @Column(name = "basic_reps", nullable = false)
    @Min(0)
    @Max(9999)
    Integer basicReps;

    @Column(name = "image_public_id")
    String imagePublicId;

    @Column(name = "image_url")
    String imageUrl;
}

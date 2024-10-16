package com.restproject.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "exercises_of_sessions",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"exercise_id", "session_id", "ordinal"})}
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExercisesOfSessions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(targetEntity = Exercise.class)
    @JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id", updatable = false)
    @JsonIgnore
    Exercise exercise;

    @ManyToOne(targetEntity = Session.class)
    @JoinColumn(name = "session_id", referencedColumnName = "session_id", updatable = false)
    @JsonIgnore
    Session session;

    @Column(name = "ordinal", nullable = false)
    Integer ordinal;

    @Column(name = "down_reps_ratio", nullable = false)
    Float downRepsRatio;    //--Each time exercise's iterated

    @Column(name = "slack_in_second", nullable = false)
    Integer slackInSecond;  //--Each time exercise's iterates

    @Column(name = "raise_slack_in_second", nullable = false)
    Integer raiseSlackInSecond; //--Each time exercise's iterates

    @Column(name = "iteration", nullable = false)
    @Min(1)
    Integer iteration;

    @Column(name = "need_switch_exercise_delay", columnDefinition = "BIT")
    @Min(0)
    @Max(1)
    boolean needSwitchExerciseDelay;    //--Each time exercise's ending
}

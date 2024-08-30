package com.restproject.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "subscription",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_info_id", "schedule_id"})
    }
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    Long subscriptionId;

    @ManyToOne(targetEntity = UserInfo.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_info_id", referencedColumnName = "user_info_id")
    @JsonIgnore
    UserInfo userInfo;

    @ManyToOne(targetEntity = Schedule.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "schedule_id", referencedColumnName = "schedule_id")
    @JsonIgnore
    Schedule schedule;

    @Column(name = "is_completed", nullable = false, columnDefinition = "BIT")
    boolean isCompleted;

    @Column(name = "subscribed_time", nullable = false, columnDefinition = "TIMESTAMP")
    LocalDateTime subscribedTime;

    @Column(name = "rep_ratio", nullable = false, columnDefinition = "TINYINT")
    @Min(0)
    @Max(100)
    Byte repRatio;

    @Column(name = "aim", nullable = false, columnDefinition = "TINYINT")
    @Min(-100)
    @Max(100)
    Byte aim;

    @Column(name = "base_tdee", nullable = false, columnDefinition = "UNSIGNED SMALLINT")
    Integer baseMaintainCalories;   //--TDEE
}

package com.restproject.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restproject.backend.enums.Aim;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "subscription")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    Long subscriptionId;

    @OneToOne(targetEntity = ChangingCoinsHistories.class)
    @JoinColumn(name = "changing_coins_histories_id", referencedColumnName = "changing_coins_histories_id",
        nullable = false, unique = true)
    ChangingCoinsHistories changingCoinsHistories;

    /**
     * NEED_VIRTUAL_UNIQUE_KEY: [1]_[user_info] can just subscribe [1]_[schedule]
     */
    @ManyToOne(targetEntity = Schedule.class)
    @JoinColumn(name = "schedule_id", referencedColumnName = "schedule_id")
    @JsonIgnore
    Schedule schedule;

    @Column(name = "completed_time", columnDefinition = "TIMESTAMP")
    LocalDateTime completedTime;

    @Column(name = "rep_ratio", nullable = false, columnDefinition = "TINYINT")
    @Min(0)
    @Max(100)
    Byte repRatio;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "aim", nullable = false)
    Aim aim;

    @Column(name = "efficient_days")
    Integer efficientDays;

    @Column(name = "bmr")
    Double bmr;

    @Column(name = "weight_aim")    //--new
    Float weightAim;

    @Column(name = "is_efficient", columnDefinition = "BIT")
    Boolean isEfficient;
}

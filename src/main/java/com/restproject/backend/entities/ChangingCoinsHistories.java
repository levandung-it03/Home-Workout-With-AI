package com.restproject.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restproject.backend.enums.ChangingCoinsType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "changing_coins_histories")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangingCoinsHistories {

    @Id
    @Column(name = "changing_coins_histories_id")
    String changingCoinsHistoriesId;  //--As Transaction-Reference

    @ManyToOne(targetEntity = UserInfo.class)
    @JoinColumn(name = "user_info_id", referencedColumnName = "user_info_id", nullable = false)
    @JsonIgnore
    UserInfo userInfo;

    @Column(name = "description")
    String description;

    @Column(name = "changing_coins", nullable = false)
    Long changingCoins;

    @Column(name = "changing_time", nullable = false, columnDefinition = "TIMESTAMP")
    LocalDateTime changingTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "changing_coins_type", nullable = false)
    ChangingCoinsType changingCoinsType;
}

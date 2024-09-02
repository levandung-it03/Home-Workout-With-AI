package com.restproject.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "completed_log")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompletedLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "completed_log_id")
    Long completedLogId;

    @ManyToOne(targetEntity = Subscription.class)
    @JoinColumn(name = "subscription_id", referencedColumnName = "subscription_id")
    @JsonIgnore
    Subscription subscription;

    @ManyToOne(targetEntity = Session.class)
    @JoinColumn(name = "session_id", referencedColumnName = "session_id")
    @JsonIgnore
    Session session;

    @Column(name = "completed_time", columnDefinition = "TIMESTAMP")
    LocalDateTime completedTime;
}

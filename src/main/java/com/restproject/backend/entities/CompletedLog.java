package com.restproject.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import net.minidev.json.annotate.JsonIgnore;

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

    @ManyToOne(targetEntity = Subscription.class, cascade = CascadeType.MERGE)
    @JoinColumn(name = "subscription_id", referencedColumnName = "subscription_id")
    @JsonIgnore
    Subscription subscription;

    @ManyToOne(targetEntity = Session.class, cascade = CascadeType.MERGE)
    @JoinColumn(name = "session_id", referencedColumnName = "session_id")
    @JsonIgnore
    Session session;

    @Column(name = "completed_time", columnDefinition = "TIMESTAMP")
    LocalDateTime completedTime;
}

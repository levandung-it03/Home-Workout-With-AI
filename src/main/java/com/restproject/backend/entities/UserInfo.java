package com.restproject.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restproject.backend.entities.Auth.User;
import com.restproject.backend.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_info")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_info_id")
    Long userInfoId;

    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @JsonIgnore
    User user;

    @Column(name = "first_name", nullable = false)
    String firstName;

    @Column(name = "last_name", nullable = false)
    String lastName;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "gender_enum", nullable = false)
    Gender gender;

    @Column(name = "dob", nullable = false, columnDefinition = "DATE")
    LocalDate dob;

    @Column(name = "coins", nullable = false)
    @Min(0)
    Long coins;
}

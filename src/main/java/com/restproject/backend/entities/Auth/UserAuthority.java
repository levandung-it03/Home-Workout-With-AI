package com.restproject.backend.entities.Auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_authorities")
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @JsonIgnore
    User user;

    @ManyToOne(targetEntity = Authority.class)
    @JoinColumn(name = "authority_id", referencedColumnName = "authority_id")
    @JsonIgnore
    Authority authority;
}

package com.restproject.backend.entities.Auth;


import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "RegisterOtp", timeToLive = 3600)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterOtp {
    @Id
    String id;
    String otpCode;
}

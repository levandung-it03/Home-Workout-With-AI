package com.restproject.backend.services.Auth;

import com.restproject.backend.entities.Auth.ChangePasswordOtp;
import com.restproject.backend.repositories.ChangePasswordOtpCrud;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChangePasswordOtpService {
    ChangePasswordOtpCrud otpCrud;

    public ChangePasswordOtp save(ChangePasswordOtp otp) {
        return otpCrud.save(otp);
    }

    public Optional<ChangePasswordOtp> findByEmail(String email) {
        return otpCrud.findById(email);
    }

    public void deleteByEmail(String email) {
        otpCrud.deleteById(email);
    }
}

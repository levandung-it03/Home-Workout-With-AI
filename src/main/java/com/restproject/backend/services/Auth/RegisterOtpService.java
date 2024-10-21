package com.restproject.backend.services.Auth;

import com.restproject.backend.entities.Auth.RegisterOtp;
import com.restproject.backend.repositories.RegisterOtpCrud;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegisterOtpService {
    RegisterOtpCrud otpCrud;

    public RegisterOtp save(RegisterOtp otp) {
        return otpCrud.save(otp);
    }

    public Optional<RegisterOtp> findByEmail(String email) {
        return otpCrud.findById(email);
    }

    public void deleteByEmail(String email) {
        otpCrud.deleteById(email);
    }
}

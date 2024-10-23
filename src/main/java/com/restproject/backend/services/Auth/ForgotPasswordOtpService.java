package com.restproject.backend.services.Auth;

import com.restproject.backend.entities.Auth.ForgotPasswordOtp;
import com.restproject.backend.repositories.ForgotPasswordOtpCrud;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ForgotPasswordOtpService {
    ForgotPasswordOtpCrud otpCrud;

    public ForgotPasswordOtp save(ForgotPasswordOtp otp) {
        return otpCrud.save(otp);
    }

    public Optional<ForgotPasswordOtp> findByEmail(String email) {
        return otpCrud.findById(email);
    }

    public void deleteByEmail(String email) {
        otpCrud.deleteById(email);
    }
}

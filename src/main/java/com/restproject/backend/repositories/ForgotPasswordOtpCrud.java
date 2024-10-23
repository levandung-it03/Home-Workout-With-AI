package com.restproject.backend.repositories;

import com.restproject.backend.entities.Auth.ForgotPasswordOtp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgotPasswordOtpCrud extends CrudRepository<ForgotPasswordOtp, String> {
}

package com.restproject.backend.repositories;

import com.restproject.backend.entities.Auth.RegisterOtp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisterOtpCrud extends CrudRepository<RegisterOtp, String> {
}

package com.restproject.backend.repositories;

import com.restproject.backend.entities.Auth.ChangePasswordOtp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangePasswordOtpCrud extends CrudRepository<ChangePasswordOtp, String> {
}

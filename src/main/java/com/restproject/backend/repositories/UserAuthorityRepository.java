package com.restproject.backend.repositories;

import com.restproject.backend.entities.Auth.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {
}

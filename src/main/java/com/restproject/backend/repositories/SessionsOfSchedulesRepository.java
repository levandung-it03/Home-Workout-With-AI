package com.restproject.backend.repositories;

import com.restproject.backend.entities.SessionsOfSchedules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionsOfSchedulesRepository extends JpaRepository<SessionsOfSchedules, Long> {
}

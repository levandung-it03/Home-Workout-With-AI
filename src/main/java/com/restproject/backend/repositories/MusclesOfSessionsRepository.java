package com.restproject.backend.repositories;

import com.restproject.backend.entities.MusclesOfSessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusclesOfSessionsRepository extends JpaRepository<MusclesOfSessions, Long> {
}

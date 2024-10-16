package com.restproject.backend.repositories;

import com.restproject.backend.entities.MuscleSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MuscleSessionRepository extends JpaRepository<MuscleSession, Long> {

    List<MuscleSession> findAllBySessionSessionId(Long sessionId);

    void deleteAllBySessionSessionId(Long sessionId);
}

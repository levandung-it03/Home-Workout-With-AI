package com.restproject.backend.repositories;

import com.restproject.backend.entities.ExercisesOfSessions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExercisesOfSessionsRepository extends JpaRepository<ExercisesOfSessions, Long> {
    boolean existsByExerciseExerciseId(Long id);

    Page<ExercisesOfSessions> findAllBySessionSessionId(Long sessionId, Pageable pageable);
}

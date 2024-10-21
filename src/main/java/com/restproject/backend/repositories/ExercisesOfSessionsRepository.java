package com.restproject.backend.repositories;

import com.restproject.backend.entities.ExercisesOfSessions;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExercisesOfSessionsRepository extends JpaRepository<ExercisesOfSessions, Long> {

    boolean existsByExerciseExerciseId(Long id);

    void deleteAllBySessionSessionId(Long id);

    @Query("""
        SELECT eos FROM ExercisesOfSessions eos WHERE eos.session.sessionId = :sessionId
    """)
    List<ExercisesOfSessions> findAllById(@Param("sessionId") Long sessionId);
}

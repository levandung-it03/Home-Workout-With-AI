package com.restproject.backend.repositories;

import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.ExercisesOfSessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExercisesOfSessionsRepository extends JpaRepository<ExercisesOfSessions, Long> {
    boolean existsByExercise(Exercise exercise);
}

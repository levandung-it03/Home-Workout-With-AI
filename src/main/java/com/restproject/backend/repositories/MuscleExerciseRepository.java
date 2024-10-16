package com.restproject.backend.repositories;

import com.restproject.backend.entities.MuscleExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MuscleExerciseRepository extends JpaRepository<MuscleExercise, Long> {

    List<MuscleExercise> findAllByExerciseExerciseId(Long exerciseId);

    void deleteAllByExerciseExerciseId(Long id);
}

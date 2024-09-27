package com.restproject.backend.repositories;

import com.restproject.backend.entities.Exercise;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    @Transactional
    @Modifying
    @Query("""
        UPDATE Exercise e
        SET e.imageUrl = :#{#updatedExercise.imageUrl},
            e.imagePublicId = :#{#updatedExercise.imagePublicId}
        WHERE e.exerciseId = :#{#updatedExercise.exerciseId}
    """)
    void updateImageUrlByExerciseId(@Param("updatedExercise") Exercise exercise);
}

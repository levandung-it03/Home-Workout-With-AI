package com.restproject.backend.repositories;

import com.restproject.backend.annotations.dev.Overload;
import com.restproject.backend.entities.Exercise;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    char GROUP_CONCAT_SEPARATOR = ',';

    @Transactional
    @Modifying
    @Query("""
        UPDATE Exercise e
        SET e.imageUrl = :#{#updatedExercise.imageUrl},
            e.imagePublicId = :#{#updatedExercise.imagePublicId}
        WHERE e.exerciseId = :#{#updatedExercise.exerciseId}
    """)
    void updateImageUrlByExerciseId(@Param("updatedExercise") Exercise exercise);

    @Modifying
    @Query("""
        UPDATE Exercise e
        SET e.name = :#{#exercise.name},
            e.levelEnum = :#{#exercise.levelEnum},
            e.basicReps = :#{#exercise.basicReps}
        WHERE e.exerciseId = :#{#exercise.exerciseId}
    """)
    void updateExerciseByExercise(@Param("exercise") Exercise exercise);

    @Query("SELECT e FROM Exercise e WHERE e.exerciseId IN :ids ORDER BY e.exerciseId ASC")
    List<Exercise> findAllByIdIn(@Param("ids") List<Long> ids);
}

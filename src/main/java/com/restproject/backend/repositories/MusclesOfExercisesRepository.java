package com.restproject.backend.repositories;

import com.restproject.backend.entities.MusclesOfExercises;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusclesOfExercisesRepository extends JpaRepository<MusclesOfExercises, Long> {

    @Query("""
        SELECT DISTINCT m FROM MusclesOfExercises m WHERE m.exercise.level = :level_enum AND m.muscle IN (:muscle_enums)
    """)
    List<MusclesOfExercises> findAllByLevelAndMuscles(
        @Param("level_enum") Level level,
        @Param("muscle_enums") List<Muscle> muscles
    );
}

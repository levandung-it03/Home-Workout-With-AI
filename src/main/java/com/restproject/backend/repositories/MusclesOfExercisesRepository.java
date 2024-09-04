package com.restproject.backend.repositories;

import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.MusclesOfExercises;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MusclesOfExercisesRepository extends JpaRepository<MusclesOfExercises, Long> {

    @Query("""
        SELECT m.exercise FROM MusclesOfExercises m
        WHERE m.exercise.level = :level_enum AND m.muscle IN (:muscle_enums)
        GROUP BY m.exercise.exerciseId
        ORDER BY m.exercise.exerciseId ASC
    """)
    List<Exercise> findAllExercisesByLevelAndMuscles(
        @Param("level_enum") Level level,
        @Param("muscle_enums") List<Muscle> muscles
    );

    List<MusclesOfExercises> findAllByExercise(Exercise exercise);

    void deleteAllByExercise(Exercise exercise) throws Exception;
}

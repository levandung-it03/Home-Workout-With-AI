package com.restproject.backend.repositories;

import com.restproject.backend.annotations.dev.Overload;
import com.restproject.backend.dtos.response.ExerciseHasMusclesResponse;
import com.restproject.backend.entities.MusclesOfExercises;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusclesOfExercisesRepository extends JpaRepository<MusclesOfExercises, Long> {
    char GROUP_CONCAT_SEPARATOR = ',';

    @Query("""
        SELECT m.exercise.exerciseId, m.exercise.name, m.exercise.basicReps, m.exercise.levelEnum,
            GROUP_CONCAT(m.muscleEnum), m.exercise.imageUrl
        FROM MusclesOfExercises m
        WHERE (:#{#filterObj.muscleList} IS NULL OR m.exercise.exerciseId IN (
            SELECT DISTINCT m.exercise.exerciseId AS exerciseId FROM MusclesOfExercises m
            WHERE m.muscleEnum IN :#{#filterObj.muscleList}
        ))
        AND (:#{#filterObj.levelEnum} IS NULL   OR :#{#filterObj.levelEnum} = m.exercise.levelEnum)
        AND (:#{#filterObj.basicReps} IS NULL   OR :#{#filterObj.basicReps} = m.exercise.basicReps)
        AND (:#{#filterObj.name} IS NULL        OR m.exercise.name LIKE CONCAT('%',:#{#filterObj.name},'%'))
        GROUP BY m.exercise.exerciseId
    """)
    Page<Object[]> findAllExercisesHasMuscles(
        @Param("filterObj") ExerciseHasMusclesResponse expectedExercise,
        Pageable pageable
    );

    @Overload
    @Query("""
        SELECT m.exercise.exerciseId, m.exercise.name, m.exercise.basicReps, m.exercise.levelEnum,
        GROUP_CONCAT(m.muscleEnum), m.exercise.imageUrl FROM MusclesOfExercises m GROUP BY m.exercise.exerciseId
    """)
    Page<Object[]> findAllExercisesHasMuscles(Pageable pageable);

    List<MusclesOfExercises> findAllByExerciseExerciseId(Long exerciseId);

    void deleteAllByExerciseExerciseId(Long id);
}

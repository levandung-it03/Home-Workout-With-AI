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

    /**
     * Required-initialization: ExerciseHasMusclesResponse(new Exercise(), Collections.emptyList())
     *
     * @return Object[] {Exercise.exerciseId, Exercise.name, Exercise.basicReps, Exercise.level, muscleList}
     */
    @Query(name = "findAllExercisesHasMusclesWithFiltering", nativeQuery = true, value = """
        SELECT e.exercise_id, e.name, e.basic_reps, e.level_enum, GROUP_CONCAT(
            DISTINCT moeft.muscle_enum
            ORDER BY moeft.muscle_enum ASC
            SEPARATOR '""" + GROUP_CONCAT_SEPARATOR
        + "')" + """
        FROM (
            SELECT moejid.exercise_id AS exercise_id, moejid.muscle_enum AS muscle_enum
            FROM muscles_of_exercises moejid
            WHERE :#{#filterObj.muscleList.isEmpty()} OR moejid.exercise_id IN (
                SELECT DISTINCT m.exercise_id FROM muscles_of_exercises m
                WHERE m.muscle_enum IN :#{#filterObj.muscleList}
            )
        ) AS moeft INNER JOIN exercise e ON e.exercise_id = moeft.exercise_id
        WHERE  (:#{#filterObj.exerciseId} IS NULL  OR :#{#filterObj.exerciseId} = e.exercise_id)
            AND (:#{#filterObj.level} IS NULL      OR :#{#filterObj.level} = e.level_enum)
            AND (:#{#filterObj.basicReps} IS NULL  OR :#{#filterObj.basicReps} = e.basic_reps)
            AND (:#{#filterObj.name} IS NULL       OR e.name LIKE CONCAT('%',:#{#filterObj.name},'%'))
        GROUP BY e.exercise_id
    """)
    Page<Object[]> findAllExercisesHasMuscles(
        @Param("filterObj") ExerciseHasMusclesResponse expectedExercise,
        Pageable pageable
    );

    /**
     * @return Object[] {Exercise.exerciseId, Exercise.name, Exercise.basicReps, Exercise.level, muscleList}
     */
    @Overload
    @Query(name = "findAllExercisesHasMuscles", nativeQuery = true, value = """
        SELECT e.exercise_id, e.name, e.basic_reps, e.level_enum, GROUP_CONCAT(
            DISTINCT m.muscle_enum
            ORDER BY m.muscle_enum
            ASC SEPARATOR '""" + GROUP_CONCAT_SEPARATOR + "') AS muscleList" + """
        FROM muscles_of_exercises m
        INNER JOIN exercise e ON e.exercise_id = m.exercise_id
        GROUP BY m.exercise_id
    """)
    Page<Object[]> findAllExercisesHasMuscles(Pageable pageable);

    List<MusclesOfExercises> findAllByExerciseExerciseId(Long exerciseId);

    void deleteAllByExerciseExerciseId(Long id);
}

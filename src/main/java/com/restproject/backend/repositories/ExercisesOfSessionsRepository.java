package com.restproject.backend.repositories;

import com.restproject.backend.dtos.response.ExercisesOfSessionResponse;
import com.restproject.backend.entities.ExercisesOfSessions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExercisesOfSessionsRepository extends JpaRepository<ExercisesOfSessions, Long> {
    char GROUP_CONCAT_SEPARATOR = ',';

    boolean existsByExerciseExerciseId(Long id);

    /**
     * Required-initialization: ExerciseHasMusclesResponse(new Exercise(), Collections.emptyList())
     *
     * @return Object[] {Exercise.exerciseId, Exercise.name, Exercise.basicReps, Exercise.level, boolean::withSession, muscleList}
     */
    @Query(name = "findAllExercisesHasMusclesWithFilteringOfSession", nativeQuery = true, value = """
        SELECT e.exercise_id, e.name, e.basic_reps, e.level_enum,
        (eos.session_id IS NOT NULL AND eos.session_id = :sessionId) AS withSession,
        GROUP_CONCAT(
            DISTINCT moeft.muscle_enum
            ORDER BY moeft.muscle_enum ASC
            SEPARATOR '""" + GROUP_CONCAT_SEPARATOR + "'" + """
        ) AS muscle_list, e.image_url
        FROM exercise e INNER JOIN (
            SELECT moejid.exercise_id AS exercise_id, moejid.muscle_enum AS muscle_enum
            FROM muscles_of_exercises moejid
            WHERE :#{#filterObj.muscleList.isEmpty()} OR moejid.exercise_id IN (
                SELECT DISTINCT m.exercise_id FROM muscles_of_exercises m
                WHERE m.muscle_enum IN :#{#filterObj.muscleList}
            )
        ) AS moeft ON e.exercise_id = moeft.exercise_id
        LEFT OUTER JOIN exercises_of_sessions eos ON eos.exercise_id = e.exercise_id
        WHERE (:#{#filterObj.levelEnum} IS NULL OR :#{#filterObj.levelEnum} = e.level_enum)
        AND (:#{#filterObj.basicReps} IS NULL OR :#{#filterObj.basicReps} = e.basic_reps)
        AND (:#{#filterObj.name} IS NULL    OR e.name LIKE CONCAT('%',:#{#filterObj.name},'%'))
        GROUP BY e.exercise_id, withSession
        ORDER BY withSession DESC
        """)
    Page<Object[]> findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
        @Param("sessionId") Long sessionId,
        @Param("filterObj") ExercisesOfSessionResponse exerciseInfo,
        Pageable pageable
    );

    /**
     * Required-initialization: ExerciseHasMusclesResponse(new Exercise(), Collections.emptyList())
     *
     * @return Object[] {Exercise.exerciseId, Exercise.name, Exercise.basicReps, Exercise.level, boolean::withSession, muscleList}
     */
    @Query(name = "findAllExercisesHasMusclesOfSession", nativeQuery = true, value = """
        SELECT e.exercise_id, e.name, e.basic_reps, e.level_enum,
        (eos.session_id IS NOT NULL AND eos.session_id = :sessionId) AS withSession,
        GROUP_CONCAT(
            DISTINCT moe.muscle_enum
            ORDER BY moe.muscle_enum ASC
            SEPARATOR '""" + GROUP_CONCAT_SEPARATOR + "'" + """
        ) AS muscle_list, e.image_url
        FROM exercise e
        INNER JOIN muscles_of_exercises moe ON e.exercise_id = moe.exercise_id
        LEFT OUTER JOIN exercises_of_sessions eos ON eos.exercise_id = e.exercise_id
        GROUP BY e.exercise_id, withSession ORDER BY withSession DESC
        """)
    Page<Object[]> findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
        @Param("sessionId") Long sessionId,
        Pageable pageable
    );

    void deleteAllBySessionSessionId(Long id);
}

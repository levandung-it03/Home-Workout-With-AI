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
//GROUP_CONCAT(moe.muscle, ',')
    boolean existsByExerciseExerciseId(Long id);

    /**
     * NOTE: In production env, we'll never have an Exercise Entity without relating to Muscles.
     * <br> So MusclesOfExercises always stores all Exercises (maybe larger as relationship).
     * @param sessionId as virtual joining condition
     * @param pageable as pagination condition
     * @return List.of(ExercisesOfSessionResponse)
     */
    @Query("""
        SELECT new com.restproject.backend.dtos.response.ExercisesOfSessionResponse(
            moe.exercise,
            "",
            CASE WHEN eos.session.sessionId = :sessionId THEN true ELSE false END
        )
        FROM MusclesOfExercises moe
        LEFT JOIN ExercisesOfSessions eos ON moe.exercise = eos.exercise
        GROUP BY moe.exercise, eos.session.sessionId
        ORDER BY moe.exercise.exerciseId, CASE WHEN eos.session.sessionId = :sessionId THEN 1 ELSE 2 END
    """)
    Page<ExercisesOfSessionResponse> findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
        @Param("sessionId") Long sessionId,
        Pageable pageable
    );

    void deleteAllBySessionSessionId(Long id);
}

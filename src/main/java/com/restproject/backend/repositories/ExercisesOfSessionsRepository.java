package com.restproject.backend.repositories;

import com.restproject.backend.dtos.request.ExercisesOfSessionRequest;
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

    @Query("""
        SELECT e.exerciseId, e.name, e.basicReps, e.levelEnum, e.muscles, eos.ordinal, eos.downRepsRatio,
            (CASE WHEN eos.session.sessionId = :sessionId THEN TRUE ELSE FALSE END) AS withCurrentSession, eos.slackInSecond,
            eos.raiseSlackInSecond, eos.iteration, eos.needSwitchExerciseDelay
        FROM Exercise e JOIN e.muscles m LEFT OUTER JOIN ExercisesOfSessions eos ON e.exerciseId = eos.exercise.exerciseId
        WHERE (:#{#filterObj.withCurrentSession} IS NULL
            OR :#{#filterObj.withCurrentSession} = CASE WHEN eos.session.sessionId = :sessionId THEN 1 ELSE 0 END)
        AND (:#{#filterObj.name} IS NULL OR e.name LIKE CONCAT('%',:#{#filterObj.name},'%'))
        AND (:#{#filterObj.basicReps} IS NULL OR :#{#filterObj.basicReps} = e.basicReps)
        AND (:#{#filterObj.levelEnum} IS NULL OR :#{#filterObj.levelEnum} = e.levelEnum)
        ORDER BY CASE WHEN eos.session.sessionId = :sessionId THEN 1 ELSE 0 END DESC, m.muscleName ASC
        """)
    Page<Object[]> findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
        @Param("sessionId") Long sessionId,
        @Param("filterObj") ExercisesOfSessionRequest exerciseInfo,
        Pageable pageable
    );

    @Query(value = """
        SELECT e.exerciseId, e.name, e.basicReps, e.levelEnum, e.muscles, eos.ordinal, eos.downRepsRatio,
            (CASE WHEN eos.session.sessionId = :sessionId THEN TRUE ELSE FALSE END) AS withCurrentSession, eos.slackInSecond,
            eos.raiseSlackInSecond, eos.iteration, eos.needSwitchExerciseDelay
        FROM Exercise e JOIN e.muscles m LEFT OUTER JOIN ExercisesOfSessions eos ON e.exerciseId = eos.exercise.exerciseId
        ORDER BY CASE WHEN eos.session.sessionId = :sessionId THEN 1 ELSE 0 END DESC, m.muscleName
    """)
    Page<Object[]> findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
        @Param("sessionId") Long sessionId,
        Pageable pageable
    );

    void deleteAllBySessionSessionId(Long id);
}

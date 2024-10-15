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

    @Query("""
        SELECT e.exerciseId, e.name, e.basicReps, e.levelEnum,
            CASE WHEN eos.session.sessionId = :sessionId THEN 1 ELSE 0 END, eos.ordinal, eos.downRepsRatio,
            eos.slackInSecond, eos.raiseSlackInSecond, eos.iteration, eos.needSwitchExerciseDelay
        FROM ExercisesOfSessions eos RIGHT OUTER JOIN Exercise e ON e.exerciseId = eos.exercise.exerciseId
        WHERE (:#{#filterObj.withCurrentSession} IS NULL
            OR :#{#filterObj.withCurrentSession} = CASE WHEN eos.session.sessionId = :sessionId THEN 1 ELSE 0 END)
        AND (:#{#filterObj.name} IS NULL OR e.name LIKE CONCAT('%',:#{#filterObj.name},'%'))
        AND (:#{#filterObj.basicReps} IS NULL OR :#{#filterObj.basicReps} = e.basicReps)
        AND (:#{#filterObj.levelEnum} IS NULL OR :#{#filterObj.levelEnum} = e.levelEnum)
        AND (:#{#filterObj.ordinal} IS NULL OR :#{#filterObj.ordinal} = eos.ordinal)
        AND (:#{#filterObj.downRepsRatio} IS NULL OR :#{#filterObj.downRepsRatio} = eos.downRepsRatio)
        AND (:#{#filterObj.slackInSecond} IS NULL OR :#{#filterObj.slackInSecond} = eos.slackInSecond)
        AND (:#{#filterObj.raiseSlackInSecond} IS NULL OR :#{#filterObj.raiseSlackInSecond} = eos.raiseSlackInSecond)
        AND (:#{#filterObj.iteration} IS NULL OR :#{#filterObj.iteration} = eos.iteration)
        AND (:#{#filterObj.needSwitchExerciseDelay} IS NULL OR :#{#filterObj.needSwitchExerciseDelay} = eos.needSwitchExerciseDelay)
        ORDER BY CASE WHEN eos.session.sessionId = :sessionId THEN 1 ELSE 0 END DESC
        """)
    Page<Object[]> findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
        @Param("sessionId") Long sessionId,
        @Param("filterObj") ExercisesOfSessionResponse exerciseInfo,
        Pageable pageable
    );

    @Query(value = """
        SELECT e.exerciseId, e.name, e.basicReps, e.levelEnum,
            CASE WHEN eos.session.sessionId = :sessionId THEN 1 ELSE 0 END, e.imageUrl,
        eos.ordinal, eos.downRepsRatio, eos.slackInSecond, eos.raiseSlackInSecond
        FROM ExercisesOfSessions eos RIGHT OUTER JOIN Exercise e ON e.exerciseId = eos.exercise.exerciseId
        ORDER BY CASE WHEN eos.session.sessionId = :sessionId THEN 1 ELSE 0 END DESC
    """)
    Page<Object[]> findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
        @Param("sessionId") Long sessionId,
        Pageable pageable
    );

    void deleteAllBySessionSessionId(Long id);
}

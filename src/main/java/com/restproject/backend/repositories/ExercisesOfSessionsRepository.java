package com.restproject.backend.repositories;

import com.restproject.backend.dtos.general.ExerciseInfoDto;
import com.restproject.backend.dtos.request.ExercisesOfSessionRequest;
import com.restproject.backend.dtos.response.ExercisesOfSessionResponse;
import com.restproject.backend.entities.ExercisesOfSessions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExercisesOfSessionsRepository extends JpaRepository<ExercisesOfSessions, Long> {

    boolean existsByExerciseExerciseId(Long id);

    @Query("""
        SELECT new com.restproject.backend.dtos.response.ExercisesOfSessionResponse(
            e, :sessionId, eos.session.sessionId, eos.ordinal, eos.downRepsRatio, eos.slackInSecond,
            eos.raiseSlackInSecond, eos.iteration, eos.needSwitchExerciseDelay
        ) FROM Exercise e LEFT OUTER JOIN ExercisesOfSessions eos ON e.exerciseId = eos.exercise.exerciseId
        JOIN e.muscles m
        WHERE (:#{#filterObj.withCurrentSession} IS NULL
            OR :#{#filterObj.withCurrentSession} = (CASE WHEN eos.session.sessionId = :sessionId THEN TRUE ELSE FALSE END))
        AND (:#{#filterObj.name} IS NULL OR e.name LIKE CONCAT('%',:#{#filterObj.name},'%'))
        AND (:#{#filterObj.basicReps} IS NULL OR :#{#filterObj.basicReps} = e.basicReps)
        AND (:#{#filterObj.levelEnum} IS NULL OR :#{#filterObj.levelEnum} = e.levelEnum)
        ORDER BY CASE WHEN eos.session.sessionId = :sessionId THEN 1 ELSE 0 END DESC, m.muscleName ASC
        """)
    Page<ExercisesOfSessionResponse> findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
        @Param("sessionId") Long sessionId,
        @Param("filterObj") ExercisesOfSessionRequest exerciseInfo,
        Pageable pageable
    );

    @Query(value = """
        SELECT new com.restproject.backend.dtos.response.ExercisesOfSessionResponse(
            e, :sessionId, eos.session.sessionId, eos.ordinal, eos.downRepsRatio, eos.slackInSecond,
            eos.raiseSlackInSecond, eos.iteration, eos.needSwitchExerciseDelay
        ) FROM Exercise e LEFT OUTER JOIN ExercisesOfSessions eos ON e.exerciseId = eos.exercise.exerciseId
        JOIN e.muscles m
        ORDER BY CASE WHEN eos.session.sessionId = :sessionId THEN 1 ELSE 0 END DESC, m.muscleName ASC
    """)
    Page<ExercisesOfSessionResponse> findAllExercisesHasMusclesPrioritizeRelationshipBySessionId(
        @Param("sessionId") Long sessionId,
        Pageable pageable
    );

    void deleteAllBySessionSessionId(Long id);

    @Query("""
        SELECT new com.restproject.backend.dtos.general.ExerciseInfoDto(
            eos.exercise.exerciseId, eos.ordinal, eos.downRepsRatio, eos.slackInSecond, eos.raiseSlackInSecond,
            eos.iteration, eos.needSwitchExerciseDelay
        ) FROM ExercisesOfSessions eos WHERE eos.session.sessionId = :sessionId
    """)
    List<ExerciseInfoDto> findAllById(@Param("sessionId") Long sessionId);
}

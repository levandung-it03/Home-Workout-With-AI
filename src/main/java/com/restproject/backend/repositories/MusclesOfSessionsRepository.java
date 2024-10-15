package com.restproject.backend.repositories;

import com.restproject.backend.annotations.dev.Overload;
import com.restproject.backend.dtos.response.SessionHasMusclesResponse;
import com.restproject.backend.entities.MusclesOfSessions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusclesOfSessionsRepository extends JpaRepository<MusclesOfSessions, Long> {
    char GROUP_CONCAT_SEPARATOR = ',';

    @Query("""
        SELECT m.session.sessionId, m.session.name, m.session.levelEnum, m.session.description,
            GROUP_CONCAT(m.muscleEnum), m.session.switchExerciseDelay
        FROM MusclesOfSessions m
        WHERE (:#{#filterObj.muscleList} IS NULL OR m.session.sessionId IN (
            SELECT DISTINCT m.session.sessionId AS exerciseId FROM MusclesOfSessions m
            WHERE m.muscleEnum IN :#{#filterObj.muscleList}
        ))
        AND (:#{#filterObj.levelEnum} IS NULL   OR :#{#filterObj.levelEnum} = m.session.levelEnum)
        AND (:#{#filterObj.switchExerciseDelay} IS NULL OR :#{#filterObj.switchExerciseDelay} = m.session.switchExerciseDelay)
        AND (:#{#filterObj.name} IS NULL        OR m.session.name LIKE CONCAT('%',:#{#filterObj.name},'%'))
        GROUP BY m.session.sessionId
    """)
    Page<Object[]> findAllSessionsHasMuscles(
        @Param("filterObj") SessionHasMusclesResponse expectedSession,
        Pageable pageable
    );

    /**
     * @return Object[] {Session.sessionId, Session.name, Session.basicReps, Session.levelEnum, muscleList}
     */
    @Overload
    @Query("""
        SELECT m.session.sessionId, m.session.name, m.session.levelEnum, m.session.description,
        GROUP_CONCAT(m.muscleEnum), m.session.switchExerciseDelay FROM MusclesOfSessions m GROUP BY m.session.sessionId
    """)
    Page<Object[]> findAllSessionsHasMuscles(Pageable pageable);

    List<MusclesOfSessions> findAllBySessionSessionId(Long sessionId);

    void deleteAllBySessionSessionId(Long sessionId);
}

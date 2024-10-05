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

    /**
     * Required-initialization: SessionHasMusclesResponse(new Session(), Collections.emptyList())
     *
     * @return Object[] {Session.sessionId, Session.name, Session.basicReps, Session.levelEnum, muscleList}
     */
    @Query(name = "findAllSessionsHasMusclesWithFiltering", nativeQuery = true, value = """
        SELECT s.session_id, s.name, s.description, s.level_enum, GROUP_CONCAT(
            DISTINCT mosft.muscle_enum
            ORDER BY mosft.muscle_enum ASC
            SEPARATOR '""" + GROUP_CONCAT_SEPARATOR + "') AS muscleList" + """
        FROM session s INNER JOIN (
            SELECT mosjid.session_id AS session_id, mosjid.muscle_enum AS muscle_enum
            FROM muscles_of_sessions mosjid
            WHERE :#{#filterObj.muscleList.isEmpty()} OR mosjid.session_id IN (
                SELECT DISTINCT m.session_id FROM muscles_of_sessions m
                WHERE m.muscle_enum IN :#{#filterObj.muscleList}
            )
        ) AS mosft ON s.session_id = mosft.session_id
        WHERE  (:#{#filterObj.sessionId} IS NULL OR :#{#filterObj.sessionId} = s.session_id)
            AND (:#{#filterObj.levelEnum} IS NULL   OR :#{#filterObj.levelEnum} = s.level_enum)
            AND (:#{#filterObj.name} IS NULL    OR s.name LIKE CONCAT('%',:#{#filterObj.name},'%'))
        GROUP BY s.session_id
    """)
    Page<Object[]> findAllSessionsHasMuscles(
        @Param("filterObj") SessionHasMusclesResponse expectedSession,
        Pageable pageable
    );

    /**
     * @return Object[] {Session.sessionId, Session.name, Session.basicReps, Session.levelEnum, muscleList}
     */
    @Overload
    @Query(name = "findAllSessionsHasMuscles", nativeQuery = true, value = """
        SELECT s.session_id, s.name, s.description, s.level_enum, GROUP_CONCAT(
            DISTINCT m.muscle_enum
            ORDER BY m.muscle_enum
            ASC SEPARATOR '""" + GROUP_CONCAT_SEPARATOR
        + "') " + """
        FROM session s
        INNER JOIN muscles_of_sessions m ON s.session_id = m.session_id
        GROUP BY m.session_id
    """)
    Page<Object[]> findAllSessionsHasMuscles(Pageable pageable);

    List<MusclesOfSessions> findAllBySessionSessionId(Long sessionId);

    void deleteAllBySessionSessionId(Long sessionId);
}

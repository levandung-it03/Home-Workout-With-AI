package com.restproject.backend.repositories;

import com.restproject.backend.dtos.response.SessionsOfScheduleResponse;
import com.restproject.backend.entities.SessionsOfSchedules;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionsOfSchedulesRepository extends JpaRepository<SessionsOfSchedules, Long> {
    char GROUP_CONCAT_SEPARATOR = ',';

    boolean existsBySessionSessionId(Long sessionId);

    void deleteAllByScheduleScheduleId(Long id);

    boolean existsByScheduleScheduleId(Long scheduleId);

    /**
     * Required-initialization: SessionsOfScheduleResponse(new Session(), Collections.emptyList())
     *
     * @return Object[] {Session.sessionId, Session.name, Session.level, Session.description, boolean::withSchedule, muscleList}
     */
    @Query(name = "findAllExercisesHasMusclesWithFilteringOfSession", nativeQuery = true, value = """
        SELECT s.session_id, s.name, s.level_enum, s.description,
        (sos.schedule_id IS NOT NULL AND sos.schedule_id = :scheduleId) AS withSchedule,
        GROUP_CONCAT(
            DISTINCT mosft.muscle_enum
            ORDER BY mosft.muscle_enum ASC
            SEPARATOR '""" + GROUP_CONCAT_SEPARATOR + "'" + """
        ) AS muscleList
        FROM (
            SELECT mosjid.session_id AS session_id, mosjid.muscle_enum AS muscle_enum
            FROM muscles_of_sessions mosjid
            WHERE :#{#filterObj.muscleList.isEmpty()} OR mosjid.session_id IN (
                SELECT DISTINCT m.session_id FROM muscles_of_sessions m
                WHERE m.muscle_enum IN :#{#filterObj.muscleList}
            )
        ) AS mosft INNER JOIN session s ON s.session_id = mosft.session_id
        LEFT OUTER JOIN sessions_of_schedules sos ON sos.session_id = s.session_id
        WHERE (:#{#filterObj.level} IS NULL OR :#{#filterObj.level} = s.level_enum)
        AND (:#{#filterObj.name} IS NULL OR s.name LIKE CONCAT('%',:#{#filterObj.name},'%'))
        GROUP BY s.session_id, withSchedule
        ORDER BY withSchedule DESC
        """)
    Page<Object[]> findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(
        @Param("scheduleId") Long id,
        @Param("filterObj") SessionsOfScheduleResponse filteringInfo,
        Pageable pageableCf
    );

    /**
     * Required-initialization: SessionsOfScheduleResponse(new Session(), Collections.emptyList())
     *
     * @return Object[] {Session.sessionId, Session.name, Session.description, Session.level, muscleList}
     */
    @Query(name = "findAllSessionsHasMusclesOfSchedule", nativeQuery = true, value = """
        SELECT s.session_id, s.name, s.level_enum, s.description,
        (sos.schedule_id IS NOT NULL AND sos.schedule_id = :scheduleId) AS withSchedule,
        GROUP_CONCAT(
            DISTINCT mos.muscle_enum
            ORDER BY mos.muscle_enum ASC
            SEPARATOR '""" + GROUP_CONCAT_SEPARATOR + "'" + """
        ) AS muscleList
        FROM muscles_of_sessions mos INNER JOIN session s ON s.session_id = mos.session_id
        LEFT OUTER JOIN sessions_of_schedules sos ON sos.session_id = s.session_id
        GROUP BY s.session_id, withSchedule
        ORDER BY withSchedule DESC
        """)
    Page<Object[]> findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(
        @Param("scheduleId") Long id,
        Pageable pageableCf
    );
}

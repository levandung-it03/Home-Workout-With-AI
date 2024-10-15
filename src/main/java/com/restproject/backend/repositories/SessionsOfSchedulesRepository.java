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

    @Query("""
        SELECT s.sessionId, s.name, s.levelEnum,
            CASE WHEN sos.schedule.scheduleId = :scheduleId THEN 1 ELSE 0 END, s.switchExerciseDelay, sos.ordinal
        FROM SessionsOfSchedules sos RIGHT OUTER JOIN Session s ON sos.session.sessionId = s.sessionId
        WHERE (:#{#filterObj.withCurrentSchedule} IS NULL
            OR :#{#filterObj.withCurrentSchedule} = CASE WHEN sos.schedule.scheduleId = :scheduleId THEN 1 ELSE 0 END)
        AND (:#{#filterObj.levelEnum} IS NULL OR :#{#filterObj.levelEnum} = s.levelEnum)
        AND (:#{#filterObj.ordinal} IS NULL OR :#{#filterObj.ordinal} = sos.ordinal)
        AND (:#{#filterObj.switchExerciseDelay} IS NULL OR :#{#filterObj.switchExerciseDelay} = s.switchExerciseDelay)
        AND (:#{#filterObj.name} IS NULL OR s.name LIKE CONCAT('%',:#{#filterObj.name},'%'))
        ORDER BY CASE WHEN sos.schedule.scheduleId = :scheduleId THEN 1 ELSE 0 END DESC
    """)
    Page<Object[]> findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(
        @Param("scheduleId") Long id,
        @Param("filterObj") SessionsOfScheduleResponse filteringInfo,
        Pageable pageableCf
    );

    @Query("""
        SELECT s.sessionId, s.name, s.levelEnum,
            CASE WHEN sos.schedule.scheduleId = :scheduleId THEN 1 ELSE 0 END, sos.ordinal
        FROM SessionsOfSchedules sos RIGHT OUTER JOIN Session s ON sos.session.sessionId = s.sessionId
        ORDER BY CASE WHEN sos.schedule.scheduleId = :scheduleId THEN 1 ELSE 0 END DESC
    """)
    Page<Object[]> findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(
        @Param("scheduleId") Long id,
        Pageable pageableCf
    );
}

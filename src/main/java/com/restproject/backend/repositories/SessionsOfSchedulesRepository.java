package com.restproject.backend.repositories;

import com.restproject.backend.dtos.request.SessionsOfScheduleRequest;
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
        SELECT s.sessionId, s.name, s.levelEnum, s.switchExerciseDelay, sos.ordinal,
            (CASE WHEN sos.schedule.scheduleId = :scheduleId THEN TRUE ELSE FALSE END) AS withCurrentSchedule
        FROM Session s JOIN s.muscles m LEFT OUTER JOIN SessionsOfSchedules sos ON sos.session.sessionId = s.sessionId
        WHERE (:#{#filterObj.withCurrentSchedule} IS NULL
            OR :#{#filterObj.withCurrentSchedule} = CASE WHEN sos.schedule.scheduleId = :scheduleId THEN 1 ELSE 0 END)
        AND (:#{#filterObj.levelEnum} IS NULL OR :#{#filterObj.levelEnum} = s.levelEnum)
        AND (:#{#filterObj.switchExerciseDelay} IS NULL OR :#{#filterObj.switchExerciseDelay} = s.switchExerciseDelay)
        AND (:#{#filterObj.name} IS NULL OR s.name LIKE CONCAT('%',:#{#filterObj.name},'%'))
        ORDER BY CASE WHEN sos.schedule.scheduleId = :scheduleId THEN 1 ELSE 0 END DESC, m.muscleName ASC
    """)
    Page<Object[]> findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(
        @Param("scheduleId") Long id,
        @Param("filterObj") SessionsOfScheduleRequest filteringInfo,
        Pageable pageableCf
    );

    @Query("""
        SELECT s.sessionId, s.name, s.levelEnum, s.switchExerciseDelay, sos.ordinal,
            (CASE WHEN sos.schedule.scheduleId = :scheduleId THEN TRUE ELSE FALSE END) AS withCurrentSchedule
        FROM Session s JOIN s.muscles m LEFT OUTER JOIN SessionsOfSchedules sos ON sos.session.sessionId = s.sessionId
        ORDER BY CASE WHEN sos.schedule.scheduleId = :scheduleId THEN 1 ELSE 0 END DESC, m.muscleName ASC
    """)
    Page<Object[]> findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(
        @Param("scheduleId") Long id,
        Pageable pageableCf
    );
}

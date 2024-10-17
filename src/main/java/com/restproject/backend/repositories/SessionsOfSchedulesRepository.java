package com.restproject.backend.repositories;

import com.restproject.backend.dtos.general.ExerciseInfoDto;
import com.restproject.backend.dtos.general.SessionInfoDto;
import com.restproject.backend.dtos.request.SessionsOfScheduleRequest;
import com.restproject.backend.dtos.response.SessionsOfScheduleResponse;
import com.restproject.backend.entities.SessionsOfSchedules;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionsOfSchedulesRepository extends JpaRepository<SessionsOfSchedules, Long> {

    boolean existsBySessionSessionId(Long sessionId);

    void deleteAllByScheduleScheduleId(Long id);

    boolean existsByScheduleScheduleId(Long scheduleId);

    @Query("""
        SELECT new com.restproject.backend.dtos.response.SessionsOfScheduleResponse(
            s, sos.schedule.scheduleId, :scheduleId, sos.ordinal
        ) FROM Session s LEFT OUTER JOIN SessionsOfSchedules sos ON sos.session.sessionId = s.sessionId
        JOIN s.muscles m
        WHERE (:#{#filterObj.withCurrentSchedule} IS NULL
            OR :#{#filterObj.withCurrentSchedule} = CASE WHEN sos.schedule.scheduleId = :scheduleId THEN TRUE ELSE FALSE END)
        AND (:#{#filterObj.levelEnum} IS NULL OR :#{#filterObj.levelEnum} = s.levelEnum)
        AND (:#{#filterObj.switchExerciseDelay} IS NULL OR :#{#filterObj.switchExerciseDelay} = s.switchExerciseDelay)
        AND (:#{#filterObj.name} IS NULL OR s.name LIKE CONCAT('%',:#{#filterObj.name},'%'))
        ORDER BY CASE WHEN sos.schedule.scheduleId = :scheduleId THEN 1 ELSE 0 END DESC, m.muscleName ASC
    """)
    Page<SessionsOfScheduleResponse> findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(
        @Param("scheduleId") Long id,
        @Param("filterObj") SessionsOfScheduleRequest filteringInfo,
        Pageable pageableCf
    );

    @Query("""
        SELECT new com.restproject.backend.dtos.response.SessionsOfScheduleResponse(
            s, sos.schedule.scheduleId, :scheduleId, sos.ordinal
        ) FROM Session s LEFT OUTER JOIN SessionsOfSchedules sos ON sos.session.sessionId = s.sessionId
        JOIN s.muscles m
        ORDER BY CASE WHEN sos.schedule.scheduleId = :scheduleId THEN 1 ELSE 0 END DESC, m.muscleName ASC
    """)
    Page<SessionsOfScheduleResponse> findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(
        @Param("scheduleId") Long id,
        Pageable pageableCf
    );

    @Query("""
        SELECT new com.restproject.backend.dtos.general.SessionInfoDto(sos.session.sessionId, sos.ordinal)
        FROM SessionsOfSchedules sos WHERE sos.schedule.scheduleId = :scheduleId
    """)
    List<SessionInfoDto> findAllById(@Param("scheduleId") Long scheduleId);
}

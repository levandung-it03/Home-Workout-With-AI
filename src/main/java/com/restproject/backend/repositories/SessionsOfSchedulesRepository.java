package com.restproject.backend.repositories;

import com.restproject.backend.entities.Session;
import com.restproject.backend.entities.SessionsOfSchedules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionsOfSchedulesRepository extends JpaRepository<SessionsOfSchedules, Long> {

    boolean existsBySessionSessionId(Long sessionId);

    void deleteAllByScheduleScheduleId(Long id);

    boolean existsByScheduleScheduleId(Long scheduleId);

    @Query("""
        SELECT sos FROM SessionsOfSchedules sos WHERE sos.schedule.scheduleId = :scheduleId ORDER BY sos.ordinal ASC
    """)
    List<SessionsOfSchedules> findAllById(@Param("scheduleId") Long scheduleId);
}

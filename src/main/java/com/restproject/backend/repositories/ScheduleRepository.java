package com.restproject.backend.repositories;

import com.restproject.backend.entities.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("""
        SELECT s FROM Schedule s
        WHERE (:#{#filterObj.name} IS NULL OR s.name LIKE CONCAT('%',:#{#filterObj.name},'%'))
        AND (:#{#filterObj.level} IS NULL OR s.level = :#{#filterObj.level})
        AND (:#{#filterObj.coins} IS NULL OR s.coins = :#{#filterObj.coins})
    """)
    Page<Schedule> findAllBySchedule(@Param("filterObj") Schedule filteringInfo, Pageable pageable);

    @Modifying
    @Query("""
        UPDATE Schedule s
        SET s.name = :#{#schedule.name},
            s.description = :#{#schedule.description},
            s.coins = :#{#schedule.coins},
            s.level = :#{#schedule.level}
        WHERE s.scheduleId = :#{#schedule.scheduleId}
    """)
    void updateScheduleBySchedule(@Param("schedule") Schedule formerSch);
}

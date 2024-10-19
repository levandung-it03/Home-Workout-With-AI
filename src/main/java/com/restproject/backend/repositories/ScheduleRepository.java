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
        AND (:#{#filterObj.description} IS NULL OR s.description LIKE CONCAT('%',:#{#filterObj.description},'%'))
        AND (:#{#filterObj.levelEnum} IS NULL OR s.levelEnum = :#{#filterObj.levelEnum})
        AND (:#{#filterObj.coins} IS NULL OR s.coins = :#{#filterObj.coins})
    """)
    Page<Schedule> findAllBySchedule(@Param("filterObj") Schedule filteringInfo, Pageable pageable);

    @Modifying
    @Query("""
        UPDATE Schedule s
        SET s.name = :#{#schedule.name},
            s.description = :#{#schedule.description},
            s.coins = :#{#schedule.coins},
            s.levelEnum = :#{#schedule.levelEnum}
        WHERE s.scheduleId = :#{#schedule.scheduleId}
    """)
    void updateScheduleBySchedule(@Param("schedule") Schedule formerSch);

    @Query("""
        SELECT s FROM Schedule s WHERE s.scheduleId NOT IN (
            SELECT DISTINCT sub.schedule.scheduleId FROM Subscription sub
            WHERE sub.userInfo.user.email = :email
        )
    """)
    Page<Schedule> findAllAvailableScheduleOfUser(@Param("email") String email, Pageable pageableCf);


    @Query("""
        SELECT s FROM Schedule s WHERE s.scheduleId NOT IN (
            SELECT DISTINCT sub.schedule.scheduleId FROM Subscription sub
            WHERE sub.userInfo.user.email = :email
        ) AND (:#{#filterObj.levelEnum} IS NULL OR :#{#filterObj.levelEnum} = s.levelEnum)
         AND (:#{#filterObj.coins} IS NULL OR :#{#filterObj.coins} = s.coins)
    """)
    Page<Schedule> findAllAvailableScheduleOfUser(
        @Param("email") String email,
        @Param("filterObj") Schedule schedule,
        Pageable pageableCf
    );
}

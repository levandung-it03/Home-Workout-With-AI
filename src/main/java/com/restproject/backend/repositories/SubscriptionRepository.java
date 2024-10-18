package com.restproject.backend.repositories;

import com.restproject.backend.dtos.request.SubscriptionsInfoRequest;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.entities.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("""
        SELECT s.schedule FROM Subscription s
        WHERE s.userInfo.user.email = :email
        AND ((s.completedTime IS NULL AND :isCompleted = 0) OR (s.completedTime IS NOT NULL AND :isCompleted = 1))
    """)
    List<Schedule> getAllScheduleByUsernameAndStatus(
        @Param("email") String email, @Param("isCompleted") Integer isCompleted);

    @Query("""
        SELECT s.subscriptionId, s.userInfo.firstName, s.userInfo.lastName, s.subscribedTime, s.efficientDays,
            s.schedule.name, s.schedule.levelEnum, s.schedule.coins, s.completedTime
        FROM Subscription s WHERE s.userInfo.userInfoId = :id
    """)
    Page<Object[]> findAddNeededSubscriptionsInfoByUserInfoId(
        @Param("id") Long id, Pageable pageableCfg);

    @Query("""
        SELECT s.subscriptionId, s.userInfo.firstName, s.userInfo.lastName, s.subscribedTime, s.efficientDays,
            s.schedule.name, s.schedule.levelEnum, s.schedule.coins, s.completedTime
        FROM Subscription s WHERE (s.userInfo.userInfoId = :id)
        AND (:#{#filterObj.firstName} IS NULL OR s.userInfo.firstName LIKE CONCAT('%',:#{#filterObj.firstName},'%'))
        AND (:#{#filterObj.lastName} IS NULL OR s.userInfo.lastName LIKE CONCAT('%',:#{#filterObj.lastName},'%'))
        AND (:#{#filterObj.fromSubscribedTime} IS NULL OR :#{#filterObj.fromSubscribedTime} <= s.subscribedTime)
        AND (:#{#filterObj.toSubscribedTime} IS NULL OR s.subscribedTime <= :#{#filterObj.toSubscribedTime})
        AND (:#{#filterObj.efficientDays} IS NULL OR s.efficientDays = :#{#filterObj.efficientDays})
        AND (:#{#filterObj.scheduleName} IS NULL OR s.schedule.name LIKE CONCAT('%',:#{#filterObj.scheduleName},'%'))
        AND (:#{#filterObj.scheduleLevelEnum} IS NULL OR s.schedule.levelEnum = :#{#filterObj.scheduleLevelEnum})
        AND (:#{#filterObj.scheduleCoins} IS NULL OR s.schedule.coins = :#{#filterObj.scheduleCoins})
        AND (:#{#filterObj.fromCompletedTime} IS NULL OR :#{#filterObj.fromCompletedTime} <= s.completedTime)
        AND (:#{#filterObj.toCompletedTime} IS NULL OR s.completedTime <= :#{#filterObj.toCompletedTime})
    """)
    Page<Object[]> findAddNeededSubscriptionsInfoByUserInfoId(
        @Param("id") Long id,
        @Param("filterObj") SubscriptionsInfoRequest filterObj,
        Pageable pageableCfg
    );
}

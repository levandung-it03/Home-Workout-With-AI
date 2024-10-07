package com.restproject.backend.repositories;

import com.restproject.backend.entities.Schedule;
import com.restproject.backend.entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("""
        SELECT s.schedule FROM Subscription s WHERE s.userInfo.user.email = :email
        AND ((s.completedTime IS NOT NULL AND :isCompleted = TRUE) OR (s.completedTime IS NULL AND :isCompleted = FALSE))
    """)
    List<Schedule> getAllScheduleByUsernameAndStatus(
        @Param("email") String email, @Param("isCompleted") Boolean isCompleted);
}

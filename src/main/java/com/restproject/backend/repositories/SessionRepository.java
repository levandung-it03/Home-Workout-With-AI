package com.restproject.backend.repositories;

import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByLevel(Level level);

    @Query("""
        SELECT s FROM Session s
        WHERE   (:#{#session.sessionId} IS NULL OR s.sessionId = :#{#session.sessionId})
            AND (:#{#session.name} IS NULL OR s.name LIKE %:#{#session.name}%)
            AND (:#{#session.level} IS NULL OR s.level = :#{#session.level})
            AND (:#{#session.description} IS NULL OR s.name LIKE %:#{#session.description}%)
    """)
    Page<Session> findAllByFilteringSession(@Param("session") Session session, Pageable pageable);
}

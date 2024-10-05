package com.restproject.backend.repositories;

import com.restproject.backend.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    @Modifying
    @Query("""
        UPDATE Session s
        SET s.name = :#{#session.name},
            s.levelEnum = :#{#session.levelEnum},
            s.description = :#{#session.description}
        WHERE s.sessionId = :#{#session.sessionId}
    """)
    void updateSessionBySession(@Param("session") Session formerSes);
}

package com.restproject.backend.repositories;

import com.restproject.backend.dtos.response.SessionHasMusclesResponse;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    @Modifying
    @Query("""
        UPDATE Session s
        SET s.name = :#{#session.name},
            s.level = :#{#session.level},         
            s.description = :#{#session.description}
        WHERE s.sessionId = :#{#session.sessionId}
    """)
    void updateSessionBySession(@Param("session") Session formerSes);
}

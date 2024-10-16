package com.restproject.backend.repositories;

import com.restproject.backend.dtos.request.SessionPagesRequest;
import com.restproject.backend.entities.Session;
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
            s.levelEnum = :#{#session.levelEnum},
            s.description = :#{#session.description}
        WHERE s.sessionId = :#{#session.sessionId}
    """)
    void updateSessionBySession(@Param("session") Session formerSes);

    @Query("SELECT s FROM Session s WHERE s.sessionId IN :ids ORDER BY s.sessionId ASC")
    List<Session> findAllByIdIn(@Param("ids") List<Long> ids);


    @Query("""
        SELECT s FROM Session s JOIN s.muscles m
        WHERE (:#{#filterObj.muscleIds} IS NULL OR m.muscleId IN :#{#filterObj.muscleIds})
        AND (:#{#filterObj.levelEnum} IS NULL   OR :#{#filterObj.levelEnum} = s.levelEnum)
        AND (:#{#filterObj.switchExerciseDelay} IS NULL OR :#{#filterObj.switchExerciseDelay} = s.switchExerciseDelay)
        AND (:#{#filterObj.name} IS NULL        OR s.name LIKE CONCAT('%',:#{#filterObj.name},'%'))
        AND (:#{#filterObj.description} IS NULL OR s.description LIKE CONCAT('%',:#{#filterObj.description},'%'))
        ORDER BY m.muscleName ASC
    """)
    Page<Session> findAllSessionsCustom(@Param("filterObj") SessionPagesRequest sessionPagesRequest, Pageable pageableCfg);
}

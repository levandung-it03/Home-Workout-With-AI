package com.restproject.backend.repositories;

import com.restproject.backend.config.RedisConfig;
import com.restproject.backend.dtos.response.SessionHasMusclesResponse;
import com.restproject.backend.entities.Session;
import com.restproject.backend.entities.MusclesOfSessions;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(RedisConfig.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MusclesOfSessionsRepositoryTest {
    @Autowired
    MusclesOfSessionsRepository musclesOfSessionsRepository;
    @Autowired
    SessionRepository sessionRepository;

    @Test
    public void findAllSessionsHasMuscles_admin_valid() {
        var sessions = new ArrayList<>(sessionRepository.saveAll(List.of(
            Session.builder().levelEnum(Level.BEGINNER).name("Test Session1").description("Testing").build(),
            Session.builder().levelEnum(Level.INTERMEDIATE).name("Test Session2").description("Testing").build(),
            Session.builder().levelEnum(Level.INTERMEDIATE).name("Test Session3").description("Testing").build(),
            Session.builder().levelEnum(Level.ADVANCE).name("Test Session4").description("Testing").build(),
            Session.builder().levelEnum(Level.BEGINNER).name("Test Session5").description("Testing").build()
        )));
        var sessionHasMusclesFromDB = new ArrayList<>(musclesOfSessionsRepository.saveAll(List.of(
            MusclesOfSessions.builder().session(sessions.get(0)).muscle(Muscle.CHEST).build(),
            MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.BICEPS).build(),
            MusclesOfSessions.builder().session(sessions.get(2)).muscle(Muscle.LEG).build(),
            MusclesOfSessions.builder().session(sessions.get(3)).muscle(Muscle.BACK_LATS).build(),
            MusclesOfSessions.builder().session(sessions.get(0)).muscle(Muscle.TRICEPS).build(),
            MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.BACK_LATS).build(),
            MusclesOfSessions.builder().session(sessions.get(2)).muscle(Muscle.ABS).build(),
            MusclesOfSessions.builder().session(sessions.get(3)).muscle(Muscle.BICEPS).build(),
            MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.ABS).build(),
            MusclesOfSessions.builder().session(sessions.get(4)).muscle(Muscle.ABS).build()
        )));
        var sessionsHasMusclesRes = new ArrayList<>(sessions.stream()
            .map(e -> SessionHasMusclesResponse.builder()
                .sessionId(e.getSessionId()).name(e.getName()).description(e.getDescription()).levelEnum(e.getLevelEnum().toString())
                .muscleList(new ArrayList<>())
                .build()).toList());
        for (MusclesOfSessions exeHasMusDB : sessionHasMusclesFromDB) {
            for (SessionHasMusclesResponse sesHasMusRes : sessionsHasMusclesRes) {
                if (sesHasMusRes.getSessionId().equals(exeHasMusDB.getSession().getSessionId()))
                    sesHasMusRes.getMuscleList().add(exeHasMusDB.getMuscle().toString());
            }
        }


        ArrayList<SessionHasMusclesResponse> actual = new ArrayList<>(musclesOfSessionsRepository
            .findAllSessionsHasMuscles(PageRequest.of(0, 10))
            .stream().map(SessionHasMusclesResponse::buildFromNativeQuery).toList());

        assertNotNull(actual);
        assertEquals(sessionsHasMusclesRes.size(), actual.size());
        actual.sort(Comparator.comparing(SessionHasMusclesResponse::getSessionId));
        sessionsHasMusclesRes.sort(Comparator.comparing(SessionHasMusclesResponse::getSessionId));
        for (int index = 0; index < 4; index++) {
            var expectSes = sessionsHasMusclesRes.get(index);
            var actualExe = actual.get(index);
            long totalMuscle = sessionHasMusclesFromDB.stream().filter(exeHasMusDB ->
                exeHasMusDB.getSession().getSessionId().equals(expectSes.getSessionId())).count();

            assertEquals(expectSes.getName(), actualExe.getName());
            assertEquals(expectSes.getLevelEnum(), actualExe.getLevelEnum());
            assertEquals(expectSes.getDescription(), actualExe.getDescription());
            assertEquals(totalMuscle, actual.get(index).getMuscleList().size());
        }
    }

    @Test
    public void findAllSessionsHasMuscles_admin_validWithFiltering() {
        var request = SessionHasMusclesResponse.builder()
            .muscleList(List.of(Muscle.ABS.toString(), Muscle.BACK_LATS.toString()))
            .build();
        var sessions = new ArrayList<>(sessionRepository.saveAll(List.of(
            Session.builder().levelEnum(Level.BEGINNER).name("Test Session1").description("Testing").build(),
            Session.builder().levelEnum(Level.INTERMEDIATE).name("Test Session2").description("Testing").build(),
            Session.builder().levelEnum(Level.INTERMEDIATE).name("Test Session3").description("Testing").build(),
            Session.builder().levelEnum(Level.ADVANCE).name("Test Session4").description("Testing").build(),
            Session.builder().levelEnum(Level.BEGINNER).name("Test Session5").description("Testing").build()
        )));
        var sessionHasMusclesFromDB = new ArrayList<>(musclesOfSessionsRepository.saveAll(List.of(
            MusclesOfSessions.builder().session(sessions.get(0)).muscle(Muscle.CHEST).build(),
            MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.BICEPS).build(),
            MusclesOfSessions.builder().session(sessions.get(2)).muscle(Muscle.LEG).build(),
            MusclesOfSessions.builder().session(sessions.get(3)).muscle(Muscle.BACK_LATS).build(),
            MusclesOfSessions.builder().session(sessions.get(0)).muscle(Muscle.TRICEPS).build(),
            MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.BACK_LATS).build(),
            MusclesOfSessions.builder().session(sessions.get(2)).muscle(Muscle.ABS).build(),
            MusclesOfSessions.builder().session(sessions.get(3)).muscle(Muscle.BICEPS).build(),
            MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.ABS).build(),
            MusclesOfSessions.builder().session(sessions.get(4)).muscle(Muscle.ABS).build()
        )));
        var sessionsHasMusclesRes = new LinkedHashMap<Long, SessionHasMusclesResponse>();
        var removedIds = new LinkedHashSet<Long>();
        for (MusclesOfSessions sesHasMusDB : sessionHasMusclesFromDB) {
            if (sessionsHasMusclesRes.containsKey(sesHasMusDB.getSession().getSessionId())) {
                sessionsHasMusclesRes
                    .get(sesHasMusDB.getSession().getSessionId())
                    .getMuscleList().add(sesHasMusDB.getMuscle().toString());
            } else {
                sessionsHasMusclesRes
                    .put(sesHasMusDB.getSession().getSessionId(),
                        SessionHasMusclesResponse.builder()
                            .sessionId(sesHasMusDB.getSession().getSessionId())
                            .name(sesHasMusDB.getSession().getName())
                            .description(sesHasMusDB.getSession().getDescription())
                            .levelEnum(sesHasMusDB.getSession().getLevelEnum().toString())
                            .muscleList(new ArrayList<>(List.of(sesHasMusDB.getMuscle().toString()))).build()
                    );
            }
        }
        for (Long sessionIdKey : sessionsHasMusclesRes.keySet()) {
            if (sessionsHasMusclesRes.get(sessionIdKey).getMuscleList()
                .stream().noneMatch(muscle -> request.getMuscleList().contains(muscle)))
                removedIds.add(sessionIdKey);
        }
        removedIds.forEach(sessionsHasMusclesRes::remove);

        ArrayList<SessionHasMusclesResponse> actual = new ArrayList<>(musclesOfSessionsRepository
            .findAllSessionsHasMuscles(request, PageRequest.of(0, 10))
            .stream().map(SessionHasMusclesResponse::buildFromNativeQuery).toList());

        assertNotNull(actual);
        assertEquals(sessionsHasMusclesRes.size(), actual.size());
        actual.sort(Comparator.comparing(SessionHasMusclesResponse::getSessionId));
        for (Long sessionId : sessionsHasMusclesRes.keySet()) {
            Collections.sort(sessionsHasMusclesRes.get(sessionId).getMuscleList());
        }
        for (int index = 0; index < sessionsHasMusclesRes.size(); index++) {
            var eachActual = actual.get(index);
            var eachExpect = sessionsHasMusclesRes.get(eachActual.getSessionId());

            assertEquals(eachExpect.getName(), eachActual.getName());
            assertEquals(eachExpect.getLevelEnum(), eachActual.getLevelEnum());
            assertEquals(eachExpect.getDescription(), eachActual.getDescription());
            assertEquals(eachExpect.getMuscleList(), eachActual.getMuscleList());
        }
    }

    @Test
    public void findAllSessionsHasMuscles_admin_validWithFilteringWithoutMuscleList() {
        var request = SessionHasMusclesResponse.builder()
            .name("Session0")
            .muscleList(new ArrayList<>())
            .build();
        var sessions = new ArrayList<>(sessionRepository.saveAll(List.of(
            Session.builder().levelEnum(Level.BEGINNER).name("Test Session1").description("Testing").build(),
            Session.builder().levelEnum(Level.INTERMEDIATE).name("Test Session2").description("Testing").build(),
            Session.builder().levelEnum(Level.INTERMEDIATE).name("Test Session3").description("Testing").build(),
            Session.builder().levelEnum(Level.ADVANCE).name("Test Session4").description("Testing").build(),
            Session.builder().levelEnum(Level.BEGINNER).name("Test Session5").description("Testing").build()
        )));
        var sessionHasMusclesFromDB = new ArrayList<>(musclesOfSessionsRepository.saveAll(List.of(
            MusclesOfSessions.builder().session(sessions.get(0)).muscle(Muscle.CHEST).build(),
            MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.BICEPS).build(),
            MusclesOfSessions.builder().session(sessions.get(2)).muscle(Muscle.LEG).build(),
            MusclesOfSessions.builder().session(sessions.get(3)).muscle(Muscle.BACK_LATS).build(),
            MusclesOfSessions.builder().session(sessions.get(0)).muscle(Muscle.TRICEPS).build(),
            MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.BACK_LATS).build(),
            MusclesOfSessions.builder().session(sessions.get(2)).muscle(Muscle.ABS).build(),
            MusclesOfSessions.builder().session(sessions.get(3)).muscle(Muscle.BICEPS).build(),
            MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.ABS).build(),
            MusclesOfSessions.builder().session(sessions.get(4)).muscle(Muscle.ABS).build()
        )));
        var sessionsHasMusclesRes = new LinkedHashMap<Long, SessionHasMusclesResponse>();
        for (MusclesOfSessions sesHasMusDB : sessionHasMusclesFromDB) {
            if (sesHasMusDB.getSession().getName().contains(request.getName())) {
                if (sessionsHasMusclesRes.containsKey(sesHasMusDB.getSession().getSessionId())) {
                    sessionsHasMusclesRes
                        .get(sesHasMusDB.getSession().getSessionId());
                } else {
                    sessionsHasMusclesRes
                        .put(sesHasMusDB.getSession().getSessionId(), SessionHasMusclesResponse.builder()
                            .sessionId(sesHasMusDB.getSession().getSessionId())
                            .name(sesHasMusDB.getSession().getName())
                            .levelEnum(sesHasMusDB.getSession().getLevelEnum().toString())
                            .description(sesHasMusDB.getSession().getDescription())
                            .build());
                }
            }
        }

        ArrayList<SessionHasMusclesResponse> actual = new ArrayList<>(musclesOfSessionsRepository
            .findAllSessionsHasMuscles(request, PageRequest.of(0, 10))
            .stream().map(SessionHasMusclesResponse::buildFromNativeQuery).toList());

        assertNotNull(actual);
        assertEquals(sessionsHasMusclesRes.size(), actual.size());
        actual.sort(Comparator.comparing(SessionHasMusclesResponse::getSessionId));
        for (int index = 0; index < sessionsHasMusclesRes.size(); index++) {
            var eachActual = actual.get(index);
            var eachExpect = sessionsHasMusclesRes.get(eachActual.getSessionId());

            assertEquals(eachExpect.getName(), eachActual.getName());
            assertEquals(eachExpect.getLevelEnum(), eachActual.getLevelEnum());
            assertEquals(eachExpect.getDescription(), eachActual.getDescription());
        }
    }
}


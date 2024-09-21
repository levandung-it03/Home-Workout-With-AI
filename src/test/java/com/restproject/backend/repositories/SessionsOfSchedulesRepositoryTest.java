package com.restproject.backend.repositories;

import com.restproject.backend.config.RedisConfig;
import com.restproject.backend.dtos.response.SessionsOfScheduleResponse;
import com.restproject.backend.entities.MusclesOfSessions;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.entities.Session;
import com.restproject.backend.entities.SessionsOfSchedules;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.enums.PageEnum;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(RedisConfig.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionsOfSchedulesRepositoryTest {
    @Autowired
    SessionsOfSchedulesRepository sessionsOfSchedulesRepository;
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    MusclesOfSessionsRepository musclesOfSessionsRepository;


    /**
     * RULES:
     * 1. All Sessions, which are at Beginner Level, belong to Schedule of request (by req.scheduleId) for fast testing.
     */
    @Test
    public void findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId_admin_valid() {
        var schedule = scheduleRepository.save(Schedule.builder().level(Level.BEGINNER).name("Schedule 2: Chest,...")
            .description("This is Schedule").coins(2000L).build());
        var sessions = new ArrayList<>(sessionRepository.saveAll(List.of(
            Session.builder().name("Push Up").level(Level.BEGINNER).description("Description").build(),
            Session.builder().name("Bicep Curl").level(Level.INTERMEDIATE).description("Description").build(),
            Session.builder().name("Triceps Dip").level(Level.INTERMEDIATE).description("Description").build(),
            Session.builder().name("Squat").level(Level.ADVANCE).description("Description").build(),
            Session.builder().name("Plank").level(Level.BEGINNER).description("Description").build(),
            Session.builder().name("Jump Squat").level(Level.ADVANCE).description("Description").build(),
            Session.builder().name("One Leg Squat").level(Level.ADVANCE).description("Description").build()
        )));
        //--All of Beginner Level sessions belong to schedule (with just testing data)
        var sessionsScheduleRelationship = sessions.stream().filter(e -> e.getLevel().equals(Level.BEGINNER)).toList();
        sessionsOfSchedulesRepository.saveAll(sessionsScheduleRelationship.stream().map(exe ->
            SessionsOfSchedules.builder().session(exe).schedule(schedule).build()).toList());
        var sessionHasMusclesFromDB = new ArrayList<>(musclesOfSessionsRepository.saveAll(
            List.of(
                MusclesOfSessions.builder().session(sessions.get(0)).muscle(Muscle.CHEST).build(),
                MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.BICEPS).build(),
                MusclesOfSessions.builder().session(sessions.get(2)).muscle(Muscle.LEG).build(),
                MusclesOfSessions.builder().session(sessions.get(3)).muscle(Muscle.BACK_LATS).build(),
                MusclesOfSessions.builder().session(sessions.get(0)).muscle(Muscle.TRICEPS).build(),
                MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.BACK_LATS).build(),
                MusclesOfSessions.builder().session(sessions.get(2)).muscle(Muscle.ABS).build(),
                MusclesOfSessions.builder().session(sessions.get(3)).muscle(Muscle.BICEPS).build(),
                MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.ABS).build(),
                MusclesOfSessions.builder().session(sessions.get(4)).muscle(Muscle.ABS).build(),
                MusclesOfSessions.builder().session(sessions.get(5)).muscle(Muscle.ABS).build(),
                MusclesOfSessions.builder().session(sessions.get(6)).muscle(Muscle.ABS).build()
            )));
        var sessionsHasMusclesRes = new ArrayList<>(sessions.stream().map(session ->
            SessionsOfScheduleResponse.builder()
                .sessionId(session.getSessionId())
                .name(session.getName())
                .description(session.getDescription())
                .level(session.getLevel()) //--All Beginner session belongs to schedule by default.
                .withCurrentSchedule(session.getLevel().equals(Level.BEGINNER))
                .muscleList(new ArrayList<>())
                //--All of Beginner Level sessions belong to schedule (with just testing data)
                .withCurrentSchedule(session.getLevel().equals(Level.BEGINNER)).build()).toList());
        for (MusclesOfSessions exeHasMusDB : sessionHasMusclesFromDB) {
            for (SessionsOfScheduleResponse exeHasMusRes : sessionsHasMusclesRes) {
                if (exeHasMusRes.getSessionId().equals(exeHasMusDB.getSession().getSessionId()))
                    exeHasMusRes.getMuscleList().add(exeHasMusDB.getMuscle().toString());
            }
        }

        List<Object[]> repoRes = sessionsOfSchedulesRepository
            .findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(
                schedule.getScheduleId(),
                PageRequest.of(0, PageEnum.SIZE.getSize())
            ).stream().toList();
        ArrayList<SessionsOfScheduleResponse> actual = new ArrayList<>(repoRes
            .stream().map(SessionsOfScheduleResponse::buildFromNativeQuery)
            .toList());

        assertNotNull(actual);
        actual.sort(Comparator.comparing(SessionsOfScheduleResponse::getSessionId));
        sessionsHasMusclesRes.sort(Comparator.comparing(SessionsOfScheduleResponse::getSessionId));
        for (int index = 0; index < actual.size(); index++) {
            var expectExe = sessionsHasMusclesRes.get(index);
            var actualExe = actual.get(index);
            long totalMuscle = sessionHasMusclesFromDB.stream().filter(exeHasMusDB ->
                exeHasMusDB.getSession().getSessionId().equals(expectExe.getSessionId())).count();

            assertEquals(expectExe.getName(), actualExe.getName());
            assertEquals(expectExe.getLevel(), actualExe.getLevel());
            assertEquals(expectExe.getDescription(), actualExe.getDescription());
            assertEquals(totalMuscle, actual.get(index).getMuscleList().size());
        }
        assertEquals(
            actual.stream().filter(SessionsOfScheduleResponse::isWithCurrentSchedule).count(),
            sessionsHasMusclesRes.stream().filter(SessionsOfScheduleResponse::isWithCurrentSchedule).count());
    }

    /**
     * RULES:
     * 1. All Sessions, which are at Beginner Level, belong to Schedule of request (by req.scheduleId) for fast testing.
     */
    @Test
    public void findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId_admin_validWithFiltering() {
        var req = SessionsOfScheduleResponse.builder().name("p").muscleList(new ArrayList<>()).build();
        var scheduleRequest = scheduleRepository.save(Schedule.builder().level(Level.BEGINNER).name("Schedule 2: Chest,...")
            .description("This is Schedule").coins(2000L).build());
        var sessions = new ArrayList<>(sessionRepository.saveAll(List.of(
            Session.builder().name("Push Up").level(Level.BEGINNER).description("Description").build(),
            Session.builder().name("Bicep Curl").level(Level.INTERMEDIATE).description("Description").build(),
            Session.builder().name("Triceps Dip").level(Level.INTERMEDIATE).description("Description").build(),
            Session.builder().name("Squat").level(Level.ADVANCE).description("Description").build(),
            Session.builder().name("Plank").level(Level.BEGINNER).description("Description").build(),
            Session.builder().name("Jump Squat").level(Level.ADVANCE).description("Description").build(),
            Session.builder().name("One Leg Squat").level(Level.ADVANCE).description("Description").build()
        )));
        //--All of Beginner Level sessions belong to schedule (with just testing data)
        var sessionsScheduleRelationship = sessions.stream().filter(e -> e.getLevel().equals(Level.BEGINNER)).toList();
        sessionsOfSchedulesRepository.saveAll(sessionsScheduleRelationship.stream().map(exe ->
            SessionsOfSchedules.builder().session(exe).schedule(scheduleRequest).build()).toList());
        var sessionHasMusclesFromDB = new ArrayList<>(musclesOfSessionsRepository.saveAll(
            List.of(
                MusclesOfSessions.builder().session(sessions.get(0)).muscle(Muscle.CHEST).build(),
                MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.BICEPS).build(),
                MusclesOfSessions.builder().session(sessions.get(2)).muscle(Muscle.LEG).build(),
                MusclesOfSessions.builder().session(sessions.get(3)).muscle(Muscle.BACK_LATS).build(),
                MusclesOfSessions.builder().session(sessions.get(0)).muscle(Muscle.TRICEPS).build(),
                MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.BACK_LATS).build(),
                MusclesOfSessions.builder().session(sessions.get(2)).muscle(Muscle.ABS).build(),
                MusclesOfSessions.builder().session(sessions.get(3)).muscle(Muscle.BICEPS).build(),
                MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.ABS).build(),
                MusclesOfSessions.builder().session(sessions.get(4)).muscle(Muscle.ABS).build(),
                MusclesOfSessions.builder().session(sessions.get(5)).muscle(Muscle.ABS).build(),
                MusclesOfSessions.builder().session(sessions.get(6)).muscle(Muscle.ABS).build()
            )));
        var sessionsHasMusclesRes = new ArrayList<>(sessions.stream()
            .filter(e -> e.getName().toUpperCase().contains("P"))
            .map(session ->
                SessionsOfScheduleResponse.builder()
                    .sessionId(session.getSessionId())
                    .name(session.getName())
                    .description(session.getDescription())
                    .level(session.getLevel()) //--All Beginner session belongs to schedule by default.
                    .withCurrentSchedule(session.getLevel().equals(Level.BEGINNER))
                    .muscleList(new ArrayList<>())
                    //--All of Beginner Level sessions belong to schedule (with just testing data)
                    .withCurrentSchedule(session.getLevel().equals(Level.BEGINNER)).build()).toList());
        for (MusclesOfSessions exeHasMusDB : sessionHasMusclesFromDB) {
            for (SessionsOfScheduleResponse exeHasMusRes : sessionsHasMusclesRes) {
                if (exeHasMusRes.getSessionId().equals(exeHasMusDB.getSession().getSessionId()))
                    exeHasMusRes.getMuscleList().add(exeHasMusDB.getMuscle().toString());
            }
        }

        List<Object[]> repoRes = sessionsOfSchedulesRepository
            .findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(
                scheduleRequest.getScheduleId(), req,
                PageRequest.of(0, PageEnum.SIZE.getSize())
            ).stream().toList();
        ArrayList<SessionsOfScheduleResponse> actual = new ArrayList<>(repoRes
            .stream().map(SessionsOfScheduleResponse::buildFromNativeQuery)
            .toList());

        assertNotNull(actual);
        actual.sort(Comparator.comparing(SessionsOfScheduleResponse::getSessionId));
        sessionsHasMusclesRes.sort(Comparator.comparing(SessionsOfScheduleResponse::getSessionId));
        for (int index = 0; index < actual.size(); index++) {
            var actualExe = actual.get(index);
            var expectExe = sessionsHasMusclesRes.get(index);

            assertEquals(expectExe.getName(), actualExe.getName());
            assertEquals(expectExe.getLevel(), actualExe.getLevel());
            assertEquals(expectExe.getDescription(), actualExe.getDescription());
            assertEquals(expectExe.getMuscleList().size(), actualExe.getMuscleList().size());
            assertTrue(expectExe.getMuscleList().containsAll(actualExe.getMuscleList()));
        }
        assertEquals(
            actual.stream().filter(SessionsOfScheduleResponse::isWithCurrentSchedule).count(),
            sessionsHasMusclesRes.stream().filter(SessionsOfScheduleResponse::isWithCurrentSchedule).count());
    }

    /**
     * RULES:
     * 1. All Sessions, which are at Beginner Level, belong to Schedule of request (by req.scheduleId) for fast testing.
     */
    @Test
    public void findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId_admin_validWithFilteringAndMuscles() {
        var req = SessionsOfScheduleResponse.builder()
            .muscleList(new ArrayList<>(List.of(Muscle.ABS.toString(), Muscle.BICEPS.toString()))).build();
        var scheduleRequest = scheduleRepository.save(Schedule.builder().level(Level.BEGINNER).name("Schedule 2: Chest,...")
            .description("This is Schedule").coins(2000L).build());
        var sessions = new ArrayList<>(sessionRepository.saveAll(List.of(
            Session.builder().name("Push Up").level(Level.BEGINNER).description("Description").build(),
            Session.builder().name("Bicep Curl").level(Level.INTERMEDIATE).description("Description").build(),
            Session.builder().name("Triceps Dip").level(Level.INTERMEDIATE).description("Description").build(),
            Session.builder().name("Squat").level(Level.ADVANCE).description("Description").build(),
            Session.builder().name("Plank").level(Level.BEGINNER).description("Description").build(),
            Session.builder().name("Jump Squat").level(Level.ADVANCE).description("Description").build(),
            Session.builder().name("One Leg Squat").level(Level.ADVANCE).description("Description").build()
        )));
        //--All of Beginner Level sessions belong to schedule (with just testing data)
        var sessionsScheduleRelationship = sessions.stream().filter(e -> e.getLevel().equals(Level.BEGINNER)).toList();
        sessionsOfSchedulesRepository.saveAll(sessionsScheduleRelationship.stream().map(exe ->
            SessionsOfSchedules.builder().session(exe).schedule(scheduleRequest).build()).toList());
        var sessionHasMusclesFromDB = new ArrayList<>(musclesOfSessionsRepository.saveAll(
            List.of(
                MusclesOfSessions.builder().session(sessions.get(0)).muscle(Muscle.CHEST).build(),
                MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.BICEPS).build(),
                MusclesOfSessions.builder().session(sessions.get(2)).muscle(Muscle.LEG).build(),
                MusclesOfSessions.builder().session(sessions.get(3)).muscle(Muscle.BACK_LATS).build(),
                MusclesOfSessions.builder().session(sessions.get(0)).muscle(Muscle.TRICEPS).build(),
                MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.BACK_LATS).build(),
                MusclesOfSessions.builder().session(sessions.get(2)).muscle(Muscle.ABS).build(),
                MusclesOfSessions.builder().session(sessions.get(3)).muscle(Muscle.BICEPS).build(),
                MusclesOfSessions.builder().session(sessions.get(1)).muscle(Muscle.ABS).build(),
                MusclesOfSessions.builder().session(sessions.get(4)).muscle(Muscle.ABS).build(),
                MusclesOfSessions.builder().session(sessions.get(5)).muscle(Muscle.ABS).build(),
                MusclesOfSessions.builder().session(sessions.get(6)).muscle(Muscle.ABS).build()
            )));
        var sessionsHasMusclesRes = new LinkedHashMap<Long, SessionsOfScheduleResponse>();
        var removedIds = new ArrayList<Long>();
        for (MusclesOfSessions exeHasMusDB : sessionHasMusclesFromDB) {
            if (!sessionsHasMusclesRes.containsKey(exeHasMusDB.getSession().getSessionId())) {
                sessionsHasMusclesRes.put(
                    exeHasMusDB.getSession().getSessionId(),
                    SessionsOfScheduleResponse.builder()
                        .sessionId(exeHasMusDB.getSession().getSessionId())
                        .name(exeHasMusDB.getSession().getName())
                        .description(exeHasMusDB.getSession().getDescription())
                        .level(exeHasMusDB.getSession().getLevel()) //--All Beginner session belongs to schedule by default.
                        .withCurrentSchedule(exeHasMusDB.getSession().getLevel().equals(Level.BEGINNER))
                        .muscleList(new ArrayList<>(List.of(exeHasMusDB.getMuscle().toString())))
                        //--All of Beginner Level sessions belong to schedule (with just testing data)
                        .withCurrentSchedule(exeHasMusDB.getSession().getLevel().equals(Level.BEGINNER)).build()
                );
            } else {
                sessionsHasMusclesRes.get(exeHasMusDB.getSession().getSessionId())
                    .getMuscleList().add(exeHasMusDB.getMuscle().toString());
            }
        }
        for (Long sessionId : sessionsHasMusclesRes.keySet()) {
            if (sessionsHasMusclesRes.get(sessionId).getMuscleList()
                .stream().noneMatch(m -> req.getMuscleList().contains(m)))
                removedIds.add(sessionsHasMusclesRes.get(sessionId).getSessionId());
        }
        removedIds.forEach(sessionsHasMusclesRes::remove);

        List<Object[]> repoRes = sessionsOfSchedulesRepository
            .findAllSessionsHasMusclesPrioritizeRelationshipByScheduleId(
                scheduleRequest.getScheduleId(), req,
                PageRequest.of(0, PageEnum.SIZE.getSize())
            ).stream().toList();
        ArrayList<SessionsOfScheduleResponse> actual = new ArrayList<>(repoRes
            .stream().map(SessionsOfScheduleResponse::buildFromNativeQuery)
            .toList());

        assertNotNull(actual);
        actual.sort(Comparator.comparing(SessionsOfScheduleResponse::getSessionId));
        for (int index = 0; index < actual.size(); index++) {
            var actualExe = actual.get(index);
            var expectExe = sessionsHasMusclesRes.get(actualExe.getSessionId());

            assertEquals(expectExe.getName(), actualExe.getName());
            assertEquals(expectExe.getLevel(), actualExe.getLevel());
            assertEquals(expectExe.getDescription(), actualExe.getDescription());
            assertEquals(expectExe.getMuscleList().size(), actualExe.getMuscleList().size());
            assertTrue(expectExe.getMuscleList().containsAll(actualExe.getMuscleList()));
        }
        assertEquals(
            actual.stream().filter(SessionsOfScheduleResponse::isWithCurrentSchedule).count(),
            sessionsHasMusclesRes.values().stream().filter(SessionsOfScheduleResponse::isWithCurrentSchedule).count());
    }
}

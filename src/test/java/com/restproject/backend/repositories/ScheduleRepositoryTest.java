package com.restproject.backend.repositories;

import com.restproject.backend.config.RedisConfig;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.PageEnum;
import io.lettuce.core.support.caching.RedisCache;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@DataJpaTest
@Import(RedisConfig.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduleRepositoryTest {
    @Autowired
    ScheduleRepository scheduleRepository;

    @Test
    public void findAllBySchedule_admin_validWithFiltering() {
        var req = Schedule.builder().name("Schedule0").build();
        var schedules = new ArrayList<>(scheduleRepository.saveAll(List.of(
            Schedule.builder().levelEnum(Level.BEGINNER).name("Test Schedule1").description("Testing").coins(2000L).build(),
            Schedule.builder().levelEnum(Level.INTERMEDIATE).name("Test Schedule02").description("Testing").coins(2000L).build(),
            Schedule.builder().levelEnum(Level.INTERMEDIATE).name("Test Schedule03").description("Testing").coins(2000L).build(),
            Schedule.builder().levelEnum(Level.ADVANCE).name("Test Schedule4").description("Testing").coins(2000L).build(),
            Schedule.builder().levelEnum(Level.BEGINNER).name("Test Schedule5").description("Testing").coins(2000L).build()
        )));
        var pageableCf = PageRequest.of(0, PageEnum.SIZE.getSize());

        Page<Schedule> repoRes = scheduleRepository.findAllBySchedule(req, pageableCf);

        assertNotNull(repoRes);
        assertEquals(
            schedules.stream().filter(s -> s.getName().contains(req.getName())).collect(Collectors.toSet()),
            new HashSet<>(repoRes.toList())
        );
    }
}

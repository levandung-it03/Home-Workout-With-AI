package com.restproject.backend.mappers;

import com.restproject.backend.dtos.request.NewScheduleRequest;
import com.restproject.backend.dtos.request.UpdateScheduleRequest;
import com.restproject.backend.entities.Schedule;
import com.restproject.backend.enums.Level;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ScheduleMappers {

    @Mapping(target = "level", source = "level", qualifiedByName = "mapLevelField")
    Schedule insertionToPlain(NewScheduleRequest newScheduleRequest);

    @Mapping(target = "level", source = "level", qualifiedByName = "mapLevelField")
    void updateTarget(@MappingTarget Schedule updatedSchedule, UpdateScheduleRequest scheduleRequest);

    @Named("mapLevelField")
    default Level mapLevelField(Integer level) {
        return Level.getByLevel(level);
    }
}

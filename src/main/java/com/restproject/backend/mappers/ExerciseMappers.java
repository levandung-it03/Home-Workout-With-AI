package com.restproject.backend.mappers;

import com.restproject.backend.dtos.request.NewExerciseRequest;
import com.restproject.backend.dtos.request.UpdateExerciseRequest;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.enums.Level;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
public interface ExerciseMappers {
    @Mapping(target = "level", source = "level", qualifiedByName = "mapLevelField")
    Exercise insertionToPlain(NewExerciseRequest exerciseRequest);

    @Mapping(target = "level", source = "level", qualifiedByName = "mapLevelField")
    void updateTarget(@MappingTarget Exercise queriedObject, UpdateExerciseRequest updateInfoObject);

    @Named("mapLevelField")
    default Level mapLevelField(Integer level) {
        return Level.getByLevel(level);
    }
}
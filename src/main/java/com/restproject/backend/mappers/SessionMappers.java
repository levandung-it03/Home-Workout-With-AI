package com.restproject.backend.mappers;

import com.restproject.backend.dtos.request.NewSessionRequest;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.Qualifier;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface SessionMappers {

    @Mapping(target = "level", source = "level", qualifiedByName = "mapLevelField")
    @Mapping(target = "muscleList", source = "muscleIds", qualifiedByName = "mapMuscleIdsToString")
    Session insertionToPlain(NewSessionRequest newSessionRequest);

    @Named("mapLevelField")
    default Level mapLevelField(Integer level) {
        return Level.getByLevel(level);
    }

    @Named("mapMuscleIdsToString")
    default String mapMuscleIdsToString(Collection<Integer> muscleIds) {
        return Muscle.idsToString(muscleIds);
    }
}

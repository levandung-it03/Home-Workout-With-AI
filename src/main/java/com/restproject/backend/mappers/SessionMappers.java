package com.restproject.backend.mappers;

import com.restproject.backend.dtos.request.NewSessionRequest;
import com.restproject.backend.dtos.request.UpdateSessionRequest;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import org.mapstruct.*;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface SessionMappers {

    @Mapping(target = "levelEnum", source = "level", qualifiedByName = "mapLevelField")
    Session insertionToPlain(NewSessionRequest newSessionRequest);

    @Mapping(target = "levelEnum", source = "level", qualifiedByName = "mapLevelField")
    void updateTarget(@MappingTarget Session udpatedSession, UpdateSessionRequest updateInfoObject);

    @Named("mapLevelField")
    default Level mapLevelField(Integer level) {
        return Level.getByLevel(level);
    }
}

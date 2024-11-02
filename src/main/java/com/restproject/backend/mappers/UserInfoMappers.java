package com.restproject.backend.mappers;

import com.restproject.backend.dtos.request.NewUserRequest;
import com.restproject.backend.dtos.request.UpdateUserInfoRequest;
import com.restproject.backend.entities.UserInfo;
import com.restproject.backend.enums.Gender;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserInfoMappers {

    @Mapping(target = "gender", source = "genderId", qualifiedByName = "mapGenderField")
    @Mapping(target = "coins", ignore = true)
    UserInfo insertionToPlain(NewUserRequest request);

    @Mapping(target = "gender", source = "genderId", qualifiedByName = "mapGenderField")
    @Mapping(target = "coins", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateTarget(@MappingTarget UserInfo result, UpdateUserInfoRequest request);

    @Named("mapGenderField")
    default Gender mapGenderField(Integer genderId) {
        return Gender.getByGenderId(genderId);
    }
}

package com.restproject.backend.mappers;

import com.restproject.backend.dtos.request.FilteringPageRequest;
import com.restproject.backend.entities.PageObject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FilteringPageMappers {

    @Mapping(target = "pageSize", expression = "java(com.restproject.backend.enums.PageEnum.SIZE.getSize())")
    PageObject pageRequestToPageable(FilteringPageRequest filteringPageRequest);
}

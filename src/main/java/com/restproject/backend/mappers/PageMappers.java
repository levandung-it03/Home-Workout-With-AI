package com.restproject.backend.mappers;

import com.restproject.backend.dtos.request.PaginatedObjectRequest;
import com.restproject.backend.entities.PageObject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PageMappers {
    @Mapping(target = "pageSize", expression = "java(com.restproject.backend.enums.PageEnum.SIZE.getSize())")
    PageObject pageRequestToPageable(PaginatedObjectRequest paginatedObjectRequest);
}

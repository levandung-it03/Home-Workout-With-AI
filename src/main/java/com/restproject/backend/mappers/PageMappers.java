package com.restproject.backend.mappers;

import com.restproject.backend.annotations.dev.Overload;
import com.restproject.backend.dtos.request.PaginatedRelationshipRequest;
import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.entities.PageObject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PageMappers {

    @Mapping(target = "pageSize", expression = "java(com.restproject.backend.enums.PageEnum.SIZE.getSize())")
    PageObject relationshipPageRequestToPageable(PaginatedRelationshipRequest paginatedRelationshipRequest);

    @Overload
    @Mapping(target = "pageSize", expression = "java(com.restproject.backend.enums.PageEnum.SIZE.getSize())")
    PageObject tablePageRequestToPageable(PaginatedTableRequest paginationTableRequest);
}

package com.restproject.backend.mappers;

import com.restproject.backend.dtos.request.PaginatedRelationshipRequest;
import com.restproject.backend.dtos.request.PaginatedTableRequest;
import com.restproject.backend.entities.PageObject;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PageMappers {

    PageObject relationshipPageRequestToPageable(PaginatedRelationshipRequest paginatedRelationshipRequest);

    PageObject tablePageRequestToPageable(PaginatedTableRequest paginationTableRequest);
}

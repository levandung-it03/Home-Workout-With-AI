package com.restproject.backend.repositories;

import com.restproject.backend.entities.Auth.InvalidToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidTokenCrud extends CrudRepository<InvalidToken, String> {
}

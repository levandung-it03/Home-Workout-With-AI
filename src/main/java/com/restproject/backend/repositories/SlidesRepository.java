package com.restproject.backend.repositories;

import com.restproject.backend.entities.Slides;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlidesRepository extends JpaRepository<Slides, Long> {
}

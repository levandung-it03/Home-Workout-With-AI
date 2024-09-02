package com.restproject.backend.repositories;

import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.Session;
import com.restproject.backend.enums.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}

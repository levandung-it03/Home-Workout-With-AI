package com.restproject.backend.services;

import com.restproject.backend.entities.Muscle;
import com.restproject.backend.repositories.MuscleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MuscleService {
    MuscleRepository muscleRepository;

    public List<Muscle> getAllMuscles() {
        return muscleRepository.findAll();
    }
}

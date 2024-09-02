package com.restproject.backend.services.Admin;

import com.restproject.backend.dtos.request.ExercisesByLevelAndMusclesRequest;
import com.restproject.backend.dtos.request.NewExerciseRequest;
import com.restproject.backend.entities.Exercise;
import com.restproject.backend.entities.MusclesOfExercises;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.ExerciseRepository;
import com.restproject.backend.repositories.MusclesOfExercisesRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseService {
    ExerciseRepository exerciseRepository;
    MusclesOfExercisesRepository musclesOfExercisesRepository;

    public List<Exercise> getExercisesByLevelAndMuscles(ExercisesByLevelAndMusclesRequest request) {
        var exAndMusclesRelationshipList = musclesOfExercisesRepository.findAllByLevelAndMuscles(
            Level.getByLevel(request.getLevel()),
            request.getMuscleIds().stream().map(Muscle::getById).toList()
        );
        return exAndMusclesRelationshipList.stream().map(MusclesOfExercises::getExercise).toList();
    }

    @Transactional(rollbackOn = {Exception.class})
    public void createExercise(NewExerciseRequest request) throws ApplicationException {
        var savedExercise = exerciseRepository.save(Exercise.builder()
            .basicReps(request.getBasicReps())
            .level(Level.getByLevel(request.getLevel()))
            .name(request.getName())
            .build());
        musclesOfExercisesRepository.saveAll(request.getMuscleIds().stream().map(id ->
            MusclesOfExercises.builder().exercise(savedExercise).muscle(Muscle.getById(id)).build()).toList());
    }
}

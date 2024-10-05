package com.restproject.backend.services;

import com.restproject.backend.enums.Gender;
import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnumsService {

    public List<Map<String, String>> getAllLevels() {
        return Level.getAllLevels().stream().map(level -> Map.of(
            "raw", level.toString(),
            "level", level.getLevel().toString(),
            "name", level.getName()
        )).toList();
    }

    public List<Map<String, String>> getAllMuscles() {
        return Muscle.getAllMuscles().stream().map(muscle -> Map.of(
            "raw", muscle.toString(),
            "id", muscle.getId().toString(),
            "name", muscle.getName()
        )).toList();
    }

    public List<Map<String, String>> getAllGenders() {
        return Gender.getAllGenders().stream().map(gender -> Map.of(
            "raw", gender.toString(),
            "genderId", gender.getGenderId().toString()
        )).toList();
    }
}

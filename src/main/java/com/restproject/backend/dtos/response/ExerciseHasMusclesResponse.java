package com.restproject.backend.dtos.response;

import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.MusclesOfExercisesRepository;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseHasMusclesResponse {
    Long exerciseId;
    String name;
    Level levelEnum;   //--Keep String type to make filter work correctly
    Integer basicReps;
    List<Muscle> muscleList;    //--Keep MuscleEnum as String to make filter works correctly.
    String imageUrl;

    public static ExerciseHasMusclesResponse buildFromNativeQuery(Object[] params) {
        return ExerciseHasMusclesResponse.builder()
            .exerciseId(Long.parseLong(params[0].toString()))
            .name(params[1].toString())
            .basicReps(Integer.parseInt(params[2].toString()))
            .levelEnum(Level.valueOf(params[3].toString()))
            .muscleList(Arrays.stream(params[4].toString()
                .replaceAll("[\\[\\]]", "")
                .split(String.valueOf(MusclesOfExercisesRepository.GROUP_CONCAT_SEPARATOR))
            ).map(Muscle::valueOf).toList())
            .imageUrl(Objects.isNull(params[5]) ? null : params[5].toString())
            .build();
    }

    public static ExerciseHasMusclesResponse buildFromHashMap(HashMap<String, Object> map)
        throws ApplicationException, IllegalArgumentException, NullPointerException, NoSuchFieldException {
        for (String key : map.keySet()) {
            if (key.equals("muscleIds") || key.equals("level")) continue;
            if (Arrays.stream(ExerciseHasMusclesResponse.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key))) throw new NoSuchFieldException();
        }

        var exerciseInfo = new ExerciseHasMusclesResponse();
        exerciseInfo.setName(!map.containsKey("name") ? null : map.get("name").toString());
        exerciseInfo.setLevelEnum(!map.containsKey("level") ? null : Level.getByLevel(map.get("level").toString()));
        exerciseInfo.setBasicReps(!map.containsKey("basicReps") ? null
            : Integer.parseInt(map.get("basicReps").toString()));
        exerciseInfo.setMuscleList(!map.containsKey("muscleIds") ? null   //--May throw IllegalArgExc
            : Muscle.parseAllMuscleIdsArrToRaw(map.get("muscleIds").toString()));
        return exerciseInfo;
    }
}

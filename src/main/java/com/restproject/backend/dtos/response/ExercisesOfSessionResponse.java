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
public class ExercisesOfSessionResponse {
    Long exerciseId;
    String name;
    Level levelEnum;
    Integer basicReps;
    List<String> muscleList;    //--Keep MuscleEnum as String to make filter works correctly.
    boolean withCurrentSession;

    public static ExercisesOfSessionResponse buildFromNativeQuery(Object[] params) {
        return ExercisesOfSessionResponse.builder()
            .exerciseId(Long.parseLong(params[0].toString()))
            .name(params[1].toString())
            .basicReps(Integer.parseInt(params[2].toString()))
            .levelEnum(Level.valueOf(params[3].toString()))
            .withCurrentSession(!Objects.isNull(params[4]) && params[4].toString().equals("1"))
            .muscleList(Arrays.stream(params[5].toString()
                .replaceAll("[\\[\\]]", "")
                .split(String.valueOf(MusclesOfExercisesRepository.GROUP_CONCAT_SEPARATOR))
            ).toList())
            .build();
    }

    public static ExercisesOfSessionResponse buildFromHashMap(HashMap<String, Object> map)
        throws ApplicationException, IllegalArgumentException, NullPointerException, NoSuchFieldException {
        for (String key : map.keySet()) {
            if (key.equals("muscleIds"))    continue;
            if (Arrays.stream(ExercisesOfSessionResponse.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key))) throw new NoSuchFieldException(); //--Ignored value.
        }

        var exerciseInfo = new ExercisesOfSessionResponse();
        exerciseInfo.setMuscleList(!map.containsKey("muscleIds") ? new ArrayList<>()   //--May throw IllegalArgExc
            : Arrays.stream(map.get("muscleIds").toString()
            .replaceAll("[\\[\\]]", "").split(",")
        ).map(id -> Muscle.getById(id).toString()).toList());
        exerciseInfo.setName(!map.containsKey("name") ? null : map.get("name").toString());
        exerciseInfo.setBasicReps(!map.containsKey("basicReps") ? null
            : Integer.parseInt(map.get("basicReps").toString()));
        exerciseInfo.setLevelEnum(!map.containsKey("level") ? null
            : Level.getByLevel(Integer.parseInt(map.get("level").toString()))); //--May throw AppExc
        return exerciseInfo;
    }
}

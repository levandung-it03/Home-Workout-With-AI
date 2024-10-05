package com.restproject.backend.dtos.response;

import com.restproject.backend.enums.Level;
import com.restproject.backend.enums.Muscle;
import com.restproject.backend.exceptions.ApplicationException;
import com.restproject.backend.repositories.MusclesOfExercisesRepository;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseHasMusclesResponse {
    Long exerciseId;
    String name;
    String level;   //--Keep String type to make filter work correctly
    Integer basicReps;
    List<String> muscleList;    //--Keep MuscleEnum as String to make filter works correctly.
    String imageUrl;

    public static ExerciseHasMusclesResponse buildFromNativeQuery(Object[] params) {
        return ExerciseHasMusclesResponse.builder()
            .exerciseId(Long.parseLong(params[0].toString()))
            .name(params[1].toString())
            .basicReps(Integer.parseInt(params[2].toString()))
            .level(params[3].toString())
            .muscleList(Arrays.stream(params[4].toString()
                .replaceAll("[\\[\\]]", "")
                .split(String.valueOf(MusclesOfExercisesRepository.GROUP_CONCAT_SEPARATOR))
            ).toList())
            .imageUrl(params[5].toString())
            .build();
    }

    public static ExerciseHasMusclesResponse buildFromHashMap(HashMap<String, Object> map)
        throws ApplicationException, IllegalArgumentException, NullPointerException, NoSuchFieldException {
        for (String key : map.keySet()) {
            if (key.equals("muscleIds"))    continue;
            if (Arrays.stream(ExerciseHasMusclesResponse.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key))) throw new NoSuchFieldException(); //--Ignored value.
        }

        var exerciseInfo = new ExerciseHasMusclesResponse();
        exerciseInfo.setName(!map.containsKey("name") ? null : map.get("name").toString());
        exerciseInfo.setLevel(!map.containsKey("level") ? null
            : Level.getByLevel(Integer.parseInt(map.get("level").toString())).toString());
        exerciseInfo.setBasicReps(!map.containsKey("basicReps") ? null
            : Integer.parseInt(map.get("basicReps").toString()));
        exerciseInfo.setMuscleList(!map.containsKey("muscleIds") ? new ArrayList<>()   //--May throw IllegalArgExc
            : Arrays.stream(map.get("muscleIds").toString()
            .replaceAll("[\\[\\]]", "").split(",")
        ).map(id -> Muscle.getById(id).toString()).toList());
        return exerciseInfo;
    }

    public ExerciseHasMusclesResponse injectMuscleList(List<String> muscleList) {
        this.muscleList = muscleList;
        return this;
    }
}

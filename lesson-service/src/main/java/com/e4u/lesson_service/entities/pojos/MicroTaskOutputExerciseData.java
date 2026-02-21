package com.e4u.lesson_service.entities.pojos;

import com.e4u.lesson_service.common.constants.Constant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MicroTaskOutputExerciseData extends ExerciseData {
    // TODO: 20/01 Add properties for micro task output exercise

    @Override
    public String getType() {
        return Constant.MICRO_TASK_OUTPUT_IDENTIFIER;
    }
}

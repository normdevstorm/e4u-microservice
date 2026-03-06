package com.e4u.learning_service.entities.pojos.answers;

import com.e4u.learning_service.common.constants.Constant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MechanicDrillAnswer extends ExerciseAnswer {

    private String selectedWord;
    private Boolean isCorrect;

    @Override
    public String getType() {
        return Constant.MECHANIC_DRILL_IDENTIFIER;
    }
}

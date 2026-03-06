package com.e4u.learning_service.entities.pojos.answers;

import com.e4u.learning_service.common.constants.Constant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MultipleChoiceAnswer extends ExerciseAnswer {

    private Integer selectedIndex;
    private String selectedAnswer;
    private Boolean isCorrect;

    @Override
    public String getType() {
        return Constant.MULTIPLE_CHOICE_IDENTIFIER;
    }
}

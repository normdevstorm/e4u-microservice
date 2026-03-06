package com.e4u.learning_service.entities.pojos.answers;

import com.e4u.learning_service.common.constants.Constant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TargetWordIntegrationAnswer extends ExerciseAnswer {

    /**
     * The full sentence written by the user integrating the target word.
     */
    private String sentence;

    @Override
    public String getType() {
        return Constant.TARGET_WORD_INTEGRATION_IDENTIFIER;
    }
}

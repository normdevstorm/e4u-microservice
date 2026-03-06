package com.e4u.learning_service.entities.pojos.answers;

import com.e4u.learning_service.common.constants.Constant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssistedCompositionAnswer extends ExerciseAnswer {

    private String composition;
    private Integer wordCount;
    private Boolean containsExpectedWord;
    private Boolean isValid;

    @Override
    public String getType() {
        return Constant.ASSISTED_COMPOSITION_IDENTIFIER;
    }
}

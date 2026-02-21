package com.e4u.lesson_service.entities.pojos;

import com.e4u.lesson_service.common.constants.Constant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContextualExerciseData extends ExerciseData {
    private String prompt;
    private String hint;
    private String highlightedText;
    private String audioUrl;

    @Override
    public String getType() {
        return Constant.CONTEXTUAL_DISCOVERY_IDENTIFIER;
    }
}

package com.e4u.lesson_service.entities.pojos;

import java.util.List;

import com.e4u.lesson_service.common.constants.Constant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MultipleChoiceExerciseData extends ExerciseData {
    // TODO: Add properties for multiple choice exercise
    private String prompt;
    private String question;
    private String correctAnswer;
    private List<String> options;
    private String sourceLanguage;
    private String targetLanguage;

    @Override
    public String getType() {
        return Constant.MULTIPLE_CHOICE_IDENTIFIER;
    }
}

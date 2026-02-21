package com.e4u.lesson_service.entities.pojos;

import com.e4u.lesson_service.common.constants.Constant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MechanicDrillExerciseData extends ExerciseData {

    private String sentenceTemplate;
    private String correctWord;
    private List<String> distractors;
    private String hint;
    private Boolean isAudioSupported;
    private String sourceLanguage;
    private String targetLanguage;

    @Override
    public String getType() {
        return Constant.MECHANIC_DRILL_IDENTIFIER;
    }
}

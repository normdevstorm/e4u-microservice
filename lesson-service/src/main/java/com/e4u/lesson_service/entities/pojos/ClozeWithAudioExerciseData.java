package com.e4u.lesson_service.entities.pojos;

import com.e4u.lesson_service.common.constants.Constant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClozeWithAudioExerciseData extends ExerciseData {

    private String sentenceTemplate;

    private String correctWord;

    private String hint;

    private String audioUrl;

    @Override
    public String getType() {
        return Constant.CLOZE_WITH_AUDIO_IDENTIFIER;
    }
}

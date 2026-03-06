package com.e4u.learning_service.entities.pojos.answers;

import com.e4u.learning_service.common.constants.Constant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClozeWithAudioAnswer extends ExerciseAnswer {

    private String userAnswer;
    private Boolean isCorrect;

    @Override
    public String getType() {
        return Constant.CLOZE_WITH_AUDIO_IDENTIFIER;
    }
}

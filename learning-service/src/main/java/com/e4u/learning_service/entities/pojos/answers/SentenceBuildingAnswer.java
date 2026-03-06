package com.e4u.learning_service.entities.pojos.answers;

import com.e4u.learning_service.common.constants.Constant;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SentenceBuildingAnswer extends ExerciseAnswer {

    private String builtSentence;
    private List<String> placedWords;
    private Boolean isCorrect;

    @Override
    public String getType() {
        return Constant.SENTENCE_BUILDING_IDENTIFIER;
    }
}

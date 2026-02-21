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
public class SentenceBuildingExerciseData extends ExerciseData {

    private String targetSentence;

    private List<String> scrambledBlocks;

    @Override
    public String getType() {
        return Constant.SENTENCE_BUILDING_IDENTIFIER;
    }
}

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
public class PartialOutputExerciseData extends ExerciseData {

    private String prompt;

    private String setupText;

    private String expectedWord;

    private Integer minWordCount;

    private String hint;

    @Override
    public String getType() {
        return Constant.PARTIAL_OUTPUT_IDENTIFIER;
    }
}

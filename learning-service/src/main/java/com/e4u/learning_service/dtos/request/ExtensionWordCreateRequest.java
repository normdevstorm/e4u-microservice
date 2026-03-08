package com.e4u.learning_service.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtensionWordCreateRequest {
    private String lemma;
    private String type;
    private String ipa;
    private String audioUrl;
    private String destLang;
    private String destLangMeaning;
    private String exampleSentence;
    private String exampleTranslation;
    private String contextSentence;
    private String contextTranslation;
    private String contextUrl;
    private String difficultyLevel;
    private Float frequencyScore;
}

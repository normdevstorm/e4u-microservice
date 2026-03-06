package com.e4u.learning_service.entities.pojos.answers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "CONTEXTUAL_DISCOVERY", value = ContextualDiscoveryAnswer.class),
        @JsonSubTypes.Type(name = "MULTIPLE_CHOICE", value = MultipleChoiceAnswer.class),
        @JsonSubTypes.Type(name = "MECHANIC_DRILL", value = MechanicDrillAnswer.class),
        @JsonSubTypes.Type(name = "TARGET_WORD_INTEGRATION", value = TargetWordIntegrationAnswer.class),
        @JsonSubTypes.Type(name = "SENTENCE_BUILDING", value = SentenceBuildingAnswer.class),
        @JsonSubTypes.Type(name = "ASSISTED_COMPOSITION", value = AssistedCompositionAnswer.class),
        @JsonSubTypes.Type(name = "CLOZE_WITH_AUDIO", value = ClozeWithAudioAnswer.class)
})
public abstract class ExerciseAnswer implements Serializable {

    @JsonIgnore
    public abstract String getType();
}

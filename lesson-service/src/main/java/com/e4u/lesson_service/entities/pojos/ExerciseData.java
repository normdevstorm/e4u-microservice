package com.e4u.lesson_service.entities.pojos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
                // Current exercise type identifiers
                @JsonSubTypes.Type(name = "CONTEXTUAL_DISCOVERY", value = ContextualExerciseData.class),
                @JsonSubTypes.Type(name = "MULTIPLE_CHOICE", value = MultipleChoiceExerciseData.class),
                @JsonSubTypes.Type(name = "MECHANIC_DRILL", value = MechanicDrillExerciseData.class),
                @JsonSubTypes.Type(name = "TARGET_WORD_INTEGRATION", value = TargetWordIntegrationExerciseData.class),
                @JsonSubTypes.Type(name = "SENTENCE_BUILDING", value = SentenceBuildingExerciseData.class),
                @JsonSubTypes.Type(name = "ASSISTED_COMPOSITION", value = AssistedCompositionExerciseData.class),
                @JsonSubTypes.Type(name = "CLOZE_WITH_AUDIO", value = ClozeWithAudioExerciseData.class),
                // Legacy format identifiers (backward compatibility with existing database
                // data)
                @JsonSubTypes.Type(name = "MICRO_TASK_OUTPUT", value = TargetWordIntegrationExerciseData.class),
                @JsonSubTypes.Type(name = "PARTIAL_OUTPUT", value = AssistedCompositionExerciseData.class),
})
public abstract class ExerciseData implements Serializable {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
        public abstract String getType();
}

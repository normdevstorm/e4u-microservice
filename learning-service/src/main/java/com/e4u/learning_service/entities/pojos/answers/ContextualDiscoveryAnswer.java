package com.e4u.learning_service.entities.pojos.answers;

import com.e4u.learning_service.common.constants.Constant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContextualDiscoveryAnswer extends ExerciseAnswer {

    /**
     * The free-form user answer/guess.
     */
    private String userAnswer;

    /**
     * Whether the user has acknowledged the discovery/completion.
     */
    private Boolean acknowledgedDiscovery;

    @Override
    public String getType() {
        return Constant.CONTEXTUAL_DISCOVERY_IDENTIFIER;
    }
}

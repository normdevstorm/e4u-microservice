package com.e4u.learning_service.common.constants;

public class Constant {
    public static final String CONTEXTUAL_DISCOVERY_IDENTIFIER = "CONTEXTUAL_DISCOVERY";
    public static final String MULTIPLE_CHOICE_IDENTIFIER = "MULTIPLE_CHOICE";
    public static final String MECHANIC_DRILL_IDENTIFIER = "MECHANIC_DRILL";
    public static final String TARGET_WORD_INTEGRATION_IDENTIFIER = "TARGET_WORD_INTEGRATION";
    public static final String SENTENCE_BUILDING_IDENTIFIER = "SENTENCE_BUILDING";
    public static final String ASSISTED_COMPOSITION_IDENTIFIER = "ASSISTED_COMPOSITION";
    public static final String CLOZE_WITH_AUDIO_IDENTIFIER = "CLOZE_WITH_AUDIO";

    // Legacy identifiers for backward compatibility with existing database data
    public static final String MICRO_TASK_OUTPUT_IDENTIFIER = "MICRO_TASK_OUTPUT";
    public static final String PARTIAL_OUTPUT_IDENTIFIER = "PARTIAL_OUTPUT";

    public static final String[] URL_WHITE_LIST = {
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/v1/**",
            "/swagger-ui.html",
    };

    public static final String SHOULD_NOT_FILTER_JWT_PATHS_REGEX = "^(/actuator.*|/v3/api-docs.*|/swagger-ui.*|/v1.*)$";

}

package com.e4u.learning_service.converters;

import com.e4u.learning_service.entities.pojos.answers.ExerciseAnswer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JPA Attribute Converter for PostgreSQL JSONB columns storing ExerciseAnswer
 * payloads.
 */
@Converter(autoApply = false)
public class ExerciseAnswerJsonbConverter implements AttributeConverter<ExerciseAnswer, String> {

    private static final Logger log = LoggerFactory.getLogger(ExerciseAnswerJsonbConverter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ExerciseAnswer attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Error converting ExerciseAnswer to JSON string", e);
            throw new IllegalArgumentException("Error converting ExerciseAnswer to JSON string", e);
        }
    }

    @Override
    public ExerciseAnswer convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, ExerciseAnswer.class);
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON string to ExerciseAnswer", e);
            throw new IllegalArgumentException("Error converting JSON string to ExerciseAnswer", e);
        }
    }
}

package com.e4u.learning_service.services.impl;

import com.e4u.learning_service.dtos.request.ExerciseTemplateCreateRequest;
import com.e4u.learning_service.dtos.request.ExerciseTemplateUpdateRequest;
import com.e4u.learning_service.dtos.response.ExerciseTemplateResponse;
import com.e4u.learning_service.entities.ExerciseTemplate;
import com.e4u.learning_service.entities.ExerciseTemplate.ExerciseType;
import com.e4u.learning_service.entities.GlobalDictionary;
import com.e4u.learning_service.entities.LessonTemplate;
import com.e4u.learning_service.entities.WordContextTemplate;
import com.e4u.learning_service.entities.pojos.ContextualExerciseData;
import com.e4u.learning_service.entities.pojos.ExerciseData;
import com.e4u.learning_service.entities.pojos.MechanicDrillExerciseData;
import com.e4u.learning_service.entities.pojos.MultipleChoiceExerciseData;
import com.e4u.learning_service.mapper.ExerciseTemplateMapper;
import com.e4u.learning_service.repositories.ExerciseTemplateRepository;
import com.e4u.learning_service.repositories.GlobalDictionaryRepository;
import com.e4u.learning_service.repositories.LessonTemplateRepository;
import com.e4u.learning_service.repositories.WordContextTemplateRepository;
import com.e4u.learning_service.services.ExerciseTemplateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Implementation of ExerciseTemplateService.
 * Manages both shared templates and user-specific generated exercises.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseTemplateServiceImpl implements ExerciseTemplateService {

    private final ExerciseTemplateRepository exerciseTemplateRepository;
    private final LessonTemplateRepository lessonTemplateRepository;
    private final GlobalDictionaryRepository globalDictionaryRepository;
    private final WordContextTemplateRepository wordContextTemplateRepository;
    private final ExerciseTemplateMapper exerciseTemplateMapper;

    @Override
    @Transactional
    public ExerciseTemplateResponse createExerciseTemplate(ExerciseTemplateCreateRequest request) {
        log.info("Creating exercise template of type: {}", request.getExerciseType());

        LessonTemplate lessonTemplate = null;
        if (request.getLessonTemplateId() != null) {
            lessonTemplate = lessonTemplateRepository.findById(request.getLessonTemplateId())
                    .orElseThrow(() -> new EntityNotFoundException("LessonTemplate not found with ID: " + request.getLessonTemplateId()));
        }

        WordContextTemplate wordContextTemplate = null;
        if (request.getWordContextTemplateId() != null) {
            wordContextTemplate = wordContextTemplateRepository.findById(request.getWordContextTemplateId())
                    .orElseThrow(() -> new EntityNotFoundException("WordContextTemplate not found with ID: " + request.getWordContextTemplateId()));
        }

        ExerciseTemplate exerciseTemplate = ExerciseTemplate.builder()
                .lessonTemplate(lessonTemplate)
                .wordContextTemplate(wordContextTemplate)
                .exerciseType(request.getExerciseType())
                .exerciseData(request.getExerciseData())
                .createdForUserId(request.getCreatedForUserId())
                .build();

        ExerciseTemplate saved = exerciseTemplateRepository.save(exerciseTemplate);
        log.info("Created exercise template with ID: {}", saved.getId());

        return exerciseTemplateMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseTemplateResponse getExerciseTemplateById(UUID id) {
        log.debug("Fetching exercise template by ID: {}", id);

        ExerciseTemplate exerciseTemplate = exerciseTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ExerciseTemplate not found with ID: " + id));

        return exerciseTemplateMapper.toResponse(exerciseTemplate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseTemplateResponse> getExercisesForLesson(UUID lessonTemplateId, UUID userId) {
        log.debug("Fetching exercises for lesson: {} for user: {}", lessonTemplateId, userId);

        List<ExerciseTemplate> exercises;
        if (userId != null) {
            exercises = exerciseTemplateRepository.findByLessonTemplateIdForUser(lessonTemplateId, userId);
        } else {
            exercises = exerciseTemplateRepository.findByLessonTemplateIdAndCreatedForUserIdIsNull(lessonTemplateId);
        }

        return exerciseTemplateMapper.toResponseList(exercises);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseTemplateResponse> getExercisesForLearning(UUID lessonTemplateId, UUID userId) {
        log.debug("Fetching exercises for learning - lesson: {}, user: {}", lessonTemplateId, userId);

        List<ExerciseTemplate> exercises = exerciseTemplateRepository.findByLessonTemplateIdForUser(lessonTemplateId, userId);

        // Return exercises without exposing correct answers
        return exerciseTemplateMapper.toResponseListWithoutAnswer(exercises);
    }

    @Override
    @Transactional
    public ExerciseTemplateResponse updateExerciseTemplate(UUID id, ExerciseTemplateUpdateRequest request) {
        log.info("Updating exercise template: {}", id);

        ExerciseTemplate exerciseTemplate = exerciseTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ExerciseTemplate not found with ID: " + id));

        if (request.getExerciseType() != null) {
            exerciseTemplate.setExerciseType(request.getExerciseType());
        }
        if (request.getExerciseData() != null) {
            exerciseTemplate.setExerciseData(request.getExerciseData());
        }

        ExerciseTemplate saved = exerciseTemplateRepository.save(exerciseTemplate);
        log.info("Updated exercise template: {}", id);

        return exerciseTemplateMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteExerciseTemplate(UUID id) {
        log.info("Deleting exercise template: {}", id);

        if (!exerciseTemplateRepository.existsById(id)) {
            throw new EntityNotFoundException("ExerciseTemplate not found with ID: " + id);
        }

        exerciseTemplateRepository.deleteById(id);
        log.info("Deleted exercise template: {}", id);
    }

    @Override
    @Transactional
    public List<ExerciseTemplateResponse> generateReviewExercises(UUID userId, UUID lessonTemplateId, List<UUID> wordIds) {
        log.info("Generating review exercises for user: {} with {} words", userId, wordIds.size());

        List<ExerciseTemplate> generatedExercises = new ArrayList<>();

        for (UUID wordId : wordIds) {
            GlobalDictionary word = globalDictionaryRepository.findById(wordId)
                    .orElseThrow(() -> new EntityNotFoundException("Word not found with ID: " + wordId));

            // Generate a multiple choice exercise for each word
            ExerciseTemplate exercise = generateMultipleChoiceExercise(word, userId, lessonTemplateId);
            generatedExercises.add(exerciseTemplateRepository.save(exercise));
        }

        log.info("Generated {} review exercises for user: {}", generatedExercises.size(), userId);
        return exerciseTemplateMapper.toResponseList(generatedExercises);
    }

    @Override
    @Transactional
    public ExerciseTemplateResponse generateExerciseForWord(UUID wordId, ExerciseType exerciseType, UUID userId) {
        log.info("Generating {} exercise for word: {} for user: {}", exerciseType, wordId, userId);

        GlobalDictionary word = globalDictionaryRepository.findById(wordId)
                .orElseThrow(() -> new EntityNotFoundException("Word not found with ID: " + wordId));

        ExerciseTemplate exercise;
        switch (exerciseType) {
            case MULTIPLE_CHOICE -> exercise = generateMultipleChoiceExercise(word, userId, null);
            case MECHANIC_DRILL -> exercise = generateMechanicDrillExercise(word, userId);
            case CONTEXTUAL_DISCOVERY -> exercise = generateContextualDiscoveryExercise(word, userId);
            default -> throw new IllegalArgumentException("Unsupported exercise type for auto-generation: " + exerciseType);
        }

        ExerciseTemplate saved = exerciseTemplateRepository.save(exercise);
        log.info("Generated exercise with ID: {}", saved.getId());

        return exerciseTemplateMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseTemplateResponse> getExercisesByType(UUID lessonTemplateId, ExerciseType exerciseType) {
        log.debug("Fetching exercises by type: {} for lesson: {}", exerciseType, lessonTemplateId);

        List<ExerciseTemplate> exercises = exerciseTemplateRepository.findByLessonAndTypeForUser(
                lessonTemplateId, exerciseType, null);

        return exerciseTemplateMapper.toResponseList(exercises);
    }

    // ========== Private Helper Methods for Exercise Generation ==========

    private ExerciseTemplate generateMultipleChoiceExercise(GlobalDictionary word, UUID userId, UUID lessonTemplateId) {
        // TODO: Implement actual distractor generation from similar words
        MultipleChoiceExerciseData exerciseData = new MultipleChoiceExerciseData();
        exerciseData.setQuestion("What is the meaning of '" + word.getLemma() + "'?");
        exerciseData.setPrompt("Select the correct definition");
        exerciseData.setOptions(List.of(
                word.getDefinition(),
                "Option B - placeholder",
                "Option C - placeholder",
                "Option D - placeholder"
        ));
        exerciseData.setCorrectAnswer(word.getDefinition());
        exerciseData.setSourceLanguage("en");
        exerciseData.setTargetLanguage("vi");

        LessonTemplate lessonTemplate = null;
        if (lessonTemplateId != null) {
            lessonTemplate = lessonTemplateRepository.findById(lessonTemplateId).orElse(null);
        }

        return ExerciseTemplate.builder()
                .lessonTemplate(lessonTemplate)
                .wordContextTemplate(null) // TODO: Create or find WordContextTemplate for the word
                .exerciseType(ExerciseType.MULTIPLE_CHOICE)
                .exerciseData(exerciseData)
                .createdForUserId(userId)
                .build();
    }

    private ExerciseTemplate generateMechanicDrillExercise(GlobalDictionary word, UUID userId) {
        // MechanicDrill exercise for fill-in-blank style
        MechanicDrillExerciseData exerciseData = new MechanicDrillExerciseData();
        exerciseData.setSentenceTemplate("The word ___ means: " + word.getDefinition());
        exerciseData.setCorrectAnswer(word.getLemma());
        exerciseData.setDistractors(List.of()); // TODO: Generate distractors
        exerciseData.setHint("Type the correct word");
        exerciseData.setIsAudioSupported(false);
        exerciseData.setSourceLanguage("en");
        exerciseData.setTargetLanguage("vi");

        return ExerciseTemplate.builder()
                .wordContextTemplate(null) // TODO: Create or find WordContextTemplate for the word
                .exerciseType(ExerciseType.MECHANIC_DRILL)
                .exerciseData(exerciseData)
                .createdForUserId(userId)
                .build();
    }

    private ExerciseTemplate generateContextualDiscoveryExercise(GlobalDictionary word, UUID userId) {
        // ContextualDiscovery exercise for flashcard-like review
        ContextualExerciseData exerciseData = new ContextualExerciseData();
        exerciseData.setPrompt(word.getLemma());
        exerciseData.setHint(word.getPhonetic());
        exerciseData.setHighlightedText(word.getDefinition());

        return ExerciseTemplate.builder()
                .wordContextTemplate(null) // TODO: Create or find WordContextTemplate for the word
                .exerciseType(ExerciseType.CONTEXTUAL_DISCOVERY)
                .exerciseData(exerciseData)
                .createdForUserId(userId)
                .build();
    }
}

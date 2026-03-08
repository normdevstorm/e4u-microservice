package com.e4u.learning_service.services.impl;

import com.e4u.learning_service.common.utils.SecurityUtil;
import com.e4u.learning_service.dtos.request.ExtensionWordCreateRequest;
import com.e4u.learning_service.entities.GlobalDictionary;
import com.e4u.learning_service.entities.TranslationDict;
import com.e4u.learning_service.entities.WordContextTemplate;
import com.e4u.learning_service.repositories.GlobalDictionaryRepository;
import com.e4u.learning_service.repositories.WordContextTemplateRepository;
import com.e4u.learning_service.services.ExtensionDigestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of ExtensionDigestService.
 * Handles browser extension word saves with context templates for batch
 * processing.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExtensionDigestServiceImpl implements ExtensionDigestService {

    private final GlobalDictionaryRepository globalDictionaryRepository;
    private final WordContextTemplateRepository wordContextTemplateRepository;

    @Override
    @Transactional
    public boolean saveWordFromExtension(ExtensionWordCreateRequest request) {
        log.debug("Create word from extension request:" + request.toString());
        log.debug("Processing extension word save request: lemma={}, type={}",
                request.getLemma(), request.getType());

        // Check if word with same lemma and part of speech already exists
        Optional<GlobalDictionary> existingWord = globalDictionaryRepository
                .findByLemmaAndPartOfSpeechAndDeletedFalse(
                        request.getLemma().toLowerCase().trim(),
                        request.getType());

        GlobalDictionary word;
        boolean isNewWord;

        if (existingWord.isPresent()) {
            // Word exists, use existing entry
            word = existingWord.get();
            isNewWord = false;
            log.debug("Word already exists in global dictionary: id={}", word.getId());
        } else {
            // Create new word entry in globatranslationl dictionary
            word = createGlobalDictionaryEntry(request);
            isNewWord = true;
            log.info("Created new word in global dictionary: id={}, lemma={}",
                    word.getId(), word.getLemma());
        }

        // Create word context template for batch processing
        createWordContextTemplate(word, request);

        return isNewWord;
    }

    /**
     * Create a new entry in the global dictionary.
     */
    private GlobalDictionary createGlobalDictionaryEntry(ExtensionWordCreateRequest request) {
        GlobalDictionary word = GlobalDictionary.builder()
                .lemma(request.getLemma().toLowerCase().trim())
                .partOfSpeech(request.getType())
                .phonetic(request.getIpa())
                .audioUrl(request.getAudioUrl())
                .exampleSentence(request.getExampleSentence())
                .difficultyLevel(request.getDifficultyLevel())
                .frequencyScore(request.getFrequencyScore())
                // TODO: Set difficulty level based on word frequency analysis
                // TODO: Set definition from external dictionary API
                .build();

        // Add translation with proper bidirectional relationship
        TranslationDict translation = TranslationDict.builder()
                .destLang(request.getDestLang())
                .translation(request.getDestLangMeaning())
                .exampleTranslation(request.getExampleTranslation())
                .build();
        word.addTranslation(translation);

        return globalDictionaryRepository.save(word);
    }

    /**
     * Create a word context template for later batch processing.
     * This stores the user's context from the extension for review/processing.
     */
    private void createWordContextTemplate(GlobalDictionary word, ExtensionWordCreateRequest request) {
        // Check if exact same context already exists to avoid duplicates
        if (wordContextTemplateRepository.existsByWordIdAndContextSentence(
                word.getId(), request.getContextSentence())) {
            log.debug("Context template already exists for word: id={}, skipping creation", word.getId());
            return;
        }

        WordContextTemplate contextTemplate = WordContextTemplate.builder()
                .word(word)
                .contextSentence(request.getContextSentence())
                .contextTranslation(request.getContextTranslation())
                .sourceType(WordContextTemplate.SourceType.USER_EXTENSION)
                // TODO: Extract user ID from security context when authentication is
                // implemented
                .createdForUserId(
                        SecurityUtil.getCurrentUserId())
                .isSelectedByAi(false)
                .build();

        wordContextTemplateRepository.save(contextTemplate);
        log.info("Created word context template for batch processing: wordId={}", word.getId());
    }
}

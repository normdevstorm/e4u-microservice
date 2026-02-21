package com.e4u.lesson_service.repositories;

import com.e4u.lesson_service.entities.UserVocabInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
@DisplayName("UserVocabInstance Repository Tests")
class UserVocabInstanceRepositoryTest {

    @Autowired
    private UserVocabInstanceRepository repository;

    @Autowired
    private EntityManager entityManager;

    private UUID testUserId;
    private UUID testWordLemmaId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testWordLemmaId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should save UserVocabInstance with all fields")
    void shouldSaveUserVocabInstanceWithAllFields() {
        // Given
        UserVocabInstance vocabInstance = createCompleteUserVocabInstance();

        // When
        UserVocabInstance saved = repository.save(vocabInstance);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(testUserId);
        assertThat(saved.getWordLemmaId()).isEqualTo(testWordLemmaId);
        assertThat(saved.getWordText()).isEqualTo("test");
        assertThat(saved.getOriginalSentence()).isEqualTo("This is a test sentence.");
        assertThat(saved.getContextSentence()).isEqualTo("The context sentence.");
        assertThat(saved.getContextSpecificWordMeaning()).isEqualTo("meaning");
        assertThat(saved.getContextSentenceTranslation()).isEqualTo("translation");
        assertThat(saved.getIsJobMatch()).isTrue();
        assertThat(saved.getWeightOfInterest()).isEqualTo(0.8f);
        assertThat(saved.getIsGoalMatch()).isTrue();
        assertThat(saved.getWeightOfGoal()).isEqualTo(0.9f);
        assertThat(saved.getAiReasoning()).isEqualTo("AI reasoning text");
        assertThat(saved.getFrequencyCount()).isEqualTo(5);
        assertThat(saved.getRelevanceScore()).isEqualTo(0.85f);
        assertThat(saved.getIsLearning()).isTrue();
        assertThat(saved.getIsMastered()).isFalse();
        assertThat(saved.getMedia()).isEqualTo("media-url");
        assertThat(saved.getContextUrl()).isEqualTo("https://example.com");
        assertThat(saved.getSourceType()).isEqualTo(UserVocabInstance.SourceType.EXTENSION);

        // Verify BaseEntity fields
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isEqualTo(saved.getUpdatedAt());
        assertThat(saved.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("Should save UserVocabInstance with minimal required fields")
    void shouldSaveUserVocabInstanceWithMinimalFields() {
        // Given
        UserVocabInstance vocabInstance = new UserVocabInstance();
        vocabInstance.setUserId(testUserId);
        vocabInstance.setWordLemmaId(testWordLemmaId);
        vocabInstance.setWordText("minimal");
        vocabInstance.setSourceType(UserVocabInstance.SourceType.AI_SUGGESTION);

        // When
        UserVocabInstance saved = repository.save(vocabInstance);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(testUserId);
        assertThat(saved.getWordLemmaId()).isEqualTo(testWordLemmaId);
        assertThat(saved.getWordText()).isEqualTo("minimal");
        assertThat(saved.getSourceType()).isEqualTo(UserVocabInstance.SourceType.AI_SUGGESTION);
        assertThat(saved.getIsMastered()).isFalse(); // Default value
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should save UserVocabInstance with AI_SUGGESTION source type")
    void shouldSaveUserVocabInstanceWithAISuggestionSourceType() {
        // Given
        UserVocabInstance vocabInstance = createCompleteUserVocabInstance();
        vocabInstance.setSourceType(UserVocabInstance.SourceType.AI_SUGGESTION);
        vocabInstance.setAiReasoning("AI suggested this word based on user's learning goals");

        // When
        UserVocabInstance saved = repository.save(vocabInstance);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(saved.getSourceType()).isEqualTo(UserVocabInstance.SourceType.AI_SUGGESTION);
        assertThat(saved.getAiReasoning()).contains("AI suggested");
    }

    @Test
    @DisplayName("Should save UserVocabInstance with EXTENSION source type")
    void shouldSaveUserVocabInstanceWithExtensionSourceType() {
        // Given
        UserVocabInstance vocabInstance = createCompleteUserVocabInstance();
        vocabInstance.setSourceType(UserVocabInstance.SourceType.EXTENSION);
        vocabInstance.setContextUrl("https://example.com/article");

        // When
        UserVocabInstance saved = repository.save(vocabInstance);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(saved.getSourceType()).isEqualTo(UserVocabInstance.SourceType.EXTENSION);
        assertThat(saved.getContextUrl()).isEqualTo("https://example.com/article");
    }

    @Test
    @DisplayName("Should set createdAt and updatedAt automatically on save")
    void shouldSetTimestampsAutomatically() {
        // Given
        UserVocabInstance vocabInstance = createCompleteUserVocabInstance();
        Instant beforeSave = Instant.now();

        // When
        UserVocabInstance saved = repository.save(vocabInstance);
        entityManager.flush();
        entityManager.clear();
        Instant afterSave = Instant.now();

        // Then
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isAfterOrEqualTo(beforeSave);
        assertThat(saved.getCreatedAt()).isBeforeOrEqualTo(afterSave);
        assertThat(saved.getUpdatedAt()).isAfterOrEqualTo(beforeSave);
        assertThat(saved.getUpdatedAt()).isBeforeOrEqualTo(afterSave);
    }

    @Test
    @DisplayName("Should save UserVocabInstance with mastered status")
    void shouldSaveUserVocabInstanceWithMasteredStatus() {
        // Given
        UserVocabInstance vocabInstance = createCompleteUserVocabInstance();
        vocabInstance.setIsMastered(true);
        vocabInstance.setIsLearning(false);

        // When
        UserVocabInstance saved = repository.save(vocabInstance);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(saved.getIsMastered()).isTrue();
        assertThat(saved.getIsLearning()).isFalse();
    }

    @Test
    @DisplayName("Should save UserVocabInstance with null optional fields")
    void shouldSaveUserVocabInstanceWithNullOptionalFields() {
        // Given
        UserVocabInstance vocabInstance = new UserVocabInstance();
        vocabInstance.setUserId(testUserId);
        vocabInstance.setWordLemmaId(testWordLemmaId);
        vocabInstance.setWordText("test");
        vocabInstance.setSourceType(UserVocabInstance.SourceType.EXTENSION);
        // Leave optional fields as null

        // When
        UserVocabInstance saved = repository.save(vocabInstance);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getOriginalSentence()).isNull();
        assertThat(saved.getContextSentence()).isNull();
        assertThat(saved.getContextSpecificWordMeaning()).isNull();
        assertThat(saved.getIsJobMatch()).isNull();
        assertThat(saved.getWeightOfInterest()).isNull();
        assertThat(saved.getIsGoalMatch()).isNull();
        assertThat(saved.getWeightOfGoal()).isNull();
        assertThat(saved.getAiReasoning()).isNull();
        assertThat(saved.getFrequencyCount()).isNull();
        assertThat(saved.getRelevanceScore()).isNull();
        assertThat(saved.getIsLearning()).isNull();
        assertThat(saved.getMedia()).isNull();
        assertThat(saved.getContextUrl()).isNull();
    }

    @Test
    @DisplayName("Should retrieve saved UserVocabInstance by ID")
    void shouldRetrieveSavedUserVocabInstanceById() {
        // Given
        UserVocabInstance vocabInstance = createCompleteUserVocabInstance();
        UserVocabInstance saved = repository.save(vocabInstance);
        repository.flush();

        // When
        UserVocabInstance found = repository.findById(saved.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getWordText()).isEqualTo("test");
        assertThat(found.getUserId()).isEqualTo(testUserId);
    }

    private UserVocabInstance createCompleteUserVocabInstance() {
        UserVocabInstance vocabInstance = new UserVocabInstance();
        vocabInstance.setUserId(testUserId);
        vocabInstance.setWordLemmaId(testWordLemmaId);
        vocabInstance.setWordText("test");
        vocabInstance.setOriginalSentence("This is a test sentence.");
        vocabInstance.setContextSentence("The context sentence.");
        vocabInstance.setContextSpecificWordMeaning("meaning");
        vocabInstance.setContextSentenceTranslation("translation");
        vocabInstance.setIsJobMatch(true);
        vocabInstance.setWeightOfInterest(0.8f);
        vocabInstance.setIsGoalMatch(true);
        vocabInstance.setWeightOfGoal(0.9f);
        vocabInstance.setAiReasoning("AI reasoning text");
        vocabInstance.setFrequencyCount(5);
        vocabInstance.setRelevanceScore(0.85f);
        vocabInstance.setIsLearning(true);
        vocabInstance.setIsMastered(false);
        vocabInstance.setMedia("media-url");
        vocabInstance.setContextUrl("https://example.com");
        vocabInstance.setSourceType(UserVocabInstance.SourceType.EXTENSION);
        return vocabInstance;
    }
}


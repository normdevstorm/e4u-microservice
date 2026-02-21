// package com.e4u.lesson_service.repositories;

// import com.e4u.lesson_service.entities.LessonExercise;
// import com.e4u.lesson_service.entities.UserVocabInstance;
// import com.e4u.lesson_service.entities.pojos.ContextualExerciseData;
// import com.e4u.lesson_service.entities.pojos.ExerciseData;
// import com.e4u.lesson_service.entities.pojos.MultipleChoiceExerciseData;
// import jakarta.persistence.EntityManager;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;
// import java.util.UUID;

// import static org.assertj.core.api.Assertions.assertThat;

// @SpringBootTest
// @ActiveProfiles("dev")
// @Transactional
// @DisplayName("LessonExercise Repository Tests")
// class LessonExerciseRepositoryTest {

//     @Autowired
//     private LessonExerciseRepository repository;

//     @Autowired
//     private UserVocabInstanceRepository userVocabInstanceRepository;

//     @Autowired
//     private EntityManager entityManager;

//     private UserVocabInstance testWordInstance;
//     private UUID testUserId;
//     private UUID testWordLemmaId;

//     @BeforeEach
//     void setUp() {
//         testUserId = UUID.randomUUID();
//         testWordLemmaId = UUID.randomUUID();

//         // Create a UserVocabInstance for testing
//         testWordInstance = new UserVocabInstance();
//         testWordInstance.setUserId(testUserId);
//         testWordInstance.setWordLemmaId(testWordLemmaId);
//         testWordInstance.setWordText("test-word");
//         testWordInstance.setSourceType(UserVocabInstance.SourceType.EXTENSION);
//         testWordInstance = userVocabInstanceRepository.save(testWordInstance);
//         entityManager.flush();
//     }

//     @Test
//     @DisplayName("Should save LessonExercise with ContextualExerciseData")
//     void shouldSaveLessonExerciseWithContextualExerciseData() {
//         // Given
//         LessonExercise exercise = new LessonExercise();
//         exercise.setWordInstance(testWordInstance);
//         exercise.setExerciseType(LessonExercise.ExerciseType.CONTEXTUAL_DISCOVERY);
//         exercise.setSequenceOrder(1);

//         ContextualExerciseData exerciseData = new ContextualExerciseData();
//         exerciseData.setHint("This is a hint");
//         exerciseData.setHighlightedText("highlighted text");
//         exercise.setExerciseData(exerciseData);

//         // When
//         LessonExercise saved = repository.save(exercise);
//         entityManager.flush();
//         entityManager.clear();

//         // Then
//         assertThat(saved.getId()).isNotNull();
//         assertThat(saved.getWordInstance()).isNotNull();
//         assertThat(saved.getWordInstance().getId()).isEqualTo(testWordInstance.getId());
//         assertThat(saved.getExerciseType()).isEqualTo(LessonExercise.ExerciseType.CONTEXTUAL_DISCOVERY);
//         assertThat(saved.getSequenceOrder()).isEqualTo(1);
//         assertThat(saved.getExerciseData()).isNotNull();
//         assertThat(saved.getExerciseData()).isInstanceOf(ContextualExerciseData.class);
        
//         ContextualExerciseData savedData = (ContextualExerciseData) saved.getExerciseData();
//         assertThat(savedData.getHint()).isEqualTo("This is a hint");
//         assertThat(savedData.getHighlightedText()).isEqualTo("highlighted text");
//         assertThat(savedData.getType()).isEqualTo("exercise.contextual_discovery");

//         // Verify BaseEntity fields
//         assertThat(saved.getCreatedAt()).isNotNull();
//         assertThat(saved.getUpdatedAt()).isNotNull();
//         assertThat(saved.getCreatedAt()).isEqualTo(saved.getUpdatedAt());
//         assertThat(saved.isDeleted()).isFalse();
//     }

//     @Test
//     @DisplayName("Should save LessonExercise with MultipleChoiceExerciseData")
//     void shouldSaveLessonExerciseWithMultipleChoiceExerciseData() {
//         // Given
//         LessonExercise exercise = new LessonExercise();
//         exercise.setWordInstance(testWordInstance);
//         exercise.setExerciseType(LessonExercise.ExerciseType.MULTIPLE_CHOICE);
//         exercise.setSequenceOrder(2);

//         MultipleChoiceExerciseData exerciseData = new MultipleChoiceExerciseData();
//         exercise.setExerciseData(exerciseData);

//         // When
//         LessonExercise saved = repository.save(exercise);
//         entityManager.flush();
//         entityManager.clear();

//         // Then
//         assertThat(saved.getId()).isNotNull();
//         assertThat(saved.getExerciseType()).isEqualTo(LessonExercise.ExerciseType.MULTIPLE_CHOICE);
//         assertThat(saved.getSequenceOrder()).isEqualTo(2);
//         assertThat(saved.getExerciseData()).isNotNull();
//         assertThat(saved.getExerciseData()).isInstanceOf(MultipleChoiceExerciseData.class);
//         assertThat(saved.getExerciseData().getType()).isEqualTo("exercise.multiple_choice");
//     }

//     @Test
//     @DisplayName("Should save LessonExercise with all ExerciseType enum values")
//     void shouldSaveLessonExerciseWithAllExerciseTypes() {
//         // Test each exercise type
//         LessonExercise.ExerciseType[] exerciseTypes = {
//             LessonExercise.ExerciseType.CONTEXTUAL_DISCOVERY,
//             LessonExercise.ExerciseType.MULTIPLE_CHOICE,
//             LessonExercise.ExerciseType.MECHANIC_DRILL,
//             LessonExercise.ExerciseType.MICRO_TASK_OUTPUT,
//             LessonExercise.ExerciseType.SENTENCE_BUILDING
//         };

//         for (int i = 0; i < exerciseTypes.length; i++) {
//             // Given
//             LessonExercise exercise = new LessonExercise();
//             exercise.setWordInstance(testWordInstance);
//             exercise.setExerciseType(exerciseTypes[i]);
//             exercise.setSequenceOrder(i + 1);

//             // Create appropriate ExerciseData based on type
//             ExerciseData exerciseData = createExerciseDataForType(exerciseTypes[i]);
//             exercise.setExerciseData(exerciseData);

//             // When
//             LessonExercise saved = repository.save(exercise);
//             entityManager.flush();
//             entityManager.clear();

//             // Then
//             assertThat(saved.getId()).isNotNull();
//             assertThat(saved.getExerciseType()).isEqualTo(exerciseTypes[i]);
//             assertThat(saved.getExerciseData()).isNotNull();
//             assertThat(saved.getExerciseData().getType()).isEqualTo(exerciseTypes[i].getSubtypeIdentifier());
//         }
//     }

//     @Test
//     @DisplayName("Should save LessonExercise with null exerciseData")
//     void shouldSaveLessonExerciseWithNullExerciseData() {
//         // Given
//         LessonExercise exercise = new LessonExercise();
//         exercise.setWordInstance(testWordInstance);
//         exercise.setExerciseType(LessonExercise.ExerciseType.CONTEXTUAL_DISCOVERY);
//         exercise.setSequenceOrder(1);
//         exercise.setExerciseData(null);

//         // When
//         LessonExercise saved = repository.save(exercise);
//         entityManager.flush();
//         entityManager.clear();

//         // Then
//         assertThat(saved.getId()).isNotNull();
//         assertThat(saved.getExerciseData()).isNull();
//         assertThat(saved.getExerciseType()).isEqualTo(LessonExercise.ExerciseType.CONTEXTUAL_DISCOVERY);
//     }

//     @Test
//     @DisplayName("Should save LessonExercise with null sequenceOrder")
//     void shouldSaveLessonExerciseWithNullSequenceOrder() {
//         // Given
//         LessonExercise exercise = new LessonExercise();
//         exercise.setWordInstance(testWordInstance);
//         exercise.setExerciseType(LessonExercise.ExerciseType.CONTEXTUAL_DISCOVERY);
//         exercise.setSequenceOrder(null);

//         ContextualExerciseData exerciseData = new ContextualExerciseData();
//         exerciseData.setHint("Hint");
//         exercise.setExerciseData(exerciseData);

//         // When
//         LessonExercise saved = repository.save(exercise);
//         entityManager.flush();
//         entityManager.clear();

//         // Then
//         assertThat(saved.getId()).isNotNull();
//         assertThat(saved.getSequenceOrder()).isNull();
//     }

//     @Test
//     @DisplayName("Should set createdAt and updatedAt automatically on save")
//     void shouldSetTimestampsAutomatically() {
//         // Given
//         LessonExercise exercise = createCompleteLessonExercise();
//         java.time.Instant beforeSave = java.time.Instant.now();

//         // When
//         LessonExercise saved = repository.save(exercise);
//         entityManager.flush();
//         java.time.Instant afterSave = java.time.Instant.now();

//         // Then
//         assertThat(saved.getCreatedAt()).isNotNull();
//         assertThat(saved.getUpdatedAt()).isNotNull();
//         assertThat(saved.getCreatedAt()).isAfterOrEqualTo(beforeSave);
//         assertThat(saved.getCreatedAt()).isBeforeOrEqualTo(afterSave);
//         assertThat(saved.getUpdatedAt()).isAfterOrEqualTo(beforeSave);
//         assertThat(saved.getUpdatedAt()).isBeforeOrEqualTo(afterSave);
//     }

//     @Test
//     @DisplayName("Should retrieve saved LessonExercise by ID")
//     void shouldRetrieveSavedLessonExerciseById() {
//         // Given
//         LessonExercise exercise = createCompleteLessonExercise();
//         LessonExercise saved = repository.save(exercise);
//         entityManager.flush();
//         entityManager.clear();

//         // When
//         LessonExercise found = repository.findById(saved.getId()).orElse(null);

//         // Then
//         assertThat(found).isNotNull();
//         assertThat(found.getId()).isEqualTo(saved.getId());
//         assertThat(found.getExerciseType()).isEqualTo(LessonExercise.ExerciseType.CONTEXTUAL_DISCOVERY);
//         assertThat(found.getSequenceOrder()).isEqualTo(1);
//         assertThat(found.getWordInstance().getId()).isEqualTo(testWordInstance.getId());
//     }

//     @Test
//     @DisplayName("Should find LessonExercise by ExerciseType")
//     void shouldFindLessonExerciseByExerciseType() {
//         // Given
//         LessonExercise exercise1 = createCompleteLessonExercise();
//         exercise1.setExerciseType(LessonExercise.ExerciseType.CONTEXTUAL_DISCOVERY);
//         repository.save(exercise1);

//         LessonExercise exercise2 = new LessonExercise();
//         exercise2.setWordInstance(testWordInstance);
//         exercise2.setExerciseType(LessonExercise.ExerciseType.MULTIPLE_CHOICE);
//         exercise2.setSequenceOrder(2);
//         MultipleChoiceExerciseData exerciseData2 = new MultipleChoiceExerciseData();
//         exercise2.setExerciseData(exerciseData2);
//         repository.save(exercise2);

//         entityManager.flush();
//         entityManager.clear();

//         // When
//         List<LessonExercise> contextualExercises = repository.findByExerciseType(
//             LessonExercise.ExerciseType.CONTEXTUAL_DISCOVERY
//         );
//         List<LessonExercise> multipleChoiceExercises = repository.findByExerciseType(
//             LessonExercise.ExerciseType.MULTIPLE_CHOICE
//         );

//         // Then
//         assertThat(contextualExercises).isNotEmpty();
//         assertThat(contextualExercises).anyMatch(e -> 
//             e.getExerciseType() == LessonExercise.ExerciseType.CONTEXTUAL_DISCOVERY
//         );
//         assertThat(multipleChoiceExercises).isNotEmpty();
//         assertThat(multipleChoiceExercises).anyMatch(e -> 
//             e.getExerciseType() == LessonExercise.ExerciseType.MULTIPLE_CHOICE
//         );
//     }

//     @Test
//     @DisplayName("Should save multiple LessonExercises for same UserVocabInstance")
//     void shouldSaveMultipleLessonExercisesForSameWordInstance() {
//         // Given
//         LessonExercise exercise1 = createCompleteLessonExercise();
//         exercise1.setSequenceOrder(1);

//         LessonExercise exercise2 = createCompleteLessonExercise();
//         exercise2.setSequenceOrder(2);
//         exercise2.setExerciseType(LessonExercise.ExerciseType.MULTIPLE_CHOICE);
//         MultipleChoiceExerciseData exerciseData2 = new MultipleChoiceExerciseData();
//         exercise2.setExerciseData(exerciseData2);

//         // When
//         LessonExercise saved1 = repository.save(exercise1);
//         LessonExercise saved2 = repository.save(exercise2);
//         entityManager.flush();
//         entityManager.clear();

//         // Then
//         assertThat(saved1.getId()).isNotNull();
//         assertThat(saved2.getId()).isNotNull();
//         assertThat(saved1.getId()).isNotEqualTo(saved2.getId());
//         assertThat(saved1.getWordInstance().getId()).isEqualTo(testWordInstance.getId());
//         assertThat(saved2.getWordInstance().getId()).isEqualTo(testWordInstance.getId());
//         assertThat(saved1.getSequenceOrder()).isEqualTo(1);
//         assertThat(saved2.getSequenceOrder()).isEqualTo(2);
//     }

//     @Test
//     @DisplayName("Should verify ExerciseType enum subtypeIdentifier values")
//     void shouldVerifyExerciseTypeSubtypeIdentifiers() {
//         // Then
//         assertThat(LessonExercise.ExerciseType.CONTEXTUAL_DISCOVERY.getSubtypeIdentifier())
//             .isEqualTo("exercise.contextual_discovery");
//         assertThat(LessonExercise.ExerciseType.MULTIPLE_CHOICE.getSubtypeIdentifier())
//             .isEqualTo("exercise.multiple_choice");
//         assertThat(LessonExercise.ExerciseType.MECHANIC_DRILL.getSubtypeIdentifier())
//             .isEqualTo("exercise.mechanic_drill");
//         assertThat(LessonExercise.ExerciseType.MICRO_TASK_OUTPUT.getSubtypeIdentifier())
//             .isEqualTo("exercise.micro_task_output");
//         assertThat(LessonExercise.ExerciseType.SENTENCE_BUILDING.getSubtypeIdentifier())
//             .isEqualTo("exercise.sentence_building");
//     }

//     private LessonExercise createCompleteLessonExercise() {
//         LessonExercise exercise = new LessonExercise();
//         exercise.setWordInstance(testWordInstance);
//         exercise.setExerciseType(LessonExercise.ExerciseType.CONTEXTUAL_DISCOVERY);
//         exercise.setSequenceOrder(1);

//         ContextualExerciseData exerciseData = new ContextualExerciseData();
//         exerciseData.setHint("Complete hint");
//         exerciseData.setHighlightedText("Complete highlighted text");
//         exercise.setExerciseData(exerciseData);

//         return exercise;
//     }

//     private ExerciseData createExerciseDataForType(LessonExercise.ExerciseType exerciseType) {
//         return switch (exerciseType) {
//             case CONTEXTUAL_DISCOVERY -> {
//                 ContextualExerciseData data = new ContextualExerciseData();
//                 data.setHint("Hint");
//                 data.setHighlightedText("Highlighted");
//                 yield data;
//             }
//             case MULTIPLE_CHOICE -> new MultipleChoiceExerciseData();
//             case MECHANIC_DRILL -> new com.e4u.lesson_service.entities.pojos.MechanicDrillExerciseData();
//             case MICRO_TASK_OUTPUT -> new com.e4u.lesson_service.entities.pojos.MicroTaskOutputExerciseData();
//             case SENTENCE_BUILDING -> new com.e4u.lesson_service.entities.pojos.SentenceBuildingExerciseData();
//         };
//     }
// }


--liquibase formatted sql
--changeset system:test-data-014 context:test,dev comment:Insert richer exercise templates across all exercise types

-- ============================================
-- Additional Exercise Templates covering all exercise types
-- Linked to existing sample lessons/word contexts
-- NOTE: exercise_payload JSON matches the ExerciseData POJO structures
-- ============================================
INSERT INTO public.exercise_templates (id, lesson_template_id, word_context_template_id, exercise_type, exercise_payload, created_for_user_id, created_at, deleted)
VALUES 
    -- CONTEXTUAL_DISCOVERY (ContextualExerciseData)
    -- Flashcard-style: front = word + POS, back = example sentence + translation
    ('ef110001-0002-0000-0000-000000000001', 'de110001-0001-0000-0000-000000000001', 'bc110001-0001-0000-0000-000000000002', 'CONTEXTUAL_DISCOVERY',
    '{"type": "CONTEXTUAL_DISCOVERY", "prompt": "Đọc câu và tập trung vào từ được tô sáng. Dựa vào ngữ cảnh, hãy suy ra nghĩa của từ đó.", "hint": "Hãy chú ý cách từ này mô tả báo cáo trong câu.", "highlightedText": "comprehensive", "audioUrl": null}',
     NULL,
     CURRENT_TIMESTAMP, false),

    -- MULTIPLE_CHOICE (MultipleChoiceExerciseData)
    -- Source language: English, target language: Vietnamese
    ('ef110001-0002-0000-0000-000000000002', 'de110001-0001-0000-0000-000000000001', 'bc110001-0001-0000-0000-000000000004', 'MULTIPLE_CHOICE',
     '{"type": "MULTIPLE_CHOICE", "prompt": "Chọn nghĩa tiếng Việt đúng cho từ analyze.", "question": "Analyze có nghĩa là gì trong ngữ cảnh học thuật?", "options": ["phân tích chi tiết", "chép lại", "tóm tắt ngắn gọn", "bỏ qua dữ liệu"], "correctAnswer": "phân tích chi tiết", "sourceLanguage": "en", "targetLanguage": "vi"}',
     NULL,
     CURRENT_TIMESTAMP, false),

    -- MECHANIC_DRILL (MechanicDrillExerciseData)
    -- Sentence template blanks the target word
    ('ef221002-0002-0000-0000-000000000001', 'de221002-0001-0000-0000-000000000001', 'bc221002-0001-0000-0000-000000000005', 'MECHANIC_DRILL',
     '{"type": "MECHANIC_DRILL", "sentenceTemplate": "The _____ for the offer is next week.", "correctAnswer": "deadline", "distractors": ["meeting", "project", "schedule"], "hint": "It is the final date when something must be finished.", "isAudioSupported": false, "sourceLanguage": "en", "targetLanguage": "en"}',
     NULL,
     CURRENT_TIMESTAMP, false),

    -- TARGET_WORD_INTEGRATION (TargetWordIntegrationExerciseData)
    ('ef331001-0002-0000-0000-000000000001', 'de331001-0001-0000-0000-000000000001', 'bc331001-0001-0000-0000-000000000001', 'TARGET_WORD_INTEGRATION',
     '{"type": "TARGET_WORD_INTEGRATION", "prompt": "Your friend helped you finish a difficult project. Write one sentence to thank them using the word appreciate.", "targetWord": "appreciate", "contextHints": ["I", "you", "help", "difficult project", "appreciate"], "simplifiedSentence": "I really ______ your help with this project.", "minWords": 6, "maxWords": 20, "exampleResponse": "I really appreciate your help with this project."}',
     NULL,
     CURRENT_TIMESTAMP, false),

    -- SENTENCE_BUILDING (SentenceBuildingExerciseData)
    ('ef331001-0002-0000-0000-000000000002', 'de331001-0001-0000-0000-000000000001', 'bc331001-0001-0000-0000-000000000004', 'SENTENCE_BUILDING',
     '{"type": "SENTENCE_BUILDING", "targetSentence": "I apologize for being late.", "scrambledBlocks": ["for", "apologize", "late", "I", "being"]}',
     NULL,
     CURRENT_TIMESTAMP, false),

    -- ASSISTED_COMPOSITION (AssistedCompositionExerciseData)
    -- Harder than mechanic drill: prompt does NOT mention the target word
    ('ef441001-0002-0000-0000-000000000001', 'de441001-0001-0000-0000-000000000001', 'bc441001-0001-0000-0000-000000000006', 'ASSISTED_COMPOSITION',
     '{"type": "ASSISTED_COMPOSITION", "prompt": "Hoàn thành câu để nói rằng vốn từ mạnh là nền tảng cho việc đọc hiệu quả.", "setupText": "A strong __________ is fundamental to effective reading.", "expectedWord": "vocabulary", "minWordCount": 1, "hint": "Nó nói về tất cả các từ bạn biết và sử dụng.", "alternativeAnswers": ["strong vocabulary", "good vocabulary"], "correctFeedback": "Great work. A strong vocabulary really helps with reading."}',
     NULL,
     CURRENT_TIMESTAMP, false),

    -- CLOZE_WITH_AUDIO (ClozeWithAudioExerciseData)
    ('ef221002-0002-0000-0000-000000000002', 'de221002-0001-0000-0000-000000000001', 'bc221002-0001-0000-0000-000000000001', 'CLOZE_WITH_AUDIO',
     '{"type": "CLOZE_WITH_AUDIO", "sentenceTemplate": "We need to _____ the contract terms.", "correctAnswer": "negotiate", "hint": "You do this when you discuss terms to reach an agreement.", "audioUrl": null}',
     NULL,
     CURRENT_TIMESTAMP, false);

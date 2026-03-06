--liquibase formatted sql
--changeset system:test-data-001 context:test,dev comment:Insert test goal definitions

-- ============================================
-- TEST DATA: Goal Definitions (4 goals)
-- ============================================
INSERT INTO public.goal_definitions (id, goal_name, skills_focus, is_active, created_at, deleted)
VALUES 
    ('11111111-1111-1111-1111-111111111111', 'IELTS Preparation', ARRAY['reading', 'writing', 'listening', 'speaking'], true, CURRENT_TIMESTAMP, false),
    ('22222222-2222-2222-2222-222222222222', 'Business English', ARRAY['speaking', 'writing', 'vocabulary'], true, CURRENT_TIMESTAMP, false),
    ('33333333-3333-3333-3333-333333333333', 'Daily Conversation', ARRAY['speaking', 'listening', 'vocabulary'], true, CURRENT_TIMESTAMP, false),
    ('44444444-4444-4444-4444-444444444444', 'Academic Writing', ARRAY['writing', 'reading', 'grammar'], true, CURRENT_TIMESTAMP, false);

--changeset system:test-data-002 context:test,dev comment:Insert test global dictionary words

-- ============================================
-- TEST DATA: Global Dictionary (80 words for comprehensive coverage)
-- ============================================
INSERT INTO public.global_dictionary (id, lemma, part_of_speech, definition, difficulty_level, frequency_score, phonetic, audio_url, example_sentence, created_at, deleted)
VALUES 
    -- Academic/IELTS vocabulary (20 words)
    ('a0000000-0000-0000-0000-000000000001', 'accommodate', 'verb', 'to provide lodging or room for; to adapt to', 'B2', 0.75, '/əˈkɒmədeɪt/', NULL, 'The hotel can accommodate up to 500 guests.', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000002', 'comprehensive', 'adjective', 'complete and including everything that is necessary', 'B2', 0.82, '/ˌkɒmprɪˈhensɪv/', NULL, 'The report provides a comprehensive analysis.', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000003', 'elaborate', 'verb', 'to add more information or details', 'B2', 0.68, '/ɪˈlæbəreɪt/', NULL, 'Could you elaborate on your statement?', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000004', 'facilitate', 'verb', 'to make an action or process easy or easier', 'C1', 0.71, '/fəˈsɪlɪteɪt/', NULL, 'The new system will facilitate communication.', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000005', 'implement', 'verb', 'to put a plan or system into operation', 'B2', 0.88, '/ˈɪmplɪment/', NULL, 'The company plans to implement the new policy.', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000006', 'perspective', 'noun', 'a particular way of viewing things', 'B2', 0.85, '/pəˈspektɪv/', NULL, 'Try to see from a different perspective.', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000007', 'sustainable', 'adjective', 'able to continue over a period of time', 'B2', 0.77, '/səˈsteɪnəbl/', NULL, 'We need a more sustainable approach.', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000008', 'significant', 'adjective', 'important or large enough to have an effect', 'B1', 0.92, '/sɪɡˈnɪfɪkənt/', NULL, 'There was a significant improvement.', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000009', 'analyze', 'verb', 'to examine in detail', 'B2', 0.86, '/ˈænəlaɪz/', NULL, 'We need to analyze the data carefully.', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000010', 'demonstrate', 'verb', 'to show or prove something clearly', 'B2', 0.84, '/ˈdemənstreɪt/', NULL, 'The results demonstrate the effectiveness.', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000011', 'hypothesis', 'noun', 'an idea or explanation that is tested', 'C1', 0.65, '/haɪˈpɒθəsɪs/', NULL, 'The hypothesis was proven correct.', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000012', 'evidence', 'noun', 'facts or signs that show something is true', 'B1', 0.91, '/ˈevɪdəns/', NULL, 'There is strong evidence to support this.', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000013', 'conclude', 'verb', 'to come to an end; to form a final opinion', 'B2', 0.83, '/kənˈkluːd/', NULL, 'We can conclude that the theory is valid.', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000014', 'interpret', 'verb', 'to explain the meaning of something', 'B2', 0.76, '/ɪnˈtɜːprɪt/', NULL, 'How do you interpret these results?', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000015', 'criteria', 'noun', 'standards used to judge or decide something', 'B2', 0.79, '/kraɪˈtɪəriə/', NULL, 'What criteria will be used for selection?', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000016', 'phenomenon', 'noun', 'something that exists and can be seen or felt', 'C1', 0.72, '/fəˈnɒmɪnən/', NULL, 'This phenomenon is difficult to explain.', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000017', 'theory', 'noun', 'a formal set of ideas to explain something', 'B1', 0.89, '/ˈθɪəri/', NULL, 'The theory has been widely accepted.', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000018', 'contribute', 'verb', 'to give something to help achieve a goal', 'B2', 0.87, '/kənˈtrɪbjuːt/', NULL, 'Many factors contribute to success.', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000019', 'evaluate', 'verb', 'to judge or assess the value of something', 'B2', 0.81, '/ɪˈvæljueɪt/', NULL, 'We need to evaluate the options carefully.', CURRENT_TIMESTAMP, false),
    ('a0000000-0000-0000-0000-000000000020', 'fundamental', 'adjective', 'forming a necessary base or core', 'B2', 0.78, '/ˌfʌndəˈmentl/', NULL, 'This is a fundamental principle.', CURRENT_TIMESTAMP, false),
    
    -- Business vocabulary (20 words)
    ('b0000000-0000-0000-0000-000000000001', 'negotiate', 'verb', 'to discuss to reach an agreement', 'B2', 0.79, '/nɪˈɡəʊʃieɪt/', NULL, 'We are negotiating a new contract.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000002', 'stakeholder', 'noun', 'a person with an interest in a business', 'B2', 0.74, '/ˈsteɪkhəʊldə/', NULL, 'All stakeholders were consulted.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000003', 'revenue', 'noun', 'income from business activities', 'B2', 0.85, '/ˈrevənjuː/', NULL, 'The company increased its revenue.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000004', 'deadline', 'noun', 'the latest time by which something must be done', 'B1', 0.93, '/ˈdedlaɪn/', NULL, 'We must meet the deadline.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000005', 'collaborate', 'verb', 'to work jointly with others', 'B2', 0.82, '/kəˈlæbəreɪt/', NULL, 'We collaborate with international partners.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000006', 'strategy', 'noun', 'a plan designed to achieve a goal', 'B2', 0.88, '/ˈstrætədʒi/', NULL, 'We need a new marketing strategy.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000007', 'budget', 'noun', 'an estimate of income and expenditure', 'B1', 0.91, '/ˈbʌdʒɪt/', NULL, 'The project is within budget.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000008', 'proposal', 'noun', 'a plan or suggestion put forward', 'B2', 0.84, '/prəˈpəʊzl/', NULL, 'The proposal was accepted.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000009', 'objective', 'noun', 'a goal or aim', 'B2', 0.86, '/əbˈdʒektɪv/', NULL, 'Our main objective is growth.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000010', 'delegate', 'verb', 'to entrust a task to another person', 'B2', 0.73, '/ˈdelɪɡeɪt/', NULL, 'Learn to delegate responsibilities.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000011', 'prioritize', 'verb', 'to designate as more important', 'B2', 0.77, '/praɪˈɒrɪtaɪz/', NULL, 'We need to prioritize our tasks.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000012', 'optimize', 'verb', 'to make the best use of', 'B2', 0.75, '/ˈɒptɪmaɪz/', NULL, 'We should optimize our resources.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000013', 'leverage', 'verb', 'to use something to maximum advantage', 'C1', 0.71, '/ˈliːvərɪdʒ/', NULL, 'We can leverage our expertise.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000014', 'milestone', 'noun', 'an important point in development', 'B2', 0.79, '/ˈmaɪlstəʊn/', NULL, 'We reached a significant milestone.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000015', 'benchmark', 'noun', 'a standard for comparison', 'B2', 0.72, '/ˈbentʃmɑːk/', NULL, 'This serves as a benchmark.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000016', 'streamline', 'verb', 'to make more efficient', 'B2', 0.74, '/ˈstriːmlaɪn/', NULL, 'We streamlined our processes.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000017', 'forecast', 'verb', 'to predict future events', 'B2', 0.81, '/ˈfɔːkɑːst/', NULL, 'We forecast strong growth.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000018', 'allocate', 'verb', 'to distribute for a particular purpose', 'B2', 0.76, '/ˈæləkeɪt/', NULL, 'We allocate resources carefully.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000019', 'initiative', 'noun', 'a new plan or action', 'B2', 0.83, '/ɪˈnɪʃətɪv/', NULL, 'The new initiative was successful.', CURRENT_TIMESTAMP, false),
    ('b0000000-0000-0000-0000-000000000020', 'feedback', 'noun', 'information about reactions to a product', 'B1', 0.90, '/ˈfiːdbæk/', NULL, 'We appreciate your feedback.', CURRENT_TIMESTAMP, false),
    
    -- Daily Conversation vocabulary (20 words)
    ('c0000000-0000-0000-0000-000000000001', 'appreciate', 'verb', 'to recognize the value of something', 'B1', 0.89, '/əˈpriːʃieɪt/', NULL, 'I really appreciate your help.', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000002', 'recommend', 'verb', 'to suggest as good or suitable', 'B1', 0.87, '/ˌrekəˈmend/', NULL, 'I recommend this restaurant.', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000003', 'hesitate', 'verb', 'to pause before doing something', 'B2', 0.76, '/ˈhezɪteɪt/', NULL, 'Do not hesitate to ask questions.', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000004', 'apologize', 'verb', 'to express regret for something', 'B1', 0.88, '/əˈpɒlədʒaɪz/', NULL, 'I apologize for the delay.', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000005', 'convenient', 'adjective', 'fitting well with needs or plans', 'B1', 0.86, '/kənˈviːniənt/', NULL, 'Is this time convenient for you?', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000006', 'experience', 'noun', 'knowledge gained through involvement', 'B1', 0.94, '/ɪkˈspɪəriəns/', NULL, 'It was an amazing experience.', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000007', 'opportunity', 'noun', 'a favorable time or situation', 'B1', 0.91, '/ˌɒpəˈtjuːnəti/', NULL, 'This is a great opportunity.', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000008', 'suggestion', 'noun', 'an idea put forward for consideration', 'B1', 0.85, '/səˈdʒestʃən/', NULL, 'Do you have any suggestions?', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000009', 'definitely', 'adverb', 'without any doubt', 'B1', 0.92, '/ˈdefɪnətli/', NULL, 'I will definitely be there.', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000010', 'actually', 'adverb', 'in fact, as a matter of fact', 'B1', 0.93, '/ˈæktʃuəli/', NULL, 'Actually, I changed my mind.', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000011', 'unfortunately', 'adverb', 'it is regrettable that', 'B1', 0.84, '/ʌnˈfɔːtʃənətli/', NULL, 'Unfortunately, I cannot come.', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000012', 'available', 'adjective', 'able to be used or obtained', 'B1', 0.90, '/əˈveɪləbl/', NULL, 'Is this seat available?', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000013', 'prefer', 'verb', 'to like one thing better than another', 'B1', 0.88, '/prɪˈfɜː/', NULL, 'I prefer coffee to tea.', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000014', 'mention', 'verb', 'to refer to something briefly', 'B1', 0.86, '/ˈmenʃən/', NULL, 'Did you mention the meeting?', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000015', 'assume', 'verb', 'to suppose something is true', 'B2', 0.82, '/əˈsjuːm/', NULL, 'I assume you agree with me.', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000016', 'confirm', 'verb', 'to establish the truth of something', 'B1', 0.87, '/kənˈfɜːm/', NULL, 'Can you confirm the reservation?', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000017', 'arrange', 'verb', 'to organize or make plans for', 'B1', 0.85, '/əˈreɪndʒ/', NULL, 'Let me arrange a meeting.', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000018', 'expect', 'verb', 'to regard something as likely to happen', 'B1', 0.91, '/ɪkˈspekt/', NULL, 'I expect to arrive by noon.', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000019', 'certainly', 'adverb', 'without doubt; definitely', 'B1', 0.89, '/ˈsɜːtnli/', NULL, 'I will certainly help you.', CURRENT_TIMESTAMP, false),
    ('c0000000-0000-0000-0000-000000000020', 'wonder', 'verb', 'to feel curious about something', 'B1', 0.87, '/ˈwʌndə/', NULL, 'I wonder what time it is.', CURRENT_TIMESTAMP, false),
    
    -- Academic Writing vocabulary (20 words)
    ('d0000000-0000-0000-0000-000000000001', 'argue', 'verb', 'to give reasons for or against', 'B2', 0.84, '/ˈɑːɡjuː/', NULL, 'The author argues that change is needed.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000002', 'assert', 'verb', 'to state a fact confidently', 'B2', 0.73, '/əˈsɜːt/', NULL, 'The study asserts a new finding.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000003', 'claim', 'verb', 'to state something as a fact', 'B2', 0.86, '/kleɪm/', NULL, 'The researchers claim success.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000004', 'contrast', 'verb', 'to compare in order to show differences', 'B2', 0.79, '/kənˈtrɑːst/', NULL, 'The results contrast with expectations.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000005', 'critique', 'verb', 'to evaluate critically', 'C1', 0.68, '/krɪˈtiːk/', NULL, 'The paper critiques previous research.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000006', 'emphasize', 'verb', 'to give special importance to', 'B2', 0.82, '/ˈemfəsaɪz/', NULL, 'I want to emphasize this point.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000007', 'illustrate', 'verb', 'to explain by using examples', 'B2', 0.78, '/ˈɪləstreɪt/', NULL, 'Let me illustrate with an example.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000008', 'imply', 'verb', 'to suggest without stating directly', 'B2', 0.77, '/ɪmˈplaɪ/', NULL, 'The data implies a correlation.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000009', 'maintain', 'verb', 'to state something strongly as true', 'B2', 0.81, '/meɪnˈteɪn/', NULL, 'The author maintains her position.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000010', 'outline', 'verb', 'to give a general description', 'B2', 0.80, '/ˈaʊtlaɪn/', NULL, 'The paper outlines the methodology.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000011', 'paraphrase', 'verb', 'to express meaning using different words', 'B2', 0.71, '/ˈpærəfreɪz/', NULL, 'Try to paraphrase the original text.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000012', 'reference', 'verb', 'to mention or refer to', 'B2', 0.85, '/ˈrefrəns/', NULL, 'The author references several studies.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000013', 'summarize', 'verb', 'to give a brief statement of main points', 'B2', 0.83, '/ˈsʌməraɪz/', NULL, 'Please summarize the findings.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000014', 'synthesize', 'verb', 'to combine elements into a coherent whole', 'C1', 0.67, '/ˈsɪnθəsaɪz/', NULL, 'The paper synthesizes various theories.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000015', 'validity', 'noun', 'the quality of being logically sound', 'B2', 0.74, '/vəˈlɪdəti/', NULL, 'The validity of the results is clear.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000016', 'bias', 'noun', 'prejudice in favor of or against something', 'B2', 0.76, '/ˈbaɪəs/', NULL, 'The study may have selection bias.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000017', 'coherent', 'adjective', 'logical and well-organized', 'B2', 0.75, '/kəʊˈhɪərənt/', NULL, 'The argument must be coherent.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000018', 'concise', 'adjective', 'brief but comprehensive', 'B2', 0.73, '/kənˈsaɪs/', NULL, 'Keep your writing concise.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000019', 'objective', 'adjective', 'not influenced by personal feelings', 'B2', 0.80, '/əbˈdʒektɪv/', NULL, 'Academic writing should be objective.', CURRENT_TIMESTAMP, false),
    ('d0000000-0000-0000-0000-000000000020', 'relevant', 'adjective', 'closely connected to the matter', 'B1', 0.88, '/ˈreləvənt/', NULL, 'Include only relevant information.', CURRENT_TIMESTAMP, false);

--changeset system:test-data-003 context:test,dev comment:Insert test curriculum (4 per goal = 16 total)

-- ============================================
-- TEST DATA: Curriculum (4 curricula per goal = 16 total)
-- ============================================
INSERT INTO public.curriculum (id, curriculum_name, goal_id, target_goals, description, is_active, created_at, deleted)
VALUES 
    -- IELTS Preparation curricula (Goal 1)
    ('c1100000-0000-0000-0000-000000000001', 'IELTS Band 6 Foundation', '11111111-1111-1111-1111-111111111111', 'IELTS 6.0', 'Foundation course for IELTS Band 6 preparation', true, CURRENT_TIMESTAMP, false),
    ('c1100000-0000-0000-0000-000000000002', 'IELTS Band 7 Advanced', '11111111-1111-1111-1111-111111111111', 'IELTS 7.0', 'Advanced course for IELTS Band 7+ preparation', true, CURRENT_TIMESTAMP, false),
    ('c1100000-0000-0000-0000-000000000003', 'IELTS Reading Mastery', '11111111-1111-1111-1111-111111111111', 'IELTS Reading', 'Specialized reading skills for IELTS', true, CURRENT_TIMESTAMP, false),
    ('c1100000-0000-0000-0000-000000000004', 'IELTS Writing Excellence', '11111111-1111-1111-1111-111111111111', 'IELTS Writing', 'Task 1 and Task 2 writing skills', true, CURRENT_TIMESTAMP, false),
    
    -- Business English curricula (Goal 2)
    ('c2200000-0000-0000-0000-000000000001', 'Business Communication Basics', '22222222-2222-2222-2222-222222222222', 'Business Basics', 'Essential business communication skills', true, CURRENT_TIMESTAMP, false),
    ('c2200000-0000-0000-0000-000000000002', 'Corporate Negotiations', '22222222-2222-2222-2222-222222222222', 'Negotiations', 'Professional negotiation vocabulary and techniques', true, CURRENT_TIMESTAMP, false),
    ('c2200000-0000-0000-0000-000000000003', 'Business Presentations', '22222222-2222-2222-2222-222222222222', 'Presentations', 'Effective business presentation skills', true, CURRENT_TIMESTAMP, false),
    ('c2200000-0000-0000-0000-000000000004', 'Email and Report Writing', '22222222-2222-2222-2222-222222222222', 'Written Business', 'Professional email and report writing', true, CURRENT_TIMESTAMP, false),
    
    -- Daily Conversation curricula (Goal 3)
    ('c3300000-0000-0000-0000-000000000001', 'Social English Basics', '33333333-3333-3333-3333-333333333333', 'Social Basics', 'Everyday social conversation skills', true, CURRENT_TIMESTAMP, false),
    ('c3300000-0000-0000-0000-000000000002', 'Travel English', '33333333-3333-3333-3333-333333333333', 'Travel', 'Essential English for travelers', true, CURRENT_TIMESTAMP, false),
    ('c3300000-0000-0000-0000-000000000003', 'Shopping and Services', '33333333-3333-3333-3333-333333333333', 'Shopping', 'Vocabulary for shopping and services', true, CURRENT_TIMESTAMP, false),
    ('c3300000-0000-0000-0000-000000000004', 'Entertainment and Hobbies', '33333333-3333-3333-3333-333333333333', 'Entertainment', 'Discussing entertainment and hobbies', true, CURRENT_TIMESTAMP, false),
    
    -- Academic Writing curricula (Goal 4)
    ('c4400000-0000-0000-0000-000000000001', 'Essay Structure Fundamentals', '44444444-4444-4444-4444-444444444444', 'Essay Basics', 'Basic academic essay writing structure', true, CURRENT_TIMESTAMP, false),
    ('c4400000-0000-0000-0000-000000000002', 'Research Paper Writing', '44444444-4444-4444-4444-444444444444', 'Research Papers', 'Writing effective research papers', true, CURRENT_TIMESTAMP, false),
    ('c4400000-0000-0000-0000-000000000003', 'Critical Analysis Skills', '44444444-4444-4444-4444-444444444444', 'Critical Analysis', 'Developing critical analysis in writing', true, CURRENT_TIMESTAMP, false),
    ('c4400000-0000-0000-0000-000000000004', 'Citation and Referencing', '44444444-4444-4444-4444-444444444444', 'Citations', 'Proper citation and referencing techniques', true, CURRENT_TIMESTAMP, false);

--changeset system:test-data-004 context:test,dev comment:Insert test curriculum units (4 per curriculum = 64 total)

-- ============================================
-- TEST DATA: Curriculum Units (4 units per curriculum = 64 total)
-- ============================================
INSERT INTO public.curriculum_units (id, curriculum_id, unit_name, required_proficiency_level, default_order, base_keywords, description, is_active, created_at, deleted)
VALUES 
    -- IELTS Band 6 Foundation units (c1100000-...-001)
    ('a1110001-0000-0000-0000-000000000001', 'c1100000-0000-0000-0000-000000000001', 'Core Academic Vocabulary 1', 'B1', 1, ARRAY['academic', 'vocabulary', 'basic'], 'Foundation academic vocabulary set 1', true, CURRENT_TIMESTAMP, false),
    ('a1110001-0000-0000-0000-000000000002', 'c1100000-0000-0000-0000-000000000001', 'Core Academic Vocabulary 2', 'B1', 2, ARRAY['academic', 'vocabulary', 'intermediate'], 'Foundation academic vocabulary set 2', true, CURRENT_TIMESTAMP, false),
    ('a1110001-0000-0000-0000-000000000003', 'c1100000-0000-0000-0000-000000000001', 'Reading Skills Basics', 'B1', 3, ARRAY['reading', 'comprehension', 'basics'], 'Basic reading comprehension skills', true, CURRENT_TIMESTAMP, false),
    ('a1110001-0000-0000-0000-000000000004', 'c1100000-0000-0000-0000-000000000001', 'Writing Skills Foundation', 'B1', 4, ARRAY['writing', 'skills', 'foundation'], 'Foundation writing skills for IELTS', true, CURRENT_TIMESTAMP, false),
    
    -- IELTS Band 7 Advanced units (c1100000-...-002)
    ('a1110002-0000-0000-0000-000000000001', 'c1100000-0000-0000-0000-000000000002', 'Advanced Academic Vocabulary', 'B2', 1, ARRAY['academic', 'advanced', 'vocabulary'], 'Advanced academic vocabulary for Band 7', true, CURRENT_TIMESTAMP, false),
    ('a1110002-0000-0000-0000-000000000002', 'c1100000-0000-0000-0000-000000000002', 'Complex Sentence Structures', 'B2', 2, ARRAY['grammar', 'complex', 'sentences'], 'Complex grammatical structures', true, CURRENT_TIMESTAMP, false),
    ('a1110002-0000-0000-0000-000000000003', 'c1100000-0000-0000-0000-000000000002', 'Inference and Analysis', 'B2', 3, ARRAY['inference', 'analysis', 'critical'], 'Critical reading and inference skills', true, CURRENT_TIMESTAMP, false),
    ('a1110002-0000-0000-0000-000000000004', 'c1100000-0000-0000-0000-000000000002', 'Advanced Writing Techniques', 'B2', 4, ARRAY['writing', 'advanced', 'techniques'], 'Advanced writing strategies', true, CURRENT_TIMESTAMP, false),
    
    -- IELTS Reading Mastery units (c1100000-...-003)
    ('a1110003-0000-0000-0000-000000000001', 'c1100000-0000-0000-0000-000000000003', 'Skimming and Scanning', 'B2', 1, ARRAY['skimming', 'scanning', 'speed'], 'Speed reading techniques', true, CURRENT_TIMESTAMP, false),
    ('a1110003-0000-0000-0000-000000000002', 'c1100000-0000-0000-0000-000000000003', 'Matching Headings', 'B2', 2, ARRAY['matching', 'headings', 'paragraphs'], 'Matching headings question type', true, CURRENT_TIMESTAMP, false),
    ('a1110003-0000-0000-0000-000000000003', 'c1100000-0000-0000-0000-000000000003', 'True False Not Given', 'B2', 3, ARRAY['true', 'false', 'not given'], 'T/F/NG question strategies', true, CURRENT_TIMESTAMP, false),
    ('a1110003-0000-0000-0000-000000000004', 'c1100000-0000-0000-0000-000000000003', 'Summary Completion', 'B2', 4, ARRAY['summary', 'completion', 'gaps'], 'Summary completion techniques', true, CURRENT_TIMESTAMP, false),
    
    -- IELTS Writing Excellence units (c1100000-...-004)
    ('a1110004-0000-0000-0000-000000000001', 'c1100000-0000-0000-0000-000000000004', 'Task 1 Data Description', 'B2', 1, ARRAY['task1', 'data', 'description'], 'Describing charts and graphs', true, CURRENT_TIMESTAMP, false),
    ('a1110004-0000-0000-0000-000000000002', 'c1100000-0000-0000-0000-000000000004', 'Task 1 Process Description', 'B2', 2, ARRAY['task1', 'process', 'diagram'], 'Describing processes and diagrams', true, CURRENT_TIMESTAMP, false),
    ('a1110004-0000-0000-0000-000000000003', 'c1100000-0000-0000-0000-000000000004', 'Task 2 Essay Structure', 'B2', 3, ARRAY['task2', 'essay', 'structure'], 'IELTS essay structure and planning', true, CURRENT_TIMESTAMP, false),
    ('a1110004-0000-0000-0000-000000000004', 'c1100000-0000-0000-0000-000000000004', 'Task 2 Argument Development', 'B2', 4, ARRAY['task2', 'argument', 'development'], 'Developing arguments effectively', true, CURRENT_TIMESTAMP, false),
    
    -- Business Communication Basics units (c2200000-...-001)
    ('a2210001-0000-0000-0000-000000000001', 'c2200000-0000-0000-0000-000000000001', 'Office Vocabulary', 'B1', 1, ARRAY['office', 'workplace', 'basics'], 'Essential office vocabulary', true, CURRENT_TIMESTAMP, false),
    ('a2210001-0000-0000-0000-000000000002', 'c2200000-0000-0000-0000-000000000001', 'Meeting Terminology', 'B1', 2, ARRAY['meeting', 'terminology', 'business'], 'Vocabulary for business meetings', true, CURRENT_TIMESTAMP, false),
    ('a2210001-0000-0000-0000-000000000003', 'c2200000-0000-0000-0000-000000000001', 'Phone Communication', 'B1', 3, ARRAY['phone', 'call', 'communication'], 'Professional phone communication', true, CURRENT_TIMESTAMP, false),
    ('a2210001-0000-0000-0000-000000000004', 'c2200000-0000-0000-0000-000000000001', 'Email Basics', 'B1', 4, ARRAY['email', 'basics', 'professional'], 'Basic professional email writing', true, CURRENT_TIMESTAMP, false),
    
    -- Corporate Negotiations units (c2200000-...-002)
    ('a2210002-0000-0000-0000-000000000001', 'c2200000-0000-0000-0000-000000000002', 'Negotiation Vocabulary', 'B2', 1, ARRAY['negotiation', 'vocabulary', 'deals'], 'Core negotiation vocabulary', true, CURRENT_TIMESTAMP, false),
    ('a2210002-0000-0000-0000-000000000002', 'c2200000-0000-0000-0000-000000000002', 'Persuasion Techniques', 'B2', 2, ARRAY['persuasion', 'influence', 'techniques'], 'Language for persuasion', true, CURRENT_TIMESTAMP, false),
    ('a2210002-0000-0000-0000-000000000003', 'c2200000-0000-0000-0000-000000000002', 'Handling Objections', 'B2', 3, ARRAY['objections', 'handling', 'responses'], 'Responding to objections', true, CURRENT_TIMESTAMP, false),
    ('a2210002-0000-0000-0000-000000000004', 'c2200000-0000-0000-0000-000000000002', 'Closing Deals', 'B2', 4, ARRAY['closing', 'deals', 'agreements'], 'Closing negotiations successfully', true, CURRENT_TIMESTAMP, false),
    
    -- Business Presentations units (c2200000-...-003)
    ('a2210003-0000-0000-0000-000000000001', 'c2200000-0000-0000-0000-000000000003', 'Opening Presentations', 'B2', 1, ARRAY['opening', 'introduction', 'presentation'], 'Strong presentation openings', true, CURRENT_TIMESTAMP, false),
    ('a2210003-0000-0000-0000-000000000002', 'c2200000-0000-0000-0000-000000000003', 'Visual Aids Vocabulary', 'B2', 2, ARRAY['visual', 'aids', 'charts'], 'Describing visual aids', true, CURRENT_TIMESTAMP, false),
    ('a2210003-0000-0000-0000-000000000003', 'c2200000-0000-0000-0000-000000000003', 'Transitional Phrases', 'B2', 3, ARRAY['transitions', 'phrases', 'linking'], 'Smooth transitions in presentations', true, CURRENT_TIMESTAMP, false),
    ('a2210003-0000-0000-0000-000000000004', 'c2200000-0000-0000-0000-000000000003', 'QA Session Handling', 'B2', 4, ARRAY['questions', 'answers', 'handling'], 'Managing Q&A sessions', true, CURRENT_TIMESTAMP, false),
    
    -- Email and Report Writing units (c2200000-...-004)
    ('a2210004-0000-0000-0000-000000000001', 'c2200000-0000-0000-0000-000000000004', 'Formal Email Structures', 'B2', 1, ARRAY['formal', 'email', 'structure'], 'Formal email writing patterns', true, CURRENT_TIMESTAMP, false),
    ('a2210004-0000-0000-0000-000000000002', 'c2200000-0000-0000-0000-000000000004', 'Report Introduction', 'B2', 2, ARRAY['report', 'introduction', 'executive'], 'Writing report introductions', true, CURRENT_TIMESTAMP, false),
    ('a2210004-0000-0000-0000-000000000003', 'c2200000-0000-0000-0000-000000000004', 'Data Analysis Reports', 'B2', 3, ARRAY['data', 'analysis', 'reporting'], 'Presenting data in reports', true, CURRENT_TIMESTAMP, false),
    ('a2210004-0000-0000-0000-000000000004', 'c2200000-0000-0000-0000-000000000004', 'Recommendations Writing', 'B2', 4, ARRAY['recommendations', 'conclusions', 'action'], 'Writing effective recommendations', true, CURRENT_TIMESTAMP, false),
    
    -- Social English Basics units (c3300000-...-001)
    ('a3310001-0000-0000-0000-000000000001', 'c3300000-0000-0000-0000-000000000001', 'Greetings and Introductions', 'A2', 1, ARRAY['greetings', 'introductions', 'social'], 'Common greetings and introductions', true, CURRENT_TIMESTAMP, false),
    ('a3310001-0000-0000-0000-000000000002', 'c3300000-0000-0000-0000-000000000001', 'Small Talk Topics', 'B1', 2, ARRAY['small talk', 'conversation', 'topics'], 'Making small talk', true, CURRENT_TIMESTAMP, false),
    ('a3310001-0000-0000-0000-000000000003', 'c3300000-0000-0000-0000-000000000001', 'Expressing Opinions', 'B1', 3, ARRAY['opinions', 'expressing', 'views'], 'Sharing opinions politely', true, CURRENT_TIMESTAMP, false),
    ('a3310001-0000-0000-0000-000000000004', 'c3300000-0000-0000-0000-000000000001', 'Making Plans', 'B1', 4, ARRAY['plans', 'arrangements', 'invitations'], 'Making and discussing plans', true, CURRENT_TIMESTAMP, false),
    
    -- Travel English units (c3300000-...-002)
    ('a3310002-0000-0000-0000-000000000001', 'c3300000-0000-0000-0000-000000000002', 'Airport and Flight', 'B1', 1, ARRAY['airport', 'flight', 'travel'], 'Airport and flight vocabulary', true, CURRENT_TIMESTAMP, false),
    ('a3310002-0000-0000-0000-000000000002', 'c3300000-0000-0000-0000-000000000002', 'Hotel and Accommodation', 'B1', 2, ARRAY['hotel', 'accommodation', 'booking'], 'Hotel and accommodation terms', true, CURRENT_TIMESTAMP, false),
    ('a3310002-0000-0000-0000-000000000003', 'c3300000-0000-0000-0000-000000000002', 'Directions and Transportation', 'B1', 3, ARRAY['directions', 'transport', 'navigation'], 'Asking for directions', true, CURRENT_TIMESTAMP, false),
    ('a3310002-0000-0000-0000-000000000004', 'c3300000-0000-0000-0000-000000000002', 'Tourist Attractions', 'B1', 4, ARRAY['tourist', 'attractions', 'sightseeing'], 'Discussing tourist attractions', true, CURRENT_TIMESTAMP, false),
    
    -- Shopping and Services units (c3300000-...-003)
    ('a3310003-0000-0000-0000-000000000001', 'c3300000-0000-0000-0000-000000000003', 'Shopping Vocabulary', 'A2', 1, ARRAY['shopping', 'buying', 'stores'], 'Basic shopping vocabulary', true, CURRENT_TIMESTAMP, false),
    ('a3310003-0000-0000-0000-000000000002', 'c3300000-0000-0000-0000-000000000003', 'Bargaining and Prices', 'B1', 2, ARRAY['bargaining', 'prices', 'discounts'], 'Discussing prices and bargaining', true, CURRENT_TIMESTAMP, false),
    ('a3310003-0000-0000-0000-000000000003', 'c3300000-0000-0000-0000-000000000003', 'Restaurant Dining', 'B1', 3, ARRAY['restaurant', 'dining', 'food'], 'Ordering at restaurants', true, CURRENT_TIMESTAMP, false),
    ('a3310003-0000-0000-0000-000000000004', 'c3300000-0000-0000-0000-000000000003', 'Service Complaints', 'B1', 4, ARRAY['complaints', 'service', 'problems'], 'Making complaints politely', true, CURRENT_TIMESTAMP, false),
    
    -- Entertainment and Hobbies units (c3300000-...-004)
    ('a3310004-0000-0000-0000-000000000001', 'c3300000-0000-0000-0000-000000000004', 'Movies and TV', 'B1', 1, ARRAY['movies', 'TV', 'entertainment'], 'Discussing films and shows', true, CURRENT_TIMESTAMP, false),
    ('a3310004-0000-0000-0000-000000000002', 'c3300000-0000-0000-0000-000000000004', 'Sports and Fitness', 'B1', 2, ARRAY['sports', 'fitness', 'exercise'], 'Sports and fitness vocabulary', true, CURRENT_TIMESTAMP, false),
    ('a3310004-0000-0000-0000-000000000003', 'c3300000-0000-0000-0000-000000000004', 'Music and Arts', 'B1', 3, ARRAY['music', 'arts', 'culture'], 'Discussing music and arts', true, CURRENT_TIMESTAMP, false),
    ('a3310004-0000-0000-0000-000000000004', 'c3300000-0000-0000-0000-000000000004', 'Hobbies and Interests', 'B1', 4, ARRAY['hobbies', 'interests', 'pastimes'], 'Talking about hobbies', true, CURRENT_TIMESTAMP, false),
    
    -- Essay Structure Fundamentals units (c4400000-...-001)
    ('a4410001-0000-0000-0000-000000000001', 'c4400000-0000-0000-0000-000000000001', 'Thesis Statement', 'B2', 1, ARRAY['thesis', 'statement', 'main idea'], 'Writing effective thesis statements', true, CURRENT_TIMESTAMP, false),
    ('a4410001-0000-0000-0000-000000000002', 'c4400000-0000-0000-0000-000000000001', 'Introduction Paragraphs', 'B2', 2, ARRAY['introduction', 'opening', 'hook'], 'Crafting strong introductions', true, CURRENT_TIMESTAMP, false),
    ('a4410001-0000-0000-0000-000000000003', 'c4400000-0000-0000-0000-000000000001', 'Body Paragraph Structure', 'B2', 3, ARRAY['body', 'paragraph', 'topic sentence'], 'Structuring body paragraphs', true, CURRENT_TIMESTAMP, false),
    ('a4410001-0000-0000-0000-000000000004', 'c4400000-0000-0000-0000-000000000001', 'Conclusion Writing', 'B2', 4, ARRAY['conclusion', 'summary', 'closing'], 'Writing effective conclusions', true, CURRENT_TIMESTAMP, false),
    
    -- Research Paper Writing units (c4400000-...-002)
    ('a4410002-0000-0000-0000-000000000001', 'c4400000-0000-0000-0000-000000000002', 'Literature Review', 'C1', 1, ARRAY['literature', 'review', 'sources'], 'Writing literature reviews', true, CURRENT_TIMESTAMP, false),
    ('a4410002-0000-0000-0000-000000000002', 'c4400000-0000-0000-0000-000000000002', 'Methodology Section', 'C1', 2, ARRAY['methodology', 'methods', 'research'], 'Describing research methodology', true, CURRENT_TIMESTAMP, false),
    ('a4410002-0000-0000-0000-000000000003', 'c4400000-0000-0000-0000-000000000002', 'Results Presentation', 'C1', 3, ARRAY['results', 'findings', 'data'], 'Presenting research results', true, CURRENT_TIMESTAMP, false),
    ('a4410002-0000-0000-0000-000000000004', 'c4400000-0000-0000-0000-000000000002', 'Discussion Section', 'C1', 4, ARRAY['discussion', 'interpretation', 'implications'], 'Writing discussion sections', true, CURRENT_TIMESTAMP, false),
    
    -- Critical Analysis Skills units (c4400000-...-003)
    ('a4410003-0000-0000-0000-000000000001', 'c4400000-0000-0000-0000-000000000003', 'Analyzing Arguments', 'B2', 1, ARRAY['analyzing', 'arguments', 'logic'], 'Analyzing logical arguments', true, CURRENT_TIMESTAMP, false),
    ('a4410003-0000-0000-0000-000000000002', 'c4400000-0000-0000-0000-000000000003', 'Identifying Bias', 'B2', 2, ARRAY['bias', 'identifying', 'objectivity'], 'Recognizing bias in texts', true, CURRENT_TIMESTAMP, false),
    ('a4410003-0000-0000-0000-000000000003', 'c4400000-0000-0000-0000-000000000003', 'Evaluating Sources', 'B2', 3, ARRAY['evaluating', 'sources', 'credibility'], 'Assessing source credibility', true, CURRENT_TIMESTAMP, false),
    ('a4410003-0000-0000-0000-000000000004', 'c4400000-0000-0000-0000-000000000003', 'Synthesizing Information', 'C1', 4, ARRAY['synthesizing', 'combining', 'integration'], 'Synthesizing multiple sources', true, CURRENT_TIMESTAMP, false),
    
    -- Citation and Referencing units (c4400000-...-004)
    ('a4410004-0000-0000-0000-000000000001', 'c4400000-0000-0000-0000-000000000004', 'In-text Citations', 'B2', 1, ARRAY['in-text', 'citations', 'quoting'], 'Using in-text citations', true, CURRENT_TIMESTAMP, false),
    ('a4410004-0000-0000-0000-000000000002', 'c4400000-0000-0000-0000-000000000004', 'Reference List Format', 'B2', 2, ARRAY['reference', 'list', 'bibliography'], 'Formatting reference lists', true, CURRENT_TIMESTAMP, false),
    ('a4410004-0000-0000-0000-000000000003', 'c4400000-0000-0000-0000-000000000004', 'Paraphrasing Skills', 'B2', 3, ARRAY['paraphrasing', 'rewording', 'original'], 'Effective paraphrasing techniques', true, CURRENT_TIMESTAMP, false),
    ('a4410004-0000-0000-0000-000000000004', 'c4400000-0000-0000-0000-000000000004', 'Avoiding Plagiarism', 'B2', 4, ARRAY['plagiarism', 'avoiding', 'integrity'], 'Academic integrity and plagiarism', true, CURRENT_TIMESTAMP, false);

--changeset system:test-data-005 context:test,dev comment:Insert word context templates (sample - 10 per unit for key units)

-- ============================================
-- Word Context Templates: 10 words per sample unit
-- ============================================
INSERT INTO public.word_context_templates (id, unit_id, word_id, specific_meaning, context_sentence, context_translation, ai_reasoning, source_type, created_at, deleted)
VALUES 
    -- Core Academic Vocabulary 1 (a1110001-...-001) - 10 word contexts
    ('bc110001-0001-0000-0000-000000000001', 'a1110001-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'to provide space for', 'The lecture hall can accommodate 200 students.', 'Hội trường có thể chứa 200 sinh viên.', 'Academic context', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc110001-0001-0000-0000-000000000002', 'a1110001-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000002', 'thorough and complete', 'Submit a comprehensive report on your research.', 'Nộp một báo cáo toàn diện về nghiên cứu của bạn.', 'Academic writing', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc110001-0001-0000-0000-000000000003', 'a1110001-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000008', 'important enough to matter', 'There was a significant increase in test scores.', 'Có một sự gia tăng đáng kể trong điểm thi.', 'Data description', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc110001-0001-0000-0000-000000000004', 'a1110001-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000009', 'to examine in detail', 'Analyze the graph and describe the main trends.', 'Phân tích biểu đồ và mô tả các xu hướng chính.', 'IELTS Task 1', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc110001-0001-0000-0000-000000000005', 'a1110001-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000010', 'to show clearly', 'The chart demonstrates a steady growth pattern.', 'Biểu đồ cho thấy một mô hình tăng trưởng ổn định.', 'Data presentation', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc110001-0001-0000-0000-000000000006', 'a1110001-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000012', 'facts supporting a claim', 'Provide evidence to support your argument.', 'Cung cấp bằng chứng để hỗ trợ lập luận của bạn.', 'Essay writing', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc110001-0001-0000-0000-000000000007', 'a1110001-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000017', 'a set of ideas explaining something', 'The theory suggests that practice improves performance.', 'Lý thuyết cho rằng thực hành cải thiện hiệu suất.', 'Academic discussion', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc110001-0001-0000-0000-000000000008', 'a1110001-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000018', 'to help achieve something', 'Several factors contribute to academic success.', 'Nhiều yếu tố góp phần vào thành công học tập.', 'Cause and effect', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc110001-0001-0000-0000-000000000009', 'a1110001-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000019', 'to assess value', 'Evaluate the effectiveness of different study methods.', 'Đánh giá hiệu quả của các phương pháp học khác nhau.', 'Critical thinking', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc110001-0001-0000-0000-000000000010', 'a1110001-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000020', 'basic and essential', 'Understanding vocabulary is fundamental to reading.', 'Hiểu từ vựng là nền tảng cho việc đọc.', 'Learning principle', 'SYSTEM', CURRENT_TIMESTAMP, false),

    -- Negotiation Vocabulary (a2210002-...-001) - 10 word contexts
    ('bc221002-0001-0000-0000-000000000001', 'a2210002-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'to discuss terms', 'We need to negotiate the contract terms.', 'Chúng ta cần đàm phán các điều khoản hợp đồng.', 'Business negotiation', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc221002-0001-0000-0000-000000000002', 'a2210002-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002', 'interested party', 'All stakeholders must agree to the proposal.', 'Tất cả các bên liên quan phải đồng ý với đề xuất.', 'Corporate decisions', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc221002-0001-0000-0000-000000000003', 'a2210002-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000008', 'a formal suggestion', 'Submit your proposal by Friday.', 'Nộp đề xuất của bạn trước thứ Sáu.', 'Business process', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc221002-0001-0000-0000-000000000004', 'a2210002-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000009', 'a goal or target', 'Our main objective is to reach an agreement.', 'Mục tiêu chính của chúng tôi là đạt được thỏa thuận.', 'Goal setting', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc221002-0001-0000-0000-000000000005', 'a2210002-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000004', 'a time limit', 'The deadline for the offer is next week.', 'Hạn chót cho đề nghị là tuần tới.', 'Time pressure', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc221002-0001-0000-0000-000000000006', 'a2210002-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000005', 'to work together', 'Both parties agreed to collaborate on the project.', 'Cả hai bên đồng ý hợp tác trong dự án.', 'Partnership', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc221002-0001-0000-0000-000000000007', 'a2210002-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000013', 'to use advantageously', 'We can leverage our market position.', 'Chúng ta có thể tận dụng vị thế thị trường của mình.', 'Strategic advantage', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc221002-0001-0000-0000-000000000008', 'a2210002-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000015', 'a standard for comparison', 'Use industry benchmarks in your analysis.', 'Sử dụng tiêu chuẩn ngành trong phân tích của bạn.', 'Market analysis', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc221002-0001-0000-0000-000000000009', 'a2210002-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000018', 'to distribute resources', 'Allocate budget for the new initiative.', 'Phân bổ ngân sách cho sáng kiến mới.', 'Resource management', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc221002-0001-0000-0000-000000000010', 'a2210002-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000019', 'a new project', 'The initiative received full support.', 'Sáng kiến nhận được sự hỗ trợ đầy đủ.', 'Business planning', 'SYSTEM', CURRENT_TIMESTAMP, false),

    -- Greetings and Introductions (a3310001-...-001) - 10 word contexts
    ('bc331001-0001-0000-0000-000000000001', 'a3310001-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001', 'to be grateful', 'I appreciate your help with this.', 'Tôi đánh giá cao sự giúp đỡ của bạn.', 'Expressing gratitude', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc331001-0001-0000-0000-000000000002', 'a3310001-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000004', 'to say sorry', 'I apologize for being late.', 'Tôi xin lỗi vì đến muộn.', 'Polite expressions', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc331001-0001-0000-0000-000000000003', 'a3310001-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000006', 'event or knowledge gained', 'It was a pleasure meeting you.', 'Thật vui được gặp bạn.', 'Social introduction', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc331001-0001-0000-0000-000000000004', 'a3310001-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000019', 'without doubt', 'I will certainly remember your kindness.', 'Tôi chắc chắn sẽ nhớ lòng tốt của bạn.', 'Emphasizing commitment', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc331001-0001-0000-0000-000000000005', 'a3310001-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000014', 'to refer to briefly', 'I should mention that I am new here.', 'Tôi nên đề cập rằng tôi mới ở đây.', 'Self-introduction', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc331001-0001-0000-0000-000000000006', 'a3310001-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000009', 'for sure', 'I will definitely call you tomorrow.', 'Tôi chắc chắn sẽ gọi cho bạn ngày mai.', 'Making promises', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc331001-0001-0000-0000-000000000007', 'a3310001-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000010', 'in fact', 'Actually, I am from Vietnam.', 'Thực ra, tôi đến từ Việt Nam.', 'Sharing information', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc331001-0001-0000-0000-000000000008', 'a3310001-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000007', 'a favorable chance', 'This is a great opportunity to meet you.', 'Đây là cơ hội tuyệt vời để gặp bạn.', 'Social context', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc331001-0001-0000-0000-000000000009', 'a3310001-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000018', 'to anticipate', 'I expect we will meet again soon.', 'Tôi hy vọng chúng ta sẽ gặp lại sớm.', 'Future plans', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc331001-0001-0000-0000-000000000010', 'a3310001-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000020', 'to be curious', 'I wonder where you are from.', 'Tôi tự hỏi bạn đến từ đâu.', 'Asking questions politely', 'SYSTEM', CURRENT_TIMESTAMP, false),

    -- Thesis Statement (a4410001-...-001) - 10 word contexts
    ('bc441001-0001-0000-0000-000000000001', 'a4410001-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000001', 'to give reasons', 'This essay argues that education is key.', 'Bài luận này lập luận rằng giáo dục là chìa khóa.', 'Thesis statement', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc441001-0001-0000-0000-000000000002', 'a4410001-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000002', 'to state confidently', 'The study asserts a direct correlation.', 'Nghiên cứu khẳng định một mối tương quan trực tiếp.', 'Making claims', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc441001-0001-0000-0000-000000000003', 'a4410001-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000003', 'to state as fact', 'Researchers claim significant results.', 'Các nhà nghiên cứu tuyên bố kết quả đáng kể.', 'Reporting findings', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc441001-0001-0000-0000-000000000004', 'a4410001-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000006', 'to stress importance', 'I want to emphasize this key point.', 'Tôi muốn nhấn mạnh điểm quan trọng này.', 'Highlighting main idea', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc441001-0001-0000-0000-000000000005', 'a4410001-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000009', 'to hold a position', 'The author maintains her argument throughout.', 'Tác giả duy trì lập luận của mình xuyên suốt.', 'Consistent argumentation', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc441001-0001-0000-0000-000000000006', 'a4410001-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000010', 'to give an overview', 'The paper outlines three main points.', 'Bài viết phác thảo ba điểm chính.', 'Essay structure', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc441001-0001-0000-0000-000000000007', 'a4410001-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000017', 'logical and organized', 'Present a coherent argument in your thesis.', 'Trình bày một lập luận mạch lạc trong luận điểm của bạn.', 'Writing quality', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc441001-0001-0000-0000-000000000008', 'a4410001-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000018', 'brief but complete', 'Keep your thesis statement concise.', 'Giữ câu luận điểm của bạn ngắn gọn.', 'Writing style', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc441001-0001-0000-0000-000000000009', 'a4410001-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000019', 'unbiased', 'Academic writing should be objective.', 'Văn bản học thuật nên khách quan.', 'Academic standards', 'SYSTEM', CURRENT_TIMESTAMP, false),
    ('bc441001-0001-0000-0000-000000000010', 'a4410001-0000-0000-0000-000000000001', 'd0000000-0000-0000-0000-000000000020', 'connected to the topic', 'Include only relevant information.', 'Chỉ bao gồm thông tin liên quan.', 'Content selection', 'SYSTEM', CURRENT_TIMESTAMP, false);

--changeset system:test-data-006 context:test,dev comment:Insert lesson templates (2 per sample unit)

-- ============================================
-- Lesson Templates: 2 lessons per sample unit
-- ============================================
INSERT INTO public.lesson_templates (id, unit_id, lesson_name, lesson_type, sequence_order, created_at, deleted)
VALUES 
    ('de110001-0001-0000-0000-000000000001', 'a1110001-0000-0000-0000-000000000001', 'Academic Words Part 1', 'STANDARD', 1, CURRENT_TIMESTAMP, false),
    ('de110001-0001-0000-0000-000000000002', 'a1110001-0000-0000-0000-000000000001', 'Academic Words Part 2', 'STANDARD', 2, CURRENT_TIMESTAMP, false),
    ('de221002-0001-0000-0000-000000000001', 'a2210002-0000-0000-0000-000000000001', 'Negotiation Terms Part 1', 'STANDARD', 1, CURRENT_TIMESTAMP, false),
    ('de221002-0001-0000-0000-000000000002', 'a2210002-0000-0000-0000-000000000001', 'Negotiation Terms Part 2', 'STANDARD', 2, CURRENT_TIMESTAMP, false),
    ('de331001-0001-0000-0000-000000000001', 'a3310001-0000-0000-0000-000000000001', 'Formal Greetings', 'STANDARD', 1, CURRENT_TIMESTAMP, false),
    ('de331001-0001-0000-0000-000000000002', 'a3310001-0000-0000-0000-000000000001', 'Casual Introductions', 'STANDARD', 2, CURRENT_TIMESTAMP, false),
    ('de441001-0001-0000-0000-000000000001', 'a4410001-0000-0000-0000-000000000001', 'Crafting Thesis Statements', 'STANDARD', 1, CURRENT_TIMESTAMP, false),
    ('de441001-0001-0000-0000-000000000002', 'a4410001-0000-0000-0000-000000000001', 'Thesis Practice', 'STANDARD', 2, CURRENT_TIMESTAMP, false);

--changeset system:test-data-007 context:test,dev comment:Link lessons to word contexts

-- ============================================
-- Lesson Word Contexts (5 words per lesson)
-- ============================================
INSERT INTO public.lesson_template_word_contexts (lesson_template_id, word_context_template_id)
VALUES 
    ('de110001-0001-0000-0000-000000000001', 'bc110001-0001-0000-0000-000000000001'),
    ('de110001-0001-0000-0000-000000000001', 'bc110001-0001-0000-0000-000000000002'),
    ('de110001-0001-0000-0000-000000000001', 'bc110001-0001-0000-0000-000000000003'),
    ('de110001-0001-0000-0000-000000000001', 'bc110001-0001-0000-0000-000000000004'),
    ('de110001-0001-0000-0000-000000000001', 'bc110001-0001-0000-0000-000000000005'),
    ('de110001-0001-0000-0000-000000000002', 'bc110001-0001-0000-0000-000000000006'),
    ('de110001-0001-0000-0000-000000000002', 'bc110001-0001-0000-0000-000000000007'),
    ('de110001-0001-0000-0000-000000000002', 'bc110001-0001-0000-0000-000000000008'),
    ('de110001-0001-0000-0000-000000000002', 'bc110001-0001-0000-0000-000000000009'),
    ('de110001-0001-0000-0000-000000000002', 'bc110001-0001-0000-0000-000000000010'),
    ('de221002-0001-0000-0000-000000000001', 'bc221002-0001-0000-0000-000000000001'),
    ('de221002-0001-0000-0000-000000000001', 'bc221002-0001-0000-0000-000000000002'),
    ('de221002-0001-0000-0000-000000000001', 'bc221002-0001-0000-0000-000000000003'),
    ('de221002-0001-0000-0000-000000000001', 'bc221002-0001-0000-0000-000000000004'),
    ('de221002-0001-0000-0000-000000000001', 'bc221002-0001-0000-0000-000000000005'),
    ('de221002-0001-0000-0000-000000000002', 'bc221002-0001-0000-0000-000000000006'),
    ('de221002-0001-0000-0000-000000000002', 'bc221002-0001-0000-0000-000000000007'),
    ('de221002-0001-0000-0000-000000000002', 'bc221002-0001-0000-0000-000000000008'),
    ('de221002-0001-0000-0000-000000000002', 'bc221002-0001-0000-0000-000000000009'),
    ('de221002-0001-0000-0000-000000000002', 'bc221002-0001-0000-0000-000000000010'),
    ('de331001-0001-0000-0000-000000000001', 'bc331001-0001-0000-0000-000000000001'),
    ('de331001-0001-0000-0000-000000000001', 'bc331001-0001-0000-0000-000000000002'),
    ('de331001-0001-0000-0000-000000000001', 'bc331001-0001-0000-0000-000000000003'),
    ('de331001-0001-0000-0000-000000000001', 'bc331001-0001-0000-0000-000000000004'),
    ('de331001-0001-0000-0000-000000000001', 'bc331001-0001-0000-0000-000000000005'),
    ('de331001-0001-0000-0000-000000000002', 'bc331001-0001-0000-0000-000000000006'),
    ('de331001-0001-0000-0000-000000000002', 'bc331001-0001-0000-0000-000000000007'),
    ('de331001-0001-0000-0000-000000000002', 'bc331001-0001-0000-0000-000000000008'),
    ('de331001-0001-0000-0000-000000000002', 'bc331001-0001-0000-0000-000000000009'),
    ('de331001-0001-0000-0000-000000000002', 'bc331001-0001-0000-0000-000000000010'),
    ('de441001-0001-0000-0000-000000000001', 'bc441001-0001-0000-0000-000000000001'),
    ('de441001-0001-0000-0000-000000000001', 'bc441001-0001-0000-0000-000000000002'),
    ('de441001-0001-0000-0000-000000000001', 'bc441001-0001-0000-0000-000000000003'),
    ('de441001-0001-0000-0000-000000000001', 'bc441001-0001-0000-0000-000000000004'),
    ('de441001-0001-0000-0000-000000000001', 'bc441001-0001-0000-0000-000000000005'),
    ('de441001-0001-0000-0000-000000000002', 'bc441001-0001-0000-0000-000000000006'),
    ('de441001-0001-0000-0000-000000000002', 'bc441001-0001-0000-0000-000000000007'),
    ('de441001-0001-0000-0000-000000000002', 'bc441001-0001-0000-0000-000000000008'),
    ('de441001-0001-0000-0000-000000000002', 'bc441001-0001-0000-0000-000000000009'),
    ('de441001-0001-0000-0000-000000000002', 'bc441001-0001-0000-0000-000000000010');

--changeset system:test-data-008 context:test,dev comment:Insert exercise templates

-- ============================================
-- Exercise Templates (sample)
-- ============================================
INSERT INTO public.exercise_templates (id, lesson_template_id, word_context_template_id, exercise_type, exercise_payload, created_at, deleted)
VALUES 
    -- CONTEXTUAL_DISCOVERY flashcard for "accommodate"
    ('ef110001-0001-0000-0000-000000000001', 'de110001-0001-0000-0000-000000000001', 'bc110001-0001-0000-0000-000000000001', 'CONTEXTUAL_DISCOVERY', 
     '{"type": "CONTEXTUAL_DISCOVERY", "prompt": "accommodate (verb)", "hint": "chứa; cung cấp chỗ ở", "highlightedText": "The lecture hall can accommodate 200 students.", "audioUrl": null}', 
     CURRENT_TIMESTAMP, false),
    -- MULTIPLE_CHOICE with explicit source/target languages
    ('ef110001-0001-0000-0000-000000000002', 'de110001-0001-0000-0000-000000000001', 'bc110001-0001-0000-0000-000000000001', 'MULTIPLE_CHOICE', 
     '{"type": "MULTIPLE_CHOICE", "prompt": "Chọn nghĩa tiếng Việt đúng cho từ accommodate.", "question": "Accommodate có nghĩa là gì?", "options": ["cung cấp chỗ ở / chứa", "từ chối", "phớt lờ", "giảm xuống"], "correctAnswer": "cung cấp chỗ ở / chứa", "sourceLanguage": "en", "targetLanguage": "vi"}', 
     CURRENT_TIMESTAMP, false),
    -- CLOZE_WITH_AUDIO aligned with ClozeWithAudioExerciseData
    ('ef221002-0001-0000-0000-000000000001', 'de221002-0001-0000-0000-000000000001', 'bc221002-0001-0000-0000-000000000001', 'CLOZE_WITH_AUDIO', 
     '{"type": "CLOZE_WITH_AUDIO", "sentenceTemplate": "We need to _____ the contract terms.", "correctAnswer": "negotiate", "hint": "You do this when you discuss terms to reach an agreement.", "audioUrl": null}', 
     CURRENT_TIMESTAMP, false);

--changeset system:test-data-009 context:test,dev comment:Insert user goals

-- ============================================
-- User Goals
-- ============================================
INSERT INTO public.user_goals (user_id, goal_id, is_primary, started_at, created_at, deleted)
VALUES 
    ('550e8400-e29b-41d4-a716-446655440000', '11111111-1111-1111-1111-111111111111', true, CURRENT_TIMESTAMP - INTERVAL '60 days', CURRENT_TIMESTAMP, false),
    ('550e8400-e29b-41d4-a716-446655440000', '22222222-2222-2222-2222-222222222222', false, CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP, false),
    ('550e8400-e29b-41d4-a716-446655440001', '11111111-1111-1111-1111-111111111111', true, CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP, false),
    ('550e8400-e29b-41d4-a716-446655440001', '44444444-4444-4444-4444-444444444444', false, CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP, false),
    ('550e8400-e29b-41d4-a716-446655440002', '22222222-2222-2222-2222-222222222222', true, CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP, false),
    ('550e8400-e29b-41d4-a716-446655440003', '33333333-3333-3333-3333-333333333333', true, CURRENT_TIMESTAMP - INTERVAL '45 days', CURRENT_TIMESTAMP, false),
    ('550e8400-e29b-41d4-a716-446655440003', '22222222-2222-2222-2222-222222222222', false, CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP, false);

--changeset system:test-data-010 context:test,dev comment:Insert user unit states

-- ============================================
-- User Unit State
-- ============================================
INSERT INTO public.user_unit_state (id, unit_id, user_id, status, proficiency_score, difficulty_modifier, current_priority_score, is_fast_tracked, last_interaction_at, created_at, deleted)
VALUES 
    ('aa510000-0000-0000-0000-000000000001', 'a1110001-0000-0000-0000-000000000001', '550e8400-e29b-41d4-a716-446655440000', 'IN_PROGRESS', 0.72, 1.0, 85, false, CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP, false),
    ('aa510000-0000-0000-0000-000000000002', 'a1110001-0000-0000-0000-000000000002', '550e8400-e29b-41d4-a716-446655440000', 'NOT_STARTED', NULL, 1.0, 50, false, NULL, CURRENT_TIMESTAMP, false),
    ('aa510000-0000-0000-0000-000000000003', 'a2210002-0000-0000-0000-000000000001', '550e8400-e29b-41d4-a716-446655440000', 'IN_PROGRESS', 0.65, 1.0, 75, false, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP, false);

--changeset system:test-data-011 context:test,dev comment:Insert user lesson sessions

-- ============================================
-- User Lesson Sessions
-- ============================================
INSERT INTO public.user_lesson_sessions (id, lesson_template_id, user_id, user_unit_state_id, status, total_items, completed_items, correct_items, accuracy_rate, created_at, deleted)
VALUES 
    ('ae510000-0000-0000-0000-000000000001', 'de110001-0001-0000-0000-000000000001', '550e8400-e29b-41d4-a716-446655440000', 'aa510000-0000-0000-0000-000000000001', 'COMPLETED', 5, 5, 4, 0.80, CURRENT_TIMESTAMP - INTERVAL '5 days', false),
    ('ae510000-0000-0000-0000-000000000002', 'de110001-0001-0000-0000-000000000002', '550e8400-e29b-41d4-a716-446655440000', 'aa510000-0000-0000-0000-000000000001', 'IN_PROGRESS', 5, 3, 2, 0.67, CURRENT_TIMESTAMP - INTERVAL '2 hours', false);

--changeset system:test-data-012 context:test,dev comment:Insert user exercise attempts

-- ============================================
-- User Exercise Attempts
-- ============================================
INSERT INTO public.user_exercise_attempts (id, exercise_template_id, session_id, user_answer, is_correct, score, time_taken_seconds, created_at, deleted)
VALUES 
    ('aea10000-0000-0000-0000-000000000001', 'ef110001-0001-0000-0000-000000000001', 'ae510000-0000-0000-0000-000000000001', '{"answer": "accommodate"}', true, 100, 45, CURRENT_TIMESTAMP - INTERVAL '5 days', false),
    ('aea10000-0000-0000-0000-000000000002', 'ef110001-0001-0000-0000-000000000002', 'ae510000-0000-0000-0000-000000000001', '{"answer": "to provide space for"}', true, 100, 30, CURRENT_TIMESTAMP - INTERVAL '5 days', false);

--changeset system:test-data-013 context:test,dev comment:Insert user vocab progress

-- ============================================
-- User Vocab Progress
-- ============================================
INSERT INTO public.user_vocab_progress (id, user_id, word_id, active_context_id, is_mastered, relevance_score, ease_factor, interval_days, consecutive_correct_answers, next_review_at, created_at, deleted)
VALUES 
    ('afb10000-0000-0000-0000-000000000001', '550e8400-e29b-41d4-a716-446655440000', 'a0000000-0000-0000-0000-000000000001', 'bc110001-0001-0000-0000-000000000001', false, 0.85, 2.5, 3, 2, CURRENT_TIMESTAMP + INTERVAL '3 days', CURRENT_TIMESTAMP, false),
    ('afb10000-0000-0000-0000-000000000002', '550e8400-e29b-41d4-a716-446655440000', 'a0000000-0000-0000-0000-000000000002', 'bc110001-0001-0000-0000-000000000002', false, 0.80, 2.3, 1, 1, CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP, false),
    ('afb10000-0000-0000-0000-000000000003', '550e8400-e29b-41d4-a716-446655440000', 'b0000000-0000-0000-0000-000000000001', 'bc221002-0001-0000-0000-000000000001', true, 0.92, 2.7, 14, 5, CURRENT_TIMESTAMP + INTERVAL '14 days', CURRENT_TIMESTAMP, false);

package com.e4u.ai_filter_service.batch.reader;

import com.e4u.ai_filter_service.batch.domain.UserContext;
import com.e4u.ai_filter_service.batch.domain.UserWordPair;
import com.e4u.ai_filter_service.common.constants.FilterConstants;
import com.e4u.ai_filter_service.learning.entity.GlobalDictionaryReadOnly;
import com.e4u.ai_filter_service.learning.repository.UserGoalReadRepository;
import com.e4u.ai_filter_service.learning.repository.UserVocabProgressReadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reads pending (user, word-context-template) pairs from {@code e4u_learning}
 * using a {@link JdbcPagingItemReader} backed by the
 * {@code learningDataSource}.
 *
 * <p>
 * <strong>Source table</strong>: {@code word_context_templates} (not
 * {@code user_vocab_progress}).
 * Each row in {@code word_context_templates} with
 * {@code created_for_user_id IS NOT NULL AND ai_reasoning IS NULL}
 * represents one word that has been prepared for a specific user but not yet
 * evaluated by the AI batch job.
 *
 * <p>
 * The query joins to {@code global_dictionary} to fetch the word's lemma,
 * part-of-speech, and definition needed for the AI prompt.
 * The {@code word_context_templates.context_sentence} (user-specific example)
 * is also fetched so the AI gets richer context than the generic
 * {@code global_dictionary.example_sentence}.
 *
 * <p>
 * <strong>Idempotency</strong>: After processing, the writer always sets
 * {@code ai_reasoning IS NOT NULL} on the template row, so subsequent batch
 * runs
 * will not re-read the same rows regardless of the tier outcome.
 *
 * <p>
 * User context (goal names, vocab stats) is resolved once per user and cached
 * in a {@link ConcurrentHashMap} for the lifetime of this step execution to
 * avoid N+1 queries when the same user has many pending templates.
 *
 * <p>
 * The reader is {@code @StepScope} — a fresh instance per job execution
 * prevents state bleed between runs.
 */
@Slf4j
@Component
@StepScope
public class GlobalDictionaryItemReader implements ItemReader<UserWordPair> {

    private final DataSource learningDataSource;
    private final UserVocabProgressReadRepository vocabProgressRepo;
    private final UserGoalReadRepository userGoalRepo;

    /** Per-step cache: userId → resolved UserContext (populated lazily). */
    private final Map<UUID, UserContext> userContextCache = new ConcurrentHashMap<>();

    private JdbcPagingItemReader<UserWordPair> delegate;

    /**
     * Explicit constructor so @Qualifier is applied directly on the DataSource
     * parameter — avoids relying on Lombok's lombok.copyableAnnotations config.
     */
    @Autowired
    public GlobalDictionaryItemReader(
            @Qualifier(FilterConstants.LEARNING_DS) DataSource learningDataSource,
            UserVocabProgressReadRepository vocabProgressRepo,
            UserGoalReadRepository userGoalRepo) {
        this.learningDataSource = learningDataSource;
        this.vocabProgressRepo = vocabProgressRepo;
        this.userGoalRepo = userGoalRepo;
        // Diagnostic: confirm correct datasource is injected (must show e4u_learning,
        // NOT e4u_ai_filter)
        try {
            log.info("[DATASOURCE CHECK] learningDataSource URL={}",
                    learningDataSource.getConnection().getMetaData().getURL());
        } catch (Exception ignored) {
            log.warn("[DATASOURCE CHECK] Could not retrieve datasource URL");
        }
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) throws Exception {
        int chunkSize = stepExecution.getJobExecution()
                .getJobParameters()
                .getLong("chunkSize", 50L)
                .intValue();

        log.info("Initializing GlobalDictionaryItemReader (word_context_templates) with pageSize={}", chunkSize);

        /*
         * Query: join word_context_templates (wct) to global_dictionary (gd).
         *
         * "Pending" rows = user-specific templates where ai_reasoning IS NULL
         * (i.e. the AI batch job has not yet evaluated this (user, word) pair).
         *
         * After processing, the writer always sets ai_reasoning on the template row,
         * so subsequent runs skip it — this is the idempotency gate.
         *
         * Sort by wct.created_for_user_id first (groups a user's words together
         * for UserContext cache efficiency), then by wct.id for stable paging.
         */
        /*
         * IMPORTANT: Sort keys must match the SELECT aliases exactly.
         * PostgresPagingQueryProvider builds the page-2+ keyset WHERE clause by
         * appending "ORDER BY <key> / WHERE (<key> > ? OR ...)" directly into the
         * outer query — table alias prefixes (wct.id) are invalid there because the
         * paging wrapper doesn't repeat the FROM clause in the keyset predicate.
         * Use the SELECT column aliases so the generated SQL is self-consistent.
         */
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause(
                "wct.id               AS context_template_id, " +
                        "wct.created_for_user_id AS user_id, " +
                        "wct.context_sentence AS user_context_sentence, " +
                        "gd.id                AS word_id, " +
                        "gd.lemma, " +
                        "gd.part_of_speech, " +
                        "gd.definition, " +
                        "gd.example_sentence");
        queryProvider.setFromClause(
                "public.word_context_templates wct " +
                        "JOIN public.global_dictionary gd ON wct.word_id = gd.id");
        queryProvider.setWhereClause(
                "wct.created_for_user_id IS NOT NULL " +
                        "AND wct.ai_reasoning IS NULL " +
                        "AND wct.deleted = false " +
                        "AND gd.deleted = false");

        // Sort keys must use SELECT aliases (not table.column) because
        // PostgresPagingQueryProvider injects them into a keyset WHERE predicate
        // that has no FROM clause — table alias references would cause
        // BadSqlGrammarException.
        Map<String, Order> sortKeys = new LinkedHashMap<>();
        sortKeys.put("user_id", Order.ASCENDING);
        sortKeys.put("context_template_id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);

        delegate = new JdbcPagingItemReader<>();
        delegate.setDataSource(learningDataSource);
        delegate.setQueryProvider(queryProvider);
        delegate.setPageSize(chunkSize);
        delegate.setRowMapper(new WordContextTemplateRowMapper());
        delegate.setName("wordContextTemplateItemReader");
        delegate.afterPropertiesSet();
    }

    @Override
    public UserWordPair read() throws Exception {
        if (delegate == null) {
            throw new IllegalStateException("Reader not initialized — @BeforeStep was not called");
        }
        return delegate.read();
    }

    // ─── Row mapper ──────────────────────────────────────────────────────────

    /**
     * Maps each JDBC row (wct + gd JOIN) to a {@link UserWordPair}.
     *
     * <p>
     * User context is resolved via the per-step cache — loaded once per user
     * on first encounter, reused for all subsequent words from the same user.
     */
    private class WordContextTemplateRowMapper implements RowMapper<UserWordPair> {

        @Override
        public UserWordPair mapRow(ResultSet rs, int rowNum) throws SQLException {
            UUID contextTemplateId = rs.getObject("context_template_id", UUID.class);
            UUID userId = rs.getObject("user_id", UUID.class);
            UUID wordId = rs.getObject("word_id", UUID.class);
            String userContextSentence = rs.getString("user_context_sentence"); // may be null

            // Build the GlobalDictionaryReadOnly projection inline (no extra DB call)
            GlobalDictionaryReadOnly word = new GlobalDictionaryReadOnly();
            word.setId(wordId);
            word.setLemma(rs.getString("lemma"));
            word.setPartOfSpeech(rs.getString("part_of_speech"));
            word.setDefinition(rs.getString("definition"));
            word.setExampleSentence(rs.getString("example_sentence"));

            // Resolve user context — load once per user per step, then cache
            UserContext context = userContextCache.computeIfAbsent(userId, this::loadUserContext);

            return new UserWordPair(contextTemplateId, userId, wordId, word, userContextSentence, context);
        }

        /**
         * Loads and aggregates the learner's context from {@code e4u_learning}.
         *
         * <p>
         * Called at most once per user per step execution due to the cache.
         * Falls back to {@link UserContext#unknown()} on any error so the
         * batch step continues rather than fails the entire step.
         */
        private UserContext loadUserContext(UUID userId) {
            try {
                // Goal names from user_goals JOIN goal_definitions
                List<String> goalNames = userGoalRepo.findByUserId(userId)
                        .stream()
                        .map(g -> g.getGoalName())
                        .filter(name -> name != null && !name.isBlank())
                        .distinct()
                        .toList();

                // Vocab stats from user_vocab_progress
                int learned = (int) vocabProgressRepo.countLearnedWordsByUser(userId);
                int mastered = (int) vocabProgressRepo.countMasteredWordsByUser(userId);

                // TODO: derive proficiencyLevel from user_unit_state.proficiency_score or
                // fetch from account-service once that data is available in e4u_learning.
                // For now pass null — the AI prompt treats null as "unknown level".
                return new UserContext(null, goalNames, learned, mastered);

            } catch (Exception ex) {
                log.warn("Failed to load user context for userId={}, using fallback. Reason: {}",
                        userId, ex.getMessage());
                return UserContext.unknown();
            }
        }
    }
}

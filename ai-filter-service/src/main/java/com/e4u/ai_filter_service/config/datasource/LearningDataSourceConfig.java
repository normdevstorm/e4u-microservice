package com.e4u.ai_filter_service.config.datasource;

import com.e4u.ai_filter_service.common.constants.FilterConstants;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Secondary (read-only) DataSource configuration pointing to
 * {@code e4u_learning} —
 * the database shared with {@code learning-service}.
 *
 * <p>
 * <strong>This datasource must NEVER modify the learning DB schema.</strong>
 * DDL is always {@code none}. All writes to {@code global_dictionary}
 * (write-back of
 * AI filter results) must go through this datasource with extreme care.
 *
 * <p>
 * Properties sourced from {@code spring.learning-datasource.*} in
 * ai-filter-service.yml.
 */
@Configuration
@EnableJpaRepositories(basePackages = {
        "com.e4u.ai_filter_service.learning.repository", // GlobalDictionaryReadRepository
}, entityManagerFactoryRef = FilterConstants.LEARNING_EM, transactionManagerRef = FilterConstants.LEARNING_TM)
public class LearningDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.learning-datasource")
    public DataSourceProperties learningDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = FilterConstants.LEARNING_DS)
    public DataSource learningDataSource(
            @Qualifier("learningDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = FilterConstants.LEARNING_EM)
    public LocalContainerEntityManagerFactoryBean learningEntityManager(
            EntityManagerFactoryBuilder builder,
            @Qualifier(FilterConstants.LEARNING_DS) DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(FilterConstants.LEARNING_ENTITY_PACKAGE)
                .persistenceUnit("learningPU")
                .properties(Map.of(
                        // NEVER auto-generate or alter tables in the learning DB
                        "hibernate.hbm2ddl.auto", "none",
                        "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect",
                        "hibernate.format_sql", "true"))
                .build();
    }

    @Bean(name = FilterConstants.LEARNING_TM)
    public PlatformTransactionManager learningTransactionManager(
            @Qualifier(FilterConstants.LEARNING_EM) EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

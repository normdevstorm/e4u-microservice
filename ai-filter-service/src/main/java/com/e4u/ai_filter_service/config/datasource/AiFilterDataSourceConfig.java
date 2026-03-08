package com.e4u.ai_filter_service.config.datasource;

import com.e4u.ai_filter_service.common.constants.FilterConstants;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Primary DataSource configuration pointing to {@code e4u_ai_filter} — the
 * service's own database.
 *
 * <p>
 * This datasource is marked {@code @Primary} so that Spring Batch's
 * {@code JobRepository}
 * auto-configuration wires to it by default. All custom domain entities
 * ({@code FilterJobItem}) are managed by this EntityManagerFactory.
 *
 * <p>
 * Properties sourced from {@code spring.datasource.*} in ai-filter-service.yml.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
        "com.e4u.ai_filter_service.repository", // FilterJobItemRepository
}, entityManagerFactoryRef = FilterConstants.AI_FILTER_EM, transactionManagerRef = FilterConstants.AI_FILTER_TM)
public class AiFilterDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties aiFilterDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = FilterConstants.AI_FILTER_DS)
    @Primary
    public DataSource aiFilterDataSource(
            @Qualifier("aiFilterDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = FilterConstants.AI_FILTER_EM)
    @Primary
    public LocalContainerEntityManagerFactoryBean aiFilterEntityManager(
            EntityManagerFactoryBuilder builder,
            @Qualifier(FilterConstants.AI_FILTER_DS) DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(FilterConstants.AI_FILTER_ENTITY_PACKAGE)
                .persistenceUnit("aiFilterPU")
                .properties(Map.of(
                        "hibernate.hbm2ddl.auto", "none",
                        "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect",
                        "hibernate.format_sql", "true"))
                .build();
    }

    @Bean(name = FilterConstants.AI_FILTER_TM)
    @Primary
    public PlatformTransactionManager aiFilterTransactionManager(
            @Qualifier(FilterConstants.AI_FILTER_EM) EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

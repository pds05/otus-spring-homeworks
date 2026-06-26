package ru.otus.hw.batch.jobs;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@Configuration
@Slf4j
public class AllModelsJobConfig {

    private JobRepository jobRepository;

    @Bean
    public Job migrateAllModelsJob(Step transformAuthorStep,
                                   Step transformGenreStep,
                                   Step transformBookStep,
                                   Step transformUserCommentStep) {
        return new JobBuilder("migrateAllModelsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(transformAuthorStep)
                .next(transformGenreStep)
                .next(transformBookStep)
                .next(transformUserCommentStep)
                .end()
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(@NonNull JobExecution jobExecution) {
                        log.info("Start migration all models job");
                    }

                    public void afterJob(@NonNull JobExecution jobExecution) {
                        log.info("End migration all models job");
                    }
                })
                .build();
    }
}

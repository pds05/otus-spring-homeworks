package ru.otus.hw.batch.jobs;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.batch.processors.AuthorProcessor;
import ru.otus.hw.batch.reader.AuthorReader;
import ru.otus.hw.batch.writers.AuthorDocWriter;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.mongo.AuthorDoc;

@AllArgsConstructor
@Configuration
@Slf4j
public class AuthorJobConfig {

    private static final int CHUNK_SIZE = 5;

    private JobRepository jobRepository;

    private PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job migrateAuthorJob(Step transformAuthorStep) {
        return new JobBuilder("migrateAuthorJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(transformAuthorStep)
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(@NonNull JobExecution jobExecution) {
                        log.info("Start migration author job");
                    }

                    public void afterJob(@NonNull JobExecution jobExecution) {
                        log.info("End migration author job");
                    }
                })
                .build();
    }

    @Bean
    public Step transformAuthorStep(AuthorReader authorReader,
                                    AuthorDocWriter authorDocWriter,
                                    AuthorProcessor authorProcessor,
                                    CustomItemWriteListener<AuthorDoc> customItemWriteListener) {
        return new StepBuilder("transformAuthorStep", jobRepository)
                .<Author, AuthorDoc>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(authorReader)
                .processor(authorProcessor)
                .writer(authorDocWriter)
                .listener(new ItemReadListener<>() {

                    public void afterRead(@NonNull Author author) {
                        log.info("Author read: {}", author);
                    }

                    public void onReadError(Exception e) {
                        log.error("Error reading author", e);
                    }
                })
                .listener(customItemWriteListener)
                .build();
    }
}

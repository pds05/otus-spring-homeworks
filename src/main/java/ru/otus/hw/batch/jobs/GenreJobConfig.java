package ru.otus.hw.batch.jobs;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.batch.processors.GenreProcessor;
import ru.otus.hw.batch.reader.GenreReader;
import ru.otus.hw.batch.writers.GenreDocWriter;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.mongo.GenreDoc;

@AllArgsConstructor
@Configuration
@Slf4j
public class GenreJobConfig {

    private static final int CHUNK_SIZE = 5;

    private JobRepository jobRepository;

    private PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job migrateGenreJob(Step transformGenreStep) {
        return new JobBuilder("migrateGenreJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(transformGenreStep)
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(@NonNull JobExecution jobExecution) {
                        log.info("Start migration genre job");
                    }

                    public void afterJob(@NonNull JobExecution jobExecution) {
                        log.info("End migration genre job");
                    }
                }).build();
    }

    @Bean
    public Step transformGenreStep(GenreReader genreReader,
                                   GenreDocWriter genreDocWriter,
                                   GenreProcessor genreProcessor,
                                   MongoDocItemWriteListener mongoDocItemWriteListener) {
        return new StepBuilder("transformGenreStep", jobRepository)
                .<Genre, GenreDoc>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(genreReader)
                .processor(genreProcessor)
                .writer(genreDocWriter)
                .listener(new ItemReadListener<>() {

                    public void afterRead(@NonNull Genre genre) {
                        log.info("Genre read: {}", genre);
                    }

                    public void onReadError(Exception e) {
                        log.error("Error reading genre", e);
                    }
                })
                .listener(mongoDocItemWriteListener)
                .build();
    }
}

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
import ru.otus.hw.batch.processors.BookProcessor;
import ru.otus.hw.batch.reader.BookReader;
import ru.otus.hw.batch.writers.BookDocWriter;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.mongo.BookDoc;

@AllArgsConstructor
@Configuration
@Slf4j
public class BookJobConfig {

    private static final int CHUNK_SIZE = 5;

    private JobRepository jobRepository;

    private PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job migrateBookJob(Step transformBookStep) {
        return new JobBuilder("migrateBookJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(transformBookStep)
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(@NonNull JobExecution jobExecution) {
                        log.info("Start migration book job");
                    }

                    public void afterJob(@NonNull JobExecution jobExecution) {
                        log.info("End migration book job");
                    }
                }).build();
    }

    @Bean
    public Step transformBookStep(BookReader bookReader,
                                  BookDocWriter bookDocWriter,
                                  BookProcessor bookProcessor) {
        return new StepBuilder("transformBookStep", jobRepository)
                .<Book, BookDoc>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(bookReader)
                .processor(bookProcessor)
                .writer(bookDocWriter)
                .listener(new ItemReadListener<>() {

                    public void afterRead(@NonNull Book book) {
                        log.info("Book read: {}", book);
                    }

                    public void onReadError(Exception e) {
                        log.error("Error reading book", e);
                    }
                }).build();
    }
}

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
import ru.otus.hw.batch.processors.UserCommentProcessor;
import ru.otus.hw.batch.reader.UserCommentReader;
import ru.otus.hw.batch.writers.UserCommentDocWriter;
import ru.otus.hw.models.UserComment;
import ru.otus.hw.models.mongo.UserCommentDoc;

@AllArgsConstructor
@Configuration
@Slf4j
public class UserCommentJobConfig {

    private static final int CHUNK_SIZE = 5;

    private JobRepository jobRepository;

    private PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job migrateUserCommentJob(Step transformUserCommentStep) {
        return new JobBuilder("migrateUserCommentJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(transformUserCommentStep)
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(@NonNull JobExecution jobExecution) {
                        log.info("Start migration user comment job");
                    }

                    public void afterJob(@NonNull JobExecution jobExecution) {
                        log.info("End migration user comment job");
                    }
                }).build();
    }

    @Bean
    public Step transformUserCommentStep(UserCommentReader userCommentReader,
                                         UserCommentDocWriter userCommentDocWriter,
                                         UserCommentProcessor userCommentProcessor) {
        return new StepBuilder("transformUserCommentStep", jobRepository)
                .<UserComment, UserCommentDoc>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(userCommentReader)
                .processor(userCommentProcessor)
                .writer(userCommentDocWriter)
                .listener(new ItemReadListener<>() {

                    public void afterRead(@NonNull UserComment userComment) {
                        log.info("userComment read: {}", userComment);
                    }

                    public void onReadError(Exception e) {
                        log.error("Error reading userComment", e);
                    }
                }).build();
    }
}

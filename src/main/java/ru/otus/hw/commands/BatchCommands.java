package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;


@RequiredArgsConstructor
@ShellComponent
public class BatchCommands {

    private final Job migrateAuthorJob;

    private final Job migrateGenreJob;

    private final Job migrateBookJob;

    private final Job migrateUserCommentJob;

    private final Job migrateAllModelsJob;

    private final JobOperator jobOperator;

    private final JobExplorer jobExplorer;

    @ShellMethod(value = "startMigrationAuthorsJob", key = "sm-a")
    public void migrateAuthors() throws Exception {
        Long executionId = jobOperator.startNextInstance(migrateAuthorJob.getName());
        System.out.println(jobOperator.getSummary(executionId));
    }

    @ShellMethod(value = "startMigrationGenresJob", key = "sm-g")
    public void migrateGenres() throws Exception {
        Long executionId = jobOperator.startNextInstance(migrateGenreJob.getName());
        System.out.println(jobOperator.getSummary(executionId));
    }

    @ShellMethod(value = "startMigrationBooksJob", key = "sm-b")
    public void migrateBooks() throws Exception {
        Long executionId = jobOperator.startNextInstance(migrateBookJob.getName());
        System.out.println(jobOperator.getSummary(executionId));
    }

    @ShellMethod(value = "startMigrationUserCommentsJob", key = "sm-uc")
    public void migrateUserComments() throws Exception {
        Long executionId = jobOperator.startNextInstance(migrateUserCommentJob.getName());
        System.out.println(jobOperator.getSummary(executionId));
    }

    @ShellMethod(value = "startMigrationAllModelsJob", key = "sm-am")
    public void migrateAllModels() throws Exception {
        Long executionId = jobOperator.startNextInstance(migrateAllModelsJob.getName());
        System.out.println(jobOperator.getSummary(executionId));
    }

    @ShellMethod(value = "showInfo", key = "i")
    public void showInfo() {
        List<String> jobNames = jobExplorer.getJobNames();
        for (String jobName : jobNames) {
            JobInstance jobInstance = jobExplorer.getLastJobInstance(jobName);
            if (jobInstance != null) {
                System.out.println(jobName + ": " + jobExplorer.getJobExecutions(jobInstance));
            }
        }
    }
}

package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.service.TestService;
import ru.otus.hw.service.TestServiceImpl;
import ru.otus.hw.service.TestRunnerService;
import ru.otus.hw.service.TestRunnerServiceImpl;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.StreamsIOService;
import ru.otus.hw.service.ResultService;
import ru.otus.hw.service.ResultServiceImpl;
import ru.otus.hw.service.StudentService;
import ru.otus.hw.service.StudentServiceImpl;


@ComponentScan
@PropertySource("classpath:application.properties")
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final AppProperties appProperties;

    @Bean
    public TestRunnerService testRunnerService() {
        return new TestRunnerServiceImpl(testService(), studentService(), resultService());
    }

    @Bean
    public IOService ioService() {
        return new StreamsIOService(System.out, System.in);
    }

    @Bean
    public QuestionDao questionDao() {
        return new CsvQuestionDao(appProperties);
    }

    @Bean
    public StudentService studentService() {
        return new StudentServiceImpl(ioService());
    }

    @Bean
    public ResultService resultService() {
        return new ResultServiceImpl(appProperties, ioService());
    }

    @Bean
    public TestService testService() {
        return new TestServiceImpl(ioService(), questionDao());
    }

}

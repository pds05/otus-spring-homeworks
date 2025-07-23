package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestServiceImplTest {

    @Mock
    private CsvQuestionDao questionDao;

    @Mock
    private StreamsIOService ioService;

    @InjectMocks
    private TestServiceImpl testServiceImpl;

    @Test
    void checkTestResultTest() {
        Question question1 = new Question("Is there life on Mars?",
                Arrays.asList(
                        new Answer("Science doesn't know this yet", true),
                        new Answer("Certainly. The red UFO is from Mars. And green is from Venus", false)
                ));
        Question question2 = new Question("How should resources be loaded form jar in Java?",
                Arrays.asList(
                        new Answer("ClassLoader#geResourceAsStream or ClassPathResource#getInputStream", true),
                        new Answer("ClassLoader#geResource#getFile + FileReader", false),
                        new Answer("Wingardium Leviosa", false)
                ));

        when(questionDao.findAll()).thenReturn(Arrays.asList(question1, question2));
        when(ioService.readStringWithPrompt(contains("Is there life on Mars?"))).thenReturn("Science doesn't know this yet");
        when(ioService.readStringWithPrompt(contains("How should resources be loaded form jar in Java"))).thenReturn("Wingardium Leviosa");

        Student student = new Student("Ivan", "Ivanov");
        TestResult testResult = testServiceImpl.executeTestFor(student);
        assertEquals(1, testResult.getRightAnswersCount());
        assertEquals(2, testResult.getAnsweredQuestions().size());
    }

}

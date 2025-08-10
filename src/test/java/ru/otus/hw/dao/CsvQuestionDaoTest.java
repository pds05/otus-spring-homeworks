package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CsvQuestionDaoTest {

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private CsvQuestionDao csvQuestionDao;

    @Test
    void readQuestionFromCsvFile() {
        Mockito.when(appProperties.getTestFileName()).thenReturn("questions.csv");

        List<Question> questions = csvQuestionDao.findAll();
        assertEquals(5, questions.size());

        Question question = questions.get(0);
        assertEquals("Is there life on Mars?", question.text());
        assertEquals(3, questions.get(0).answers().size());

        Answer answer = questions.get(0).answers().get(0);
        assertEquals("Science doesn't know this yet", answer.text());
        assertTrue(answer.isCorrect());
    }
}

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.StreamsIOService;
import ru.otus.hw.service.TestService;
import ru.otus.hw.service.TestServiceImpl;

import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class AnswerOnQuestionTest {

    @Mock
    CsvQuestionDao questionDao;

    @Mock
    StreamsIOService ioService;

    private TestService testService;

    @BeforeEach
    void init() {
        testService = new TestServiceImpl(ioService, questionDao);
    }

    @Test
    void startTestRunnerService() {
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
        Mockito.when(questionDao.findAll()).thenReturn(Arrays.asList(question1, question2));
        Mockito.doNothing().when(ioService).printLine(Mockito.anyString());
        Mockito.doNothing().when(ioService).printFormattedLine(Mockito.anyString());
        testService.executeTest();
        Mockito.verify(questionDao, Mockito.only()).findAll();
    }
}
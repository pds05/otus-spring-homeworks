import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hw.service.TestRunnerService;
import ru.otus.hw.service.TestService;

import static org.junit.jupiter.api.Assertions.*;

public class AnswerOnQuestionTest {
    private static ApplicationContext context;

    @BeforeAll
    static void init() {
        context = new ClassPathXmlApplicationContext("/spring-context-test.xml");
    }

    @Test
    void startTestRunnerService() {
        var testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();
        TestService testService = testRunnerService.getTestService();
        assertFalse(testService.getResult().isEmpty());
        assertEquals(5, testService.getQuestions().size());
        assertEquals(4, testService.getSuccessAnswerResult().size());
        assertEquals(1, testService.getFailureAnswerResult().size());
    }
}
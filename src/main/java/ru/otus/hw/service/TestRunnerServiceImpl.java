package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.QuestionReadException;

@Service
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    private final LocalizedIOService localizedIOService;

    @Override
    public void run() {
        try {
            var student = studentService.determineCurrentStudent();
            var testResult = testService.executeTestFor(student);
            resultService.showResult(testResult);
        } catch (QuestionReadException qre) {
            localizedIOService.printFormattedLineLocalized("ResultService.error.test", "question error occurred");
        } catch (Exception e) {
            localizedIOService.printFormattedLineLocalized("ResultService.error.test", "common error occurred");
        }
    }
}

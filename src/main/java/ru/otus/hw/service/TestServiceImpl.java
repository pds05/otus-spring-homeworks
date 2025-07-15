package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLine("Beginning of student IQ testing");
        ioService.printFormattedLine("Please answer the questions below%n");

        var questions = questionDao.findAll();

        var testResult = new TestResult(student);

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String questionForPrint = convertQuestionToString(question, i + 1);

            String answer = ioService.readStringWithPrompt(questionForPrint);
            var isAnswerValid = question.answers().stream()
                    .filter(a -> a.text().equalsIgnoreCase(answer))
                    .findFirst()
                    .orElse(new Answer(answer, false))
                    .isCorrect();

            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private String convertQuestionToString(Question question, Integer index) {
        StringBuilder sb = new StringBuilder("Question");
        if (index != null) {
            sb.append(" #").append(index);
        }
        sb.append(":%n");
        sb.append(question.text()).append("%n");
        sb.append("Answers:%n");
        int ascii = 97;
        for (int j = 0; j < question.answers().size(); j++) {
            sb.append(Character.toChars(ascii));
            sb.append(") %s%n");
            ascii++;
        }
        return sb.toString().formatted(question.answers().stream().map(Answer::text).toArray());
    }
}

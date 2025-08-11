package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOServiceImpl ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String questionForPrint = convertQuestionToString(question, i + 1);

            String answer = ioService.readStringWithPrompt(questionForPrint);
            testResult.applyAnswer(question, isAnswerValid(answer, question));
        }
        return testResult;
    }

    private boolean isAnswerValid(String answer, Question question) {
        return question.answers().stream()
                .filter(a -> a.text().equalsIgnoreCase(answer))
                .map(Answer::isCorrect)
                .findFirst()
                .orElse(false);
    }

    private String convertQuestionToString(Question question, Integer index) {
        StringBuilder sb = new StringBuilder();
        sb.append(ioService.getMessage("TestService.info.question"));
        if (index != null) {
            sb.append(" #").append(index);
        }
        sb.append(":%n");
        sb.append(question.text()).append("%n");
        sb.append(ioService.getMessage("TestService.info.answer")).append("%n");
        int ascii = 97;
        for (int j = 0; j < question.answers().size(); j++) {
            sb.append(Character.toChars(ascii));
            sb.append(") %s%n");
            ascii++;
        }
        return sb.toString().formatted(question.answers().stream().map(Answer::text).toArray());
    }

}

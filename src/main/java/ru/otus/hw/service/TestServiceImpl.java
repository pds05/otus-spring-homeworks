package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("Beginning of student IQ testing");
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        List<Question> questions = questionDao.findAll();
        printQuestions(questions);
    }

    private void printQuestions(List<Question> questions) {
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String msg = convertQuestionToString(question, i + 1);
            ioService.printLine(msg);
        }
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

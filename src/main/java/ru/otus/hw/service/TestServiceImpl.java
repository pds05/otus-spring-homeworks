package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        StringBuilder sb = new StringBuilder();
        AtomicInteger counter = new AtomicInteger(1);
        questions.forEach(question -> {
            AtomicInteger ascii = new AtomicInteger(97);
            sb.append("Question #").append(counter).append(":%n");
            sb.append(question.text()).append("%n");
            sb.append("Choice answers:%n");
            question.answers().forEach(
                    a -> {
                        sb.append(Character.toChars(ascii.get()));
                        sb.append(") %s%n");
                        ascii.getAndIncrement();
                    }
            );
            ioService.printFormattedLine(sb.toString(), question.answers().stream().map(Answer::text).toArray());
            sb.setLength(0);
            counter.getAndIncrement();
        });
    }
}

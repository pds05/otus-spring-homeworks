package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;
    private final QuestionDao questionDao;
    private final Map<Question, Answer> questionAnswersMap = new LinkedHashMap<>();

    @Override
    public void executeTest() {
        ioService.printLine("Beginning of student IQ testing");
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        // Получить вопросы из дао и вывести их с вариантами ответов
        List<Question> questions = questionDao.findAll();
        questions.forEach(question -> {
            ioService.printFormattedLine(question.text() + "%n");
            String answerString = ioService.readLine();
            Answer answer = question.answers().stream().filter(a -> a.text().equalsIgnoreCase(answerString)).findFirst().orElse(new Answer(answerString, false));
            questionAnswersMap.put(question, answer);
        });
        ioService.printLine("");
        ioService.printLine("Test completed");
        ioService.printLine("Results: correct answers " + questionAnswersMap.values().stream().filter(Answer::isCorrect).count() + "/" + questionAnswersMap.size());
    }

    @Override
    public Map<Question, Answer> getResult() {
        return questionAnswersMap;
    }

    @Override
    public List<Answer> getSuccessAnswerResult() {
        return questionAnswersMap.values().stream().filter(Answer::isCorrect).collect(Collectors.toList());
    }

    @Override
    public List<Answer> getFailureAnswerResult() {
        return questionAnswersMap.values().stream().filter(answer -> !answer.isCorrect()).collect(Collectors.toList());
    }

    @Override
    public List<Question> getQuestions() {
        return questionAnswersMap.keySet().stream().toList();
    }
}

package ru.otus.hw.service;

import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;
import java.util.Map;

public interface TestService {

    void executeTest();

    Map<Question, Answer> getResult();

    List<Answer> getSuccessAnswerResult();

    List<Answer> getFailureAnswerResult();

    List<Question> getQuestions();
}

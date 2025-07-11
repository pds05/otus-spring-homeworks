package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.FuzzyMappingStrategy;
import com.opencsv.bean.MappingStrategy;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        MappingStrategy<QuestionDto> strategy = new FuzzyMappingStrategy<>();
        strategy.setType(QuestionDto.class);
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileNameProvider.getTestFileName());
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            List<QuestionDto> questionDto = new CsvToBeanBuilder<QuestionDto>(br)
                    .withSkipLines(1).withSeparator(';')
                    .withType(QuestionDto.class)
                    .build()
                    .parse();
            return questionDto.stream().map(QuestionDto::toDomainObject).toList();
        } catch (Exception e) {
            throw new QuestionReadException("Failed to read from file with questions", e);
        }
    }
}

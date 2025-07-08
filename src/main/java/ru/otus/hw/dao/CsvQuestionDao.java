package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.FuzzyMappingStrategy;
import com.opencsv.bean.MappingStrategy;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        // Использовать CsvToBean
        // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
        // Использовать QuestionReadException
        // Про ресурсы: https://mkyong.com/java/java-read-a-file-from-resources-folder/
        try {
            MappingStrategy<QuestionDto> strategy = new FuzzyMappingStrategy<>();
            strategy.setType(QuestionDto.class);
            List<QuestionDto> questionDto = new CsvToBeanBuilder<QuestionDto>(new FileReader(Objects.requireNonNull(this.getClass().getResource(fileNameProvider.getTestFileName())).getFile())).withSkipLines(1).withSeparator(';').withType(QuestionDto.class).build().parse();
            return questionDto.stream().map(QuestionDto::toDomainObject).toList();
        } catch (FileNotFoundException e) {
            throw new QuestionReadException("Question file not found", e);
        }
    }
}

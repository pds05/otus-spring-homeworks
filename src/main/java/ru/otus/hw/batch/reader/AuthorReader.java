package ru.otus.hw.batch.reader;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.Map;

@StepScope
@Component
public class AuthorReader extends RepositoryItemReader<Author> {

    public AuthorReader(AuthorRepository authorRepository) {
        setRepository(authorRepository);
        setMethodName("findAll");
        setSort(Map.of("id", Sort.Direction.ASC));
    }
}

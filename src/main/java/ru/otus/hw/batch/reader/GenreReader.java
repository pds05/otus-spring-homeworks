package ru.otus.hw.batch.reader;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Map;

@StepScope
@Component
public class GenreReader extends RepositoryItemReader<Genre> {

    public GenreReader(GenreRepository genreRepository) {
        setRepository(genreRepository);
        setMethodName("findAll");
        setSort(Map.of("id", Sort.Direction.ASC));
    }
}

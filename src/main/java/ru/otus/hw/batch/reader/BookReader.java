package ru.otus.hw.batch.reader;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.BookRepository;

import java.util.Map;

@StepScope
@Component
public class BookReader extends RepositoryItemReader<Book> {

    public BookReader(BookRepository bookRepository) {
        setRepository(bookRepository);
        setMethodName("findAll");
        setSort(Map.of("id", Sort.Direction.ASC));
    }
}

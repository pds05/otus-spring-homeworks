package ru.otus.hw.mongock.changeunits;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@AllArgsConstructor
@ChangeUnit(id = "init-books", order = "003", author = "mongock")
public class MigrationBooks {

    private final MongoTemplate mongoTemplate;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    @BeforeExecution
    public void createCollection() {
        mongoTemplate.createCollection(Book.class);
    }

    @RollbackBeforeExecution
    public void rollbackBefore() {

    }

    @Execution
    public void migrate() {
        Book book1 = new Book();
        book1.setTitle("BookTitle_1");

        Author author1 = authorRepository.findByFullName("Author_1").orElseThrow(() -> new RuntimeException("Author_1 not found"));
        book1.setAuthor(author1);

        Genre genre1 = genreRepository.findByName("Genre_1").orElseThrow(() -> new RuntimeException("Genre_1 not found"));
        Genre genre2 = genreRepository.findByName("Genre_2").orElseThrow(() -> new RuntimeException("Genre_2 not found"));
        book1.setGenres(List.of(genre1, genre2));
        mongoTemplate.save(book1);

        Book book2 = new Book();
        book2.setTitle("BookTitle_2");

        Author author2 = authorRepository.findByFullName("Author_2").orElseThrow(() -> new RuntimeException("Author_2 not found"));
        book2.setAuthor(author2);

        Genre genre3 = genreRepository.findByName("Genre_3").orElseThrow(() -> new RuntimeException("Genre_3 not found"));
        book2.setGenres(List.of(genre2, genre3));
        mongoTemplate.save(book2);

        Book book3 = new Book();
        book3.setTitle("BookTitle_3");

        Author author3 = authorRepository.findByFullName("Author_3").orElseThrow(() -> new RuntimeException("Author_3 not found"));
        book3.setAuthor(author3);
        book3.setGenres(List.of(genre1, genre2, genre3));
        mongoTemplate.save(book3);
    }

    @RollbackExecution
    public void rollback() {

    }
}

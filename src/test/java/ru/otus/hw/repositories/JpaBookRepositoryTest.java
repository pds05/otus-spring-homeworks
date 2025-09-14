package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с книгами ")
@DataJpaTest
@Import({JpaBookRepository.class})
class JpaBookRepositoryTest {

    public static final int FIRST_BOOK_ID = 1;

    @Autowired
    private JpaBookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnBookById() {
        var optionalBook = bookRepository.findById(FIRST_BOOK_ID);
        var expectedBook = entityManager.find(Book.class, FIRST_BOOK_ID);
        assertThat(optionalBook).isPresent()
                .get()
                .isEqualTo(expectedBook);
        System.out.println(optionalBook.get());
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookRepository.findAll();

        assertThat(actualBooks).isNotEmpty().allMatch(b -> b.getId() > 0L);
        actualBooks.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var author = entityManager.find(Author.class, 3);
        var genre3 = entityManager.find(Genre.class, 3);
        var genre6 = entityManager.find(Genre.class, 6);
        var expectedBook = new Book(0, "BookTitle_10500", author, List.of(genre3, genre6));
        var returnedBook = bookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(entityManager.find(Book.class, returnedBook.getId())).isEqualTo(expectedBook);

        System.out.println(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var author = entityManager.find(Author.class, 3);
        var genre2 = entityManager.find(Genre.class, 2);
        var genre4 = entityManager.find(Genre.class, 4);
        var expectedBook = new Book(FIRST_BOOK_ID, "BookTitle_10500", author,
                List.of(genre2, genre4));
        assertThat(entityManager.find(Book.class, expectedBook.getId())).isNotEqualTo(expectedBook);

        var returnedBook = bookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(entityManager.find(Book.class, returnedBook.getId()))
                .isEqualTo(returnedBook);

        System.out.println(returnedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        assertThat(entityManager.find(Book.class, FIRST_BOOK_ID)).isNotNull();
        bookRepository.deleteById(FIRST_BOOK_ID);
        assertThat(entityManager.find(Book.class, FIRST_BOOK_ID)).isNull();
    }

}
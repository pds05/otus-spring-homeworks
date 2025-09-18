package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.exceptions.EntityNotFoundException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Сервис для работы с книгами")
@SpringBootTest
public class BookServiceTest {

    @Autowired
    BookServiceImpl bookService;
    @Autowired
    BookConverter bookConverter;

    @Test
    @DisplayName("должен вернуть список всех книг")
    void shouldReturnBooksList() {
        var books = bookService.findAll();

        assertThat(books).isNotNull();
        assertThat(books).size().isEqualTo(3);

        books.forEach(book -> System.out.println(
                bookConverter.bookDtoToString(book)));
    }

    @Test
    @DisplayName("должен вернуть книгу по идентификатору")
    void shouldReturnBookById() {
        assertDoesNotThrow(() -> bookService.findById(1));
    }

    @Transactional
    @Test
    @DisplayName("Должен добавлять новую книгу")
    void shouldInsertNewBook() {
        var returnedBook = bookService.insert("Test book", 1, Set.of(2L, 3L));

        assertThat(returnedBook).isNotNull();
        assertThat(returnedBook.id()).isGreaterThan(0);

        System.out.println(bookConverter.bookDtoToString(returnedBook));
    }

    @Transactional
    @Test
    @DisplayName("должен добавлять и обновлять книгу")
    void shouldInsertAndUpdateBook() {
        var expectedBook = bookService.insert("Test book for update", 1L, Set.of(2L, 3L));
        var updatedBook = bookService.update(expectedBook.id(), "Edited: " + expectedBook.title(), 2, Set.of(4L, 5L, 6L));

        assertThat(updatedBook.id()).isEqualTo(expectedBook.id());
        assertThat(expectedBook).isNotEqualTo(updatedBook);
        assertThat(updatedBook.title()).startsWith("Edited");
        assertThat(updatedBook.authorId()).isEqualTo(2L);
        assertThat(updatedBook.genreIds()).hasSize(3).containsExactlyInAnyOrder(4L, 5L, 6L);

        System.out.println(bookConverter.bookDtoToString(updatedBook));
    }

    @Transactional
    @Test
    @DisplayName("должен добавлять и удалять книгу")
    void shouldInsertAndDeleteBook() {
        var returnedBook = bookService.insert("Test book for delete", 1L, Set.of(2L, 3L));
        bookService.deleteById(returnedBook.id());

        assertThrows(EntityNotFoundException.class, () -> bookService.findById(returnedBook.id()));
    }
}

package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.exceptions.EntityNotFoundException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Сервис для работы с книгами")
@SpringBootTest
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
public class BookServiceTest {

    @Autowired
    private BookServiceImpl bookService;

    @Test
    @DisplayName("должен вернуть список всех книг")
    void shouldReturnBooksList() {
        var books = bookService.findAll();

        assertThat(books).isNotNull();
        assertThat(books).size().isGreaterThan(0);
    }

    @Test
    @DisplayName("должен вернуть книгу по идентификатору")
    void shouldReturnBookById() {
        assertDoesNotThrow(() -> bookService.findById(1));
    }

    @Test
    @DisplayName("Должен добавлять новую книгу")
    void shouldInsertBook() {
        var returnedBook = bookService.insert("Test book", 1, Set.of(2L, 3L));

        assertThat(returnedBook).isNotNull();
        assertThat(returnedBook.id()).isGreaterThan(0);
    }

    @Test
    @DisplayName("должен добавлять и обновлять книгу")
    void shouldUpdateBook() {
        var expectedBook = bookService.findById(1);
        var updatedBook = bookService.update(expectedBook.id(), "Edited: " + expectedBook.title(), 2, Set.of(4L, 5L, 6L));

        assertThat(updatedBook.id()).isEqualTo(expectedBook.id());
        assertThat(expectedBook).isNotEqualTo(updatedBook);
        assertThat(updatedBook.title()).startsWith("Edited");
        assertThat(updatedBook.author().id()).isEqualTo(2L);
        assertThat(updatedBook.genres()).hasSize(3);
    }

    @Test
    @DisplayName("должен добавлять и удалять книгу")
    void shouldDeleteBook() {
        bookService.deleteById(2);

        assertThrows(EntityNotFoundException.class, () -> bookService.findById(2));
    }
}

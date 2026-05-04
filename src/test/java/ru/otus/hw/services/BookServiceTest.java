package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.UserComment;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Сервис для работы с книгами")
@DataMongoTest
@ComponentScan(basePackages = {"ru.otus.hw.services", "ru.otus.hw.converters"})
public class BookServiceTest {
    @Autowired
    private BookServiceImpl bookService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("должен вернуть список всех книг")
    void shouldReturnBooksList() {
        var expected = mongoTemplate.findAll(Book.class);
        var books = bookService.findAll();

        assertThat(books).isNotNull();
        assertThat(books).size().isEqualTo(expected.size());
        assertTrue(books.stream().allMatch(dto -> expected.stream().anyMatch(book -> book.getId().equals(dto.id()))));
    }

    @Test
    @DisplayName("должен вернуть книгу по идентификатору")
    void shouldReturnBookById() {
        var expected = mongoTemplate.findAll(Book.class).get(0);
        assertDoesNotThrow(() -> bookService.findById(expected.getId()));
    }

    @Test
    @DisplayName("Должен добавлять новую книгу")
    void shouldInsertBook() {
        var author = mongoTemplate.findAll(Author.class).get(0);
        var genres = mongoTemplate.findAll(Genre.class);
        var returnedBook = bookService.insert("Test book", author.getId(), Set.of(genres.get(0).getId(), genres.get(1).getId()));

        assertThat(returnedBook).isNotNull();
        assertFalse(returnedBook.id().isBlank());
    }

    @Test
    @DisplayName("Должен обновлять книгу")
    void shouldUpdateBook() {
        var expectedBook = mongoTemplate.findAll(Book.class).get(0);
        var expectedGenres = mongoTemplate.findAll(Genre.class);
        var updatedBook = bookService.update(expectedBook.getId(), "Edited: " + expectedBook.getTitle(),
                expectedBook.getAuthor().getId(),
                Set.of(expectedGenres.get(0).getId(), expectedGenres.get(1).getId()));
        assertThat(updatedBook.id()).isEqualTo(expectedBook.getId());
        assertThat(expectedBook).isNotEqualTo(updatedBook);
        assertThat(updatedBook.title()).startsWith("Edited");
        assertThat(updatedBook.author().id()).isEqualTo(expectedBook.getAuthor().getId());
        assertThat(updatedBook.genres()).hasSize(2);
    }

    @Test
    @DisplayName("должен удалять книгу")
    void shouldDeleteBook() {
        var expectedBook = mongoTemplate.findAll(Book.class).get(0);
        UserComment uc = mongoTemplate.findOne(Query.query(Criteria.where("book").is(expectedBook)), UserComment.class);
        assertNotNull(uc);
        bookService.deleteById(expectedBook.getId());
        assertNull(mongoTemplate.findById(uc.getId(), UserComment.class));
        assertThrows(EntityNotFoundException.class, () -> bookService.findById(expectedBook.getId()));

    }
}

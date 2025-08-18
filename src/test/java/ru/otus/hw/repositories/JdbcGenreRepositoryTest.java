package ru.otus.hw.repositories;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с жанрами книг")
@JdbcTest
@Import(JdbcGenreRepository.class)
public class JdbcGenreRepositoryTest {

    @Autowired

    private JdbcGenreRepository repositoryJdbc;

    private List<Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = getDbGenres();
    }

    @DisplayName("должен загружать список всех жанров книг")
    @Test
    void shouldReturnCorrectAuthorList() {
        var actualGenres = repositoryJdbc.findAll();
        var expectedAuthors = dbGenres;

        assertThat(dbGenres).containsExactlyElementsOf(expectedAuthors);
        actualGenres.forEach(System.out::println);
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }
}

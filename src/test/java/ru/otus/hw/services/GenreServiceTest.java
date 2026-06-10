package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с жанрами книг")
@SpringBootTest
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
public class GenreServiceTest {
    @Autowired
    private GenreService genreService;

    @Test
    @DisplayName("должен вернуть список всех жанров")
    void shouldReturnGenreList() {
        var genres = genreService.findAll();

        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(6);
    }
}

package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.converters.GenreConverter;

@DisplayName("Сервис для работы с жанрами книг")
@SpringBootTest
public class GenreServiceTest {
    @Autowired
    private GenreService genreService;
    @Autowired
    private GenreConverter genreConverter;

    @Test
    @DisplayName("должен вернуть список всех жанров")
    void shouldReturnGenreList() {
        var genres = genreService.findAll();

        genres.forEach(genre -> System.out.println(
                genreConverter.genreDtoToString(genre)));

    }
}

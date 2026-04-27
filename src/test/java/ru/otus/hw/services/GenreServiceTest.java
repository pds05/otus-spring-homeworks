package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.Application;
import ru.otus.hw.models.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с жанрами книг")
@DataMongoTest
@ComponentScan(basePackageClasses = Application.class)
@ExtendWith(SpringExtension.class)
public class GenreServiceTest {
    @Autowired
    private GenreService genreService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("должен вернуть список всех жанров")
    void shouldReturnGenreList() {
        var expectedGenres = mongoTemplate.findAll(Genre.class);
        var genres = genreService.findAll();

        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(expectedGenres.size());
    }
}

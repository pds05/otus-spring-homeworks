package ru.otus.hw.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с авторами")
@DataMongoTest
public class AuthorServiceTest {
    @Autowired
    private AuthorService authorService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("должен вернуть список авторов")
    void shouldReturnAuthors() {
        var expected = mongoTemplate.findAll(Author.class);
        var authors = authorService.findAll();

        assertThat(authors).isNotNull();
        assertThat(authors.size()).isEqualTo(expected.size());
        Assertions.assertTrue(authors.stream().allMatch(dto -> expected.stream().anyMatch(author -> author.getId().equals(dto.id()))));
    }
}

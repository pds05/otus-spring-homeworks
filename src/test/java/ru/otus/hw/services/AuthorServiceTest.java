package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с авторами")
@SpringBootTest
public class AuthorServiceTest {
    @Autowired
    private AuthorServiceImpl authorService;

    @Test
    @DisplayName("должен вернуть список авторов")
    void shouldReturnAuthors() {
        var authors = authorService.findAll();

        assertThat(authors).isNotNull();
        assertThat(authors.size()).isEqualTo(3);
    }

}

package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с авторами")
@DataJpaTest
@Import(JpaAuthorRepository.class)
public class JpaAuthorRepositoryTest {

    public static final int FIRST_AUTHOR_ID = 1;

    @Autowired
    private JpaAuthorRepository authorRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("должен загружать всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var authors = authorRepository.findAll();

        assertThat(authors).isNotEmpty().allMatch(a -> a.getId() > 0L);
    }

    @DisplayName("должен загружать автора по его id")
    @Test
    void shouldReturnAuthorById() {
        var optionalAuthor = authorRepository.findById(FIRST_AUTHOR_ID);
        var expectedAuthor = entityManager.find(Author.class, FIRST_AUTHOR_ID);

        assertThat(optionalAuthor).isPresent().get().isEqualTo(expectedAuthor);
    }
}

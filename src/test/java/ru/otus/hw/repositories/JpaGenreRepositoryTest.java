package ru.otus.hw.repositories;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с жанрами книг")
@DataJpaTest
@Import(JpaGenreRepository.class)
public class JpaGenreRepositoryTest {

    @Autowired
    private JpaGenreRepository genreRepository;

    @Autowired
    private TestEntityManager entityManager;


    @DisplayName("должен загружать список всех жанров книг")
    @Test
    void shouldReturnCorrectGenreList() {
        var actualGenres = genreRepository.findAll();

        assertThat(actualGenres).isNotEmpty().allMatch(g -> g.getId() > 0L);
    }

    @DisplayName("должен загружать список жанров по списку идентификаторов")
    @Test
    void shouldReturnGenresByIdsList() {
        Set<Long> expectedIds = Set.of(2L, 4L, 6L);

        var returnedGenres = genreRepository.findAllByIds(expectedIds);

        var genre_2 = entityManager.find(Genre.class, 2L);
        var genre_4 = entityManager.find(Genre.class, 4L);
        var genre_6 = entityManager.find(Genre.class, 6L);
        List<Genre> expectedGenres = List.of(genre_2, genre_4, genre_6);

        assertThat(returnedGenres.size()).isEqualTo(expectedIds.size());
        assertThat(returnedGenres).allMatch(genre -> expectedIds.contains(genre.getId()));
        assertThat(returnedGenres).containsExactlyElementsOf(expectedGenres);
    }
}

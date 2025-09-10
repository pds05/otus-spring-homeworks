package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.User;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с читателями книг")
@DataJpaTest
@Import({JpaUserRepository.class})
public class JpaUserRepositoryTest {

    public static final int FIRST_USER_ID = 1;
    public static final String FIRST_USER_USERNAME = "User_1";

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @DisplayName("должен загружать всех читателей")
    @Test
    void shouldReturnCorrectUsersList() {
        var users = userRepository.findAll();

        assertThat(users).isNotEmpty().allMatch(a -> a.getId() > 0L);
        users.forEach(System.out::println);
    }

    @DisplayName("должен загружать читателя по его id")
    @Test
    void shouldReturnUserById() {
        var optionalUser = userRepository.findById(FIRST_USER_ID);
        var expectedUser = entityManager.find(User.class, FIRST_USER_ID);

        assertThat(optionalUser).isPresent().get().isEqualTo(expectedUser);
        System.out.println(optionalUser.get());
    }

    @DisplayName("должен загружать читателя по его username")
    @Test
    void shouldReturnAuthorByUsername() {
        var optionalUser = userRepository.findByUsername(FIRST_USER_USERNAME);
        var expectedUser = entityManager.find(User.class, FIRST_USER_ID);

        assertThat(optionalUser).isPresent().get().isEqualTo(expectedUser);
        System.out.println(optionalUser.get());
    }
}

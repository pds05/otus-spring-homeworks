package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.User;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с читателями книг")
@DataJpaTest
public class UserRepositoryTest {

    public static final long FIRST_USER_ID = 1;
    public static final String FIRST_USER_USERNAME = "User_1";

    @Autowired
    private UserRepository userRepository;

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
        var optionalUser = userRepository.findByUserName(FIRST_USER_USERNAME);
        var expectedUser = entityManager.find(User.class, FIRST_USER_ID);

        assertThat(optionalUser).isPresent().get().isEqualTo(expectedUser);
        System.out.println(optionalUser.get());
    }

    @DisplayName("должен сохранять нового читателя")
    @Test
    void shouldCreateNewUser() {
        User user = new User();
        user.setUserName("New User");

        var savedUser = userRepository.save(user);
        assertThat(savedUser.getId()).isGreaterThan(0);
        assertThat(savedUser.getUserName()).isEqualTo(user.getUserName());

    }

    @DisplayName("должен удалять читателя по его id")
    @Test
    void shouldDeleteUser() {
        userRepository.deleteById(FIRST_USER_ID);
        assertThat(entityManager.find(User.class, FIRST_USER_ID)).isNull();

    }
}

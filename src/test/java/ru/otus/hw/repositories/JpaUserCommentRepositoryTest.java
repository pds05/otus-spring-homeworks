package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями книг")
@DataJpaTest
@Import({JpaUserCommentRepository.class})
public class JpaUserCommentRepositoryTest {

    @Autowired
    private JpaUserCommentRepository jpaUserCommentRepository;

    @Autowired
    private TestEntityManager entityManager;


    @DisplayName("должен добавлять комментарий к книге")
    @Test
    void shouldCreateNewUserCommentsForBook() {
        User user1 = entityManager.find(User.class, 1L);
        Book book1 = entityManager.find(Book.class, 1L);
        UserComment expectedComment = createUserComment(user1, book1);
        UserComment returnedComment = jpaUserCommentRepository.save(expectedComment);

        assertThat(returnedComment)
                .isNotNull().matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        Book book2 = entityManager.find(Book.class, 2L);
        expectedComment = createUserComment(user1, book2);
        jpaUserCommentRepository.save(expectedComment);

        User user2 = entityManager.find(User.class, 2L);
        expectedComment = createUserComment(user2, book2);
        jpaUserCommentRepository.save(expectedComment);

        List<UserComment> allBooksComments = jpaUserCommentRepository.getAllUserCommentsByBookId(book1.getId());
        assertThat(allBooksComments).size().isEqualTo(1);

        allBooksComments = jpaUserCommentRepository.getAllUserCommentsByBookId(book2.getId());
        assertThat(allBooksComments).size().isEqualTo(2);

        List<UserComment> allUserComments = jpaUserCommentRepository.getAllUserCommentsByUserId(user1.getId());
        assertThat(allUserComments).size().isEqualTo(2);

        allUserComments = jpaUserCommentRepository.getAllUserCommentsByUserId(user2.getId());
        assertThat(allUserComments).size().isEqualTo(1);

    }

    @DisplayName("должен изменять комментарий к книге")
    @Test
    void shouldUpdateUserComment() {
        User user1 = entityManager.find(User.class, 1L);
        Book book1 = entityManager.find(Book.class, 1L);
        UserComment expectedComment = createUserComment(user1, book1);
        UserComment returnedComment = jpaUserCommentRepository.save(expectedComment);

        assertThat(returnedComment)
                .isNotNull().matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);
        String updatedUserCommentText = "edited: " + expectedComment.getText();

        returnedComment.setText(updatedUserCommentText);
        jpaUserCommentRepository.save(returnedComment);

        UserComment actualUserComment = entityManager.find(UserComment.class, 1L);
        assertThat(actualUserComment).usingRecursiveComparison().isEqualTo(returnedComment);
        assertThat(actualUserComment.getText()).isEqualTo(updatedUserCommentText);

    }

    @DisplayName("должен удалять комментарий к книге")
    @Test
    void shouldDeleteUserComment() {
        User user = entityManager.find(User.class, 1L);
        Book book = entityManager.find(Book.class, 1L);
        UserComment userComment = createUserComment(user, book);
        jpaUserCommentRepository.save(userComment);

        assertThat(userComment).matches(comment -> comment.getId() > 0);

        jpaUserCommentRepository.deleteById(userComment.getId());

        assertThat(jpaUserCommentRepository.getUserCommentById(userComment.getId())).isEmpty();

    }

    private UserComment createUserComment(User user, Book book) {
        UserComment comment = new UserComment();
        comment.setUser(user);
        comment.setBook(book);
        comment.setText(user.getUserName() + " comments the book " + book.getTitle());
        return comment;
    }


}

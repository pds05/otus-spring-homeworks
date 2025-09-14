package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями книг")
@DataJpaTest
@Import({JpaUserCommentRepository.class})
public class JpaUserCommentRepositoryTest {

    private static final Map<Book, AtomicInteger> BOOKS_COUNTER = new HashMap<>();

    @Autowired
    private JpaUserCommentRepository jpaUserCommentRepository;

    @Autowired
    private TestEntityManager entityManager;


    @DisplayName("должен добавлять комментарий к книге")
    @Test
    void shouldCreateNewUserCommentsForBook() {
        Book book1 = entityManager.find(Book.class, 1L);
        UserComment expectedComment = createUserComment(book1);
        UserComment returnedComment = jpaUserCommentRepository.save(expectedComment);

        assertThat(returnedComment)
                .isNotNull().matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);
    }

    @DisplayName("должен возвращать все комментарии к книге")
    @Test
    void shouldReturnUserCommentsOfBook() {
        Book book1 = entityManager.find(Book.class, 1L);
        entityManager.persist(createUserComment(book1));
        entityManager.persist(createUserComment(book1));
        entityManager.persist(createUserComment(book1));

        Book book2 = entityManager.find(Book.class, 2L);
        entityManager.persist(createUserComment(book2));
        entityManager.persist(createUserComment(book2));

        List<UserComment> allBooksComments = jpaUserCommentRepository.getAllUserCommentsByBookId(book1.getId());
        assertThat(allBooksComments).size().isEqualTo(3);

        allBooksComments = jpaUserCommentRepository.getAllUserCommentsByBookId(book2.getId());
        assertThat(allBooksComments).size().isEqualTo(2);

    }

    @DisplayName("должен изменять комментарий к книге")
    @Test
    void shouldUpdateUserComment() {
        Book book1 = entityManager.find(Book.class, 1L);
        UserComment expectedComment = createUserComment(book1);
        UserComment returnedComment = entityManager.persist(expectedComment);

        assertThat(returnedComment)
                .isNotNull().matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);
        String updatedUserCommentText = "edited: " + expectedComment.getText();

        returnedComment.setText(updatedUserCommentText);
        jpaUserCommentRepository.save(returnedComment);

        UserComment actualUserComment = entityManager.find(UserComment.class, returnedComment.getId());
        assertThat(actualUserComment).usingRecursiveComparison().isEqualTo(returnedComment);
        assertThat(actualUserComment.getText()).isEqualTo(updatedUserCommentText);

    }

    @DisplayName("должен удалять комментарий к книге")
    @Test
    void shouldDeleteUserComment() {
        Book book = entityManager.find(Book.class, 1L);
        UserComment userComment = createUserComment(book);
        entityManager.persist(userComment);

        assertThat(userComment).matches(comment -> comment.getId() > 0);

        jpaUserCommentRepository.deleteById(userComment.getId());

        assertThat(entityManager.find(UserComment.class, userComment.getId())).isNull();

    }

    private UserComment createUserComment(Book book) {
        BOOKS_COUNTER.putIfAbsent(book, new AtomicInteger(1));

        UserComment comment = new UserComment();
        comment.setBook(book);
        comment.setText("Comment_" + BOOKS_COUNTER.get(book).getAndIncrement() + " for book " + book.getTitle());
        return comment;
    }

}

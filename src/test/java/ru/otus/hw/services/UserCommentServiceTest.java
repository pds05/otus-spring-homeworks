package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.dtos.UserCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.UserComment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Сервис для работы с комментариями к книгам")
@DataMongoTest
public class UserCommentServiceTest {
    @Autowired
    private UserCommentService userCommentService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("должен добавлять новый комментарий к книге")
    void shouldInsertUserComment() {
        var expectedBook = mongoTemplate.findAll(Book.class).get(0);
        var returnedUserComment = userCommentService.insert("Test comment 1", expectedBook.getId());

        assertThat(returnedUserComment).isNotNull();
        assertFalse(returnedUserComment.id().isBlank());
    }

    @Test
    @DisplayName("должен вернуть комментарий по его идентификатору")
    void shouldReturnUserCommentById() {
        var expectedUserComment = mongoTemplate.findAll(UserComment.class).get(0);
        var returnedUserComment = userCommentService.findById(expectedUserComment.getId());

        assertThat(returnedUserComment).isNotNull();
        assertThat(returnedUserComment.id()).isEqualTo(expectedUserComment.getId());
    }

    @Test
    @DisplayName("должен вернуть все комментарии к книге")
    void shouldReturnUserCommentsByBookId() {
        var expectedBook = mongoTemplate.findAll(Book.class).get(0);
        List<UserCommentDto> userCommentDtos = userCommentService.findAllByBookId(expectedBook.getId());

        assertThat(userCommentDtos).isNotEmpty();
        assertTrue(userCommentDtos.stream().anyMatch(uc -> uc.text().startsWith("Test comment 1")));
    }

    @Test
    @DisplayName("должен изменить комментарий к книге")
    void shouldUpdateUserComment() {
        var expectedUserComments = mongoTemplate.findAll(UserComment.class);
        var expectedUserComment = expectedUserComments.stream().filter(uc -> uc.getText().startsWith("Test comment 1")).findFirst().get();
        var updatedUserComment = userCommentService.update(expectedUserComment.getId(), "Edited: " + expectedUserComment.getText());

        assertThat(expectedUserComment.getId()).isEqualTo(updatedUserComment.id());
        assertThat(expectedUserComment).isNotEqualTo(updatedUserComment);
        assertThat(updatedUserComment.text()).startsWith("Edited");
    }

    @Test
    @DisplayName("должен удалять комментарий по его идентификатору")
    void shouldDeleteUserComment() {
        var expectedUserComments = mongoTemplate.findAll(UserComment.class);
        var expectedUserComment = expectedUserComments.stream().filter(uc -> uc.getText().contains("Test comment 1")).findFirst().get();
        userCommentService.deleteById(expectedUserComment.getId());

        assertThrows(EntityNotFoundException.class, () -> userCommentService.findById(expectedUserComment.getId()));
    }
}

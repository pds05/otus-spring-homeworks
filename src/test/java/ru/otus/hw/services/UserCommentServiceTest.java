package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.dtos.UserCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.UserComment;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Сервис для работы с комментариями к книгам")
@DataMongoTest
@ComponentScan(basePackages = {"ru.otus.hw.services", "ru.otus.hw.converters"})
public class UserCommentServiceTest {
    @Autowired
    private UserCommentService userCommentService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("должен добавлять новый комментарий к книге")
    void shouldInsertUserComment() {
        var expectedBook = mongoTemplate.findAll(Book.class).get(0);
        var returnedUserComment = userCommentService.insert("New Test comment", expectedBook.getId());

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
        var expectedUserComment = mongoTemplate.findAll(UserComment.class).get(0);
        List<UserCommentDto> userCommentDtos = userCommentService.findAllByBookId(expectedUserComment.getBook().getId());

        assertThat(userCommentDtos).isNotEmpty();
        assertTrue(userCommentDtos.stream().anyMatch(uc -> uc.text().contains("Test comment")));
    }

    @Test
    @DisplayName("должен изменить комментарий к книге")
    void shouldUpdateUserComment() {
        var expectedUserComment = mongoTemplate.findOne(Query.query(Criteria.where("text").is("Test comment 2-1")), UserComment.class);
        assertNotNull(expectedUserComment);

        var updatedUserComment = userCommentService.update(expectedUserComment.getId(), "Edited " + expectedUserComment.getText());

        assertThat(expectedUserComment.getId()).isEqualTo(updatedUserComment.id());
        assertThat(expectedUserComment).isNotEqualTo(updatedUserComment);
        assertThat(updatedUserComment.text()).startsWith("Edited");
    }

    @Test
    @DisplayName("должен удалять комментарий по его идентификатору")
    void shouldDeleteUserComment() {
        var expectedUserComment = mongoTemplate.findOne(Query.query(Criteria.where("text").is("Test comment 3-1")), UserComment.class);
        assertNotNull(expectedUserComment);
        userCommentService.deleteById(expectedUserComment.getId());

        assertThrows(EntityNotFoundException.class, () -> userCommentService.findById(expectedUserComment.getId()));
    }
}

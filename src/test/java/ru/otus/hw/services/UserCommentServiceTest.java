package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.UserCommentConverter;
import ru.otus.hw.dtos.UserCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Сервис для работы с комментариями к книгам")
@SpringBootTest
public class UserCommentServiceTest {

    @Autowired
    private UserCommentService userCommentService;
    @Autowired
    private UserCommentConverter userCommentConverter;

    @Transactional
    @Test
    @DisplayName("должен добавлять новый комментарий к книге")
    void shouldInsertUserComment() {
        var returnedUserComment = userCommentService.insert("Test comment 1", 1);

        assertThat(returnedUserComment).isNotNull();
        assertThat(returnedUserComment.id()).isGreaterThan(0);

        System.out.println(userCommentConverter.userCommentDtoToString(returnedUserComment));

    }

    @Transactional
    @Test
    @DisplayName("должен вернуть комментарий по его идентификатору")
    void shouldInsertAndReturnUserCommentById() {
        var expectedUserComment = userCommentService.insert("Test comment 1", 1);
        var returnedUserComment = userCommentService.findById(expectedUserComment.id());

        assertThat(returnedUserComment).isNotNull();
        assertThat(returnedUserComment).isEqualTo(expectedUserComment);

        System.out.println(userCommentConverter.userCommentDtoToString(returnedUserComment));

    }

    @Transactional
    @Test
    @DisplayName("должен вернуть все комментарии к книге")
    void shouldReturnUserCommentsByBookId() {
        userCommentService.insert("Test comment 1", 1);
        userCommentService.insert("Test comment 2", 1);

        List<UserCommentDto> userCommentDtos = userCommentService.findAllByBookId(1);
        assertThat(userCommentDtos).isNotEmpty().hasSize(2);

        userCommentDtos.forEach(uc -> System.out.println(
                userCommentConverter.userCommentDtoToString(uc)));

    }

    @Transactional
    @Test
    @DisplayName("должен изменить комментарий к книге")
    void shouldUpdateUserComment() {
        var expectedUserComment = userCommentService.insert("Test comment 1", 1);
        var updatedUserComment = userCommentService.update(expectedUserComment.id(), "Edited: " + expectedUserComment.text());

        assertThat(expectedUserComment.id()).isEqualTo(updatedUserComment.id());
        assertThat(expectedUserComment).isNotEqualTo(updatedUserComment);
        assertThat(updatedUserComment.text()).startsWith("Edited");

        System.out.println(userCommentConverter.userCommentDtoToString(updatedUserComment));

    }

    @Transactional
    @Test
    @DisplayName("должен удалять комментарий по его идентификатору")
    void shouldDeleteUserComment() {
        var expectedUserComment = userCommentService.insert("Test comment 1", 1);
        userCommentService.deleteById(expectedUserComment.id());

        assertThrows(EntityNotFoundException.class, () -> userCommentService.findById(expectedUserComment.id()));
    }
}
